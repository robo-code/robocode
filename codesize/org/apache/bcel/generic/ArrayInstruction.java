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

/**
 * Super class for instructions dealing with array access such as IALOAD.
 *
 * @version $Id: ArrayInstruction.java,v 1.1 2007/04/22 08:32:30 fnl Exp $
 * @author  <A HREF="mailto:m.dahm@gmx.de">M. Dahm</A>
 */
public abstract class ArrayInstruction extends Instruction implements ExceptionThrower,
        TypedInstruction {

    /**
     * Empty constructor needed for the Class.newInstance() statement in
     * Instruction.readInstruction(). Not to be used otherwise.
     */
    ArrayInstruction() {
    }


    /**
     * @param opcode of instruction
     */
    protected ArrayInstruction(short opcode) {
        super(opcode, (short) 1);
    }


    public Class[] getExceptions() {
        return org.apache.bcel.ExceptionConstants.EXCS_ARRAY_EXCEPTION;
    }


    /** @return type associated with the instruction
     */
    public Type getType( ConstantPoolGen cp ) {
        switch (opcode) {
            case org.apache.bcel.Constants.IALOAD:
            case org.apache.bcel.Constants.IASTORE:
                return Type.INT;
            case org.apache.bcel.Constants.CALOAD:
            case org.apache.bcel.Constants.CASTORE:
                return Type.CHAR;
            case org.apache.bcel.Constants.BALOAD:
            case org.apache.bcel.Constants.BASTORE:
                return Type.BYTE;
            case org.apache.bcel.Constants.SALOAD:
            case org.apache.bcel.Constants.SASTORE:
                return Type.SHORT;
            case org.apache.bcel.Constants.LALOAD:
            case org.apache.bcel.Constants.LASTORE:
                return Type.LONG;
            case org.apache.bcel.Constants.DALOAD:
            case org.apache.bcel.Constants.DASTORE:
                return Type.DOUBLE;
            case org.apache.bcel.Constants.FALOAD:
            case org.apache.bcel.Constants.FASTORE:
                return Type.FLOAT;
            case org.apache.bcel.Constants.AALOAD:
            case org.apache.bcel.Constants.AASTORE:
                return Type.OBJECT;
            default:
                throw new ClassGenException("Oops: unknown case in switch" + opcode);
        }
    }
}
