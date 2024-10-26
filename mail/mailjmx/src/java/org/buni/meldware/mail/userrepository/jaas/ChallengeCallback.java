package org.buni.meldware.mail.userrepository.jaas;

import javax.security.auth.callback.Callback;

public class ChallengeCallback implements Callback {

    private final byte[] challenge;

    public ChallengeCallback(byte[] challenge) {
        this.challenge = challenge;
    }
    
    /**
     * @return the challenge
     */
    public byte[] getChallenge() {
        return challenge;
    }

}
