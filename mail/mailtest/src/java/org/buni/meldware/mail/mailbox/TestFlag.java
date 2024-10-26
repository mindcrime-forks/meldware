package org.buni.meldware.mail.mailbox;

import junit.framework.TestCase;

public class TestFlag extends TestCase {

	public void testFlag() {
		assertEquals("DELETED", org.buni.meldware.mail.api.Folder.FlagType.DELETED.toString());
	}
}
