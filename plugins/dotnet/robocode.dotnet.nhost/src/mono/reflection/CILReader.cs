//
// MethodBaseRocks.cs
//
// Author:
// Jb Evain (jbevain@novell.com)
//
// (C) 2009 Novell, Inc. (http://www.novell.com)
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

using System;
using System.Collections.Generic;
using System.Reflection;
using System.Reflection.Emit;

namespace Mono.Reflection
{
    public sealed class Instruction
    {
        private readonly int offset;
        private OpCode opcode;
        private object operand;

        public int Offset
        {
            get { return offset; }
        }

        public OpCode OpCode
        {
            get { return opcode; }
        }

        public object Operand
        {
            get { return operand; }
            internal set { operand = value; }
        }

        public Instruction Previous { get; internal set; }

        public Instruction Next { get; internal set; }

        internal Instruction(int offset, OpCode opcode)
        {
            this.offset = offset;
            this.opcode = opcode;
        }

        public int GetSize()
        {
            int size = opcode.Size;

            switch (opcode.OperandType)
            {
                case OperandType.InlineSwitch:
                    size += (1 + ((int[]) operand).Length)*4;
                    break;
                case OperandType.InlineI8:
                case OperandType.InlineR:
                    size += 8;
                    break;
                case OperandType.InlineBrTarget:
                case OperandType.InlineField:
                case OperandType.InlineI:
                case OperandType.InlineMethod:
                case OperandType.InlineString:
                case OperandType.InlineTok:
                case OperandType.InlineType:
                case OperandType.ShortInlineR:
                    size += 4;
                    break;
                case OperandType.InlineVar:
                    size += 2;
                    break;
                case OperandType.ShortInlineBrTarget:
                case OperandType.ShortInlineI:
                case OperandType.ShortInlineVar:
                    size += 1;
                    break;
            }

            return size;
        }

        public override string ToString()
        {
            return opcode.Name;
        }
    }

    internal class MethodBodyReader
    {
        private static readonly OpCode[] one_byte_opcodes;
        private static readonly OpCode[] two_bytes_opcodes;

        static MethodBodyReader()
        {
            one_byte_opcodes = new OpCode[0xe1];
            two_bytes_opcodes = new OpCode[0x1f];

            var fields = GetOpCodeFields();

            foreach (FieldInfo t in fields)
            {
                var opcode = (OpCode) t.GetValue(null);
                if (opcode.OpCodeType == OpCodeType.Nternal)
                    continue;

                if (opcode.Size == 1)
                    one_byte_opcodes[opcode.Value] = opcode;
                else
                    two_bytes_opcodes[opcode.Value & 0xff] = opcode;
            }
        }

        private static IEnumerable<FieldInfo> GetOpCodeFields()
        {
            return typeof (OpCodes).GetFields(BindingFlags.Public | BindingFlags.Static);
        }

        private class ByteBuffer
        {
            internal readonly byte[] buffer;
            internal int position;

            public ByteBuffer(byte[] buffer)
            {
                this.buffer = buffer;
            }

            public byte ReadByte()
            {
                CheckCanRead(1);
                return buffer[position++];
            }

            private byte[] ReadBytes(int length)
            {
                CheckCanRead(length);
                var value = new byte[length];
                Buffer.BlockCopy(buffer, position, value, 0, length);
                position += length;
                return value;
            }

            public short ReadInt16()
            {
                CheckCanRead(2);
                short value = (short) (buffer[position]
                                       | (buffer[position + 1] << 8));
                position += 2;
                return value;
            }

            public int ReadInt32()
            {
                CheckCanRead(4);
                int value = buffer[position]
                            | (buffer[position + 1] << 8)
                            | (buffer[position + 2] << 16)
                            | (buffer[position + 3] << 24);
                position += 4;
                return value;
            }

            public long ReadInt64()
            {
                CheckCanRead(8);
                uint low = (uint) (buffer[position]
                                   | (buffer[position + 1] << 8)
                                   | (buffer[position + 2] << 16)
                                   | (buffer[position + 3] << 24));

                uint high = (uint) (buffer[position + 4]
                                    | (buffer[position + 5] << 8)
                                    | (buffer[position + 6] << 16)
                                    | (buffer[position + 7] << 24));

                long value = (((long) high) << 32) | low;
                position += 8;
                return value;
            }

            public float ReadSingle()
            {
                if (!BitConverter.IsLittleEndian)
                {
                    var bytes = ReadBytes(4);
                    Array.Reverse(bytes);
                    return BitConverter.ToSingle(bytes, 0);
                }

                CheckCanRead(4);
                float value = BitConverter.ToSingle(buffer, position);
                position += 4;
                return value;
            }

            public double ReadDouble()
            {
                if (!BitConverter.IsLittleEndian)
                {
                    var bytes = ReadBytes(8);
                    Array.Reverse(bytes);
                    return BitConverter.ToDouble(bytes, 0);
                }

                CheckCanRead(8);
                double value = BitConverter.ToDouble(buffer, position);
                position += 8;
                return value;
            }

            private void CheckCanRead(int count)
            {
                if (position + count > buffer.Length)
                    throw new ArgumentOutOfRangeException();
            }
        }

        private readonly MethodBase method;
        private readonly MethodBody body;
        private readonly Module module;
        private readonly Type[] generic_type_arguments;
        private readonly Type[] method_arguments;
        private readonly ByteBuffer il;
        private readonly ParameterInfo[] parameters;
        private readonly IList<LocalVariableInfo> locals;
        private readonly List<Instruction> instructions = new List<Instruction>();

        private MethodBodyReader(MethodBase method, MethodBody mb)
        {
            this.method = method;

            body = mb;
            if (body == null)
                throw new ArgumentException();

            var bytes = body.GetILAsByteArray();
            if (bytes == null)
                throw new ArgumentException();

            if (!(method is ConstructorInfo))
                method_arguments = method.GetGenericArguments();

            if (method.DeclaringType != null)
                generic_type_arguments = method.DeclaringType.GetGenericArguments();

            parameters = method.GetParameters();
            locals = body.LocalVariables;
            module = method.Module;
            il = new ByteBuffer(bytes);
        }

        private void ReadInstructions()
        {
            Instruction previous = null;

            while (il.position < il.buffer.Length)
            {
                var instruction = new Instruction(il.position, ReadOpCode());

                ReadOperand(instruction);

                if (previous != null)
                {
                    instruction.Previous = previous;
                    previous.Next = instruction;
                }

                instructions.Add(instruction);
                previous = instruction;
            }
        }

        private void ReadOperand(Instruction instruction)
        {
            switch (instruction.OpCode.OperandType)
            {
                case OperandType.InlineNone:
                    break;
                case OperandType.InlineSwitch:
                    int length = il.ReadInt32();
                    int[] branches = new int[length];
                    int[] offsets = new int[length];
                    for (int i = 0; i < length; i++)
                        offsets[i] = il.ReadInt32();
                    for (int i = 0; i < length; i++)
                        branches[i] = il.position + offsets[i];

                    instruction.Operand = branches;
                    break;
                case OperandType.ShortInlineBrTarget:
                    instruction.Operand = (sbyte) (il.ReadByte() + il.position);
                    break;
                case OperandType.InlineBrTarget:
                    instruction.Operand = il.ReadInt32() + il.position;
                    break;
                case OperandType.ShortInlineI:
                    if (instruction.OpCode == OpCodes.Ldc_I4_S)
                        instruction.Operand = (sbyte) il.ReadByte();
                    else
                        instruction.Operand = il.ReadByte();
                    break;
                case OperandType.InlineI:
                    instruction.Operand = il.ReadInt32();
                    break;
                case OperandType.ShortInlineR:
                    instruction.Operand = il.ReadSingle();
                    break;
                case OperandType.InlineR:
                    instruction.Operand = il.ReadDouble();
                    break;
                case OperandType.InlineI8:
                    instruction.Operand = il.ReadInt64();
                    break;
                case OperandType.InlineSig:
                    byte[] resolveSignature = module.ResolveSignature(il.ReadInt32());
                    instruction.Operand = resolveSignature;
                    break;
                case OperandType.InlineString:
                    string resolveString = module.ResolveString(il.ReadInt32());
                    instruction.Operand = resolveString;
                    break;
                case OperandType.InlineTok:
                    MemberInfo resolveMember = module.ResolveMember(il.ReadInt32(), generic_type_arguments, method_arguments);
                    instruction.Operand = resolveMember;
                    break;
                case OperandType.InlineType:
                    Type resolveType = module.ResolveType(il.ReadInt32(), generic_type_arguments, method_arguments);
                    instruction.Operand = resolveType;
                    break;
                case OperandType.InlineMethod:
                    MethodBase resolveMethod = module.ResolveMethod(il.ReadInt32(), generic_type_arguments, method_arguments);
                    instruction.Operand = resolveMethod;
                    break;
                case OperandType.InlineField:
                    FieldInfo resolveField = module.ResolveField(il.ReadInt32(), generic_type_arguments, method_arguments);
                    instruction.Operand = resolveField;
                    break;
                case OperandType.ShortInlineVar:
                    object variable = GetVariable(instruction, il.ReadByte());
                    instruction.Operand = variable;
                    break;
                case OperandType.InlineVar:
                    object operand = GetVariable(instruction, il.ReadInt16());
                    instruction.Operand = operand;
                    break;
                default:
                    throw new NotSupportedException();
            }
        }

        private object GetVariable(Instruction instruction, int index)
        {
            if (TargetsLocalVariable(instruction.OpCode))
                return GetLocalVariable(index);
            return GetParameter(index);
        }

        private static bool TargetsLocalVariable(OpCode opcode)
        {
            return opcode.Name.Contains("loc");
        }

        private LocalVariableInfo GetLocalVariable(int index)
        {
            return locals[index];
        }

        private ParameterInfo GetParameter(int index)
        {
            if (!method.IsStatic)
                index--;

            return parameters[index];
        }

        private OpCode ReadOpCode()
        {
            byte op = il.ReadByte();
            return op != 0xfe
                       ? one_byte_opcodes[op]
                       : two_bytes_opcodes[il.ReadByte()];
        }

        public static IList<Instruction> GetInstructions(MethodBase method)
        {
            var mb = method.GetMethodBody();
            if (mb == null)
            {
                return new List<Instruction>();
            }
            var reader = new MethodBodyReader(method, mb);
            reader.ReadInstructions();
            return reader.instructions;
        }

    }
}