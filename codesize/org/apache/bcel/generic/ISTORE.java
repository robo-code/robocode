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
 * ISTORE - Store int from stack into local variable
 * <PRE>Stack: ..., value -&gt; ... </PRE>
 *
 * @version $Id: ISTORE.java,v 1.1 2007/04/22 08:32:27 fnl Exp $
 * @author  <A HREF="mailto:m.dahm@gmx.de">M. Dahm</A>
 */
public class ISTORE extends StoreInstruction {

    /**
     * Empty constructor needed for the Class.newInstance() statement in
     * Instruction.readInstruction(). Not to be used otherwise.
     */
    ISTORE() {
        super(org.apache.bcel.Constants.ISTORE, org.apache.bcel.Constants.ISTORE_0);
    }


    /** Store int into local variable
     * @param n index of local variable
     */
    public ISTORE(int n) {
        super(org.apache.bcel.Constants.ISTORE, org.apache.bcel.Constants.ISTORE_0, n);
    }


    /**
     * Call corresponding visitor method(s). The order is:
     * Call visitor methods of implemented interfaces first, then
     * call methods according to the class hierarchy in descending order,
     * i.e., the most specific visitXXX() call comes last.
     *
     * @param v Visitor object
     */
    public void accept( Visitor v ) {
        super.accept(v);
        v.visitISTORE(this);
    }
}
