/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.buni.meldware.mail.tx;

import org.jboss.aspects.tx.TxType;

/**
 * @author Michael.Barker
 *
 */
public class AOPTxRunner implements TxRunner {

    @org.jboss.aspects.tx.Tx(TxType.REQUIRED)
    public <S> S required(Tx<S> tx) {
        return tx.run();
    }

    @org.jboss.aspects.tx.Tx(TxType.REQUIRESNEW)
    public <S> S requiresNew(Tx<S> tx) {
        return tx.run();
    }

    @org.jboss.aspects.tx.Tx(TxType.REQUIRED)
    public void required(VoidTx tx) {
        tx.run();
    }

    @org.jboss.aspects.tx.Tx(TxType.REQUIRESNEW)
    public void requiresNew(VoidTx tx) {
        tx.run();
    }
}