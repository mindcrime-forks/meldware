package org.buni.meldware.mail.tx;

public class TxRunnerFactory {

    public static TxRunner create() {
        return new AOPTxRunner();
    }
}
