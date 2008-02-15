/*
 * Copyright  2000-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *
 */
package org.apache.bcel.generic;

import java.util.StringTokenizer;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;

/**
 * Super class for the INVOKExxx family of instructions.
 *
 * @version $Id: InvokeInstruction.java,v 1.1 2007/04/22 08:32:27 fnl Exp $
 * @author  <A HREF="mailto:m.dahm@gmx.de">M. Dahm</A>
 */
public abstract class InvokeInstruction extends FieldOrMethod implements ExceptionThrower,
        TypedInstruction, StackConsumer, StackProducer {

    /**
     * Empty constructor needed for the Class.newInstance() statement in
     * Instruction.readInstruction(). Not to be used otherwise.
     */
    InvokeInstruction() {
    }


    /**
     * @param index to constant pool
     */
    protected InvokeInstruction(short opcode, int index) {
        super(opcode, index);
    }


    /**
     * @return mnemonic for instruction with symbolic references resolved
     */
    public String toString( ConstantPool cp ) {
        Constant c = cp.getConstant(index);
        StringTokenizer tok = new StringTokenizer(cp.constantToString(c));
        return Constants.OPCODE_NAMES[opcode] + " " + tok.nextToken().replace('.', '/')
                + tok.nextToken();
    }


    /**
     * Also works for instructions whose stack effect depends on the
     * constant pool entry they reference.
     * @return Number of words consumed from stack by this instruction
     */
    public int consumeStack( ConstantPoolGen cpg ) {
        String signature = getSignature(cpg);
        Type[] args = Type.getArgumentTypes(signature);
        int sum;
        if (opcode == Constants.INVOKESTATIC) {
            sum = 0;
        } else {
            sum = 1; // this reference
        }
        int n = args.length;
        for (int i = 0; i < n; i++) {
            sum += args[i].getSize();
        }
        return sum;
    }


    /**
     * Also works for instructions whose stack effect depends on the
     * constant pool entry they reference.
     * @return Number of words produced onto stack by this instruction
     */
    public int produceStack( ConstantPoolGen cpg ) {
        return getReturnType(cpg).getSize();
    }


    /** @return return type of referenced method.
     */
    public Type getType( ConstantPoolGen cpg ) {
        return getReturnType(cpg);
    }


    /** @return name of referenced method.
     */
    public String getMethodName( ConstantPoolGen cpg ) {
        return getName(cpg);
    }


    /** @return return type of referenced method.
     */
    public Type getReturnType( ConstantPoolGen cpg ) {
        return Type.getReturnType(getSignature(cpg));
    }


    /** @return argument types of referenced method.
     */
    public Type[] getArgumentTypes( ConstantPoolGen cpg ) {
        return Type.getArgumentTypes(getSignature(cpg));
    }
}
