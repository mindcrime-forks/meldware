package org.buni.meldware.mail.userrepository.jaas;

import javax.security.auth.callback.Callback;

public class ResponseCallback implements Callback {

    private byte[] response;

    /**
     * @return the response
     */
    public byte[] getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(byte[] response) {
        this.response = response;
    }
}
