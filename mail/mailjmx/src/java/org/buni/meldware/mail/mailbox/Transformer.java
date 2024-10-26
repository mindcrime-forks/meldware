package org.buni.meldware.mail.mailbox;

public interface Transformer<A,B> {

	public B transform(A a);
}
