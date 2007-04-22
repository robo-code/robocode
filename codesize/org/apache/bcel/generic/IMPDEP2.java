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
 * IMPDEP2 - Implementation dependent
 *
 * @version $Id: IMPDEP2.java,v 1.1 2007/04/22 08:32:32 fnl Exp $
 * @author  <A HREF="mailto:m.dahm@gmx.de">M. Dahm</A>
 */
public class IMPDEP2 extends Instruction {

    public IMPDEP2() {
        super(org.apache.bcel.Constants.IMPDEP2, (short) 1);
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
        v.visitIMPDEP2(this);
    }
}
