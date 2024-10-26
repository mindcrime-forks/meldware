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
package org.buni.meldware.mail;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.buni.meldware.mail.abmounts.TestABMounts;
import org.buni.meldware.mail.domaingroup.TestDomainGroup;
import org.buni.meldware.mail.imap4.TestIMAP2;
import org.buni.meldware.mail.imap4.parser.TestLexer;
import org.buni.meldware.mail.mailbox.TestMailbox;
import org.buni.meldware.mail.mailbox.search.TestSearch;
import org.buni.meldware.mail.maillist.hn.TestHnMailListManager;
import org.buni.meldware.mail.message.TestMail;
import org.buni.meldware.mail.message.TestMailAddress;
import org.buni.meldware.mail.message.TestMailBodyManager;
import org.buni.meldware.mail.message.TestMailHeaders;
import org.buni.meldware.mail.message.TestMessageSize;
import org.buni.meldware.mail.message.TestMimeParser;
import org.buni.meldware.mail.pop3.TestPOP;
import org.buni.meldware.mail.smtp.TestSMTPProtocol;
import org.buni.meldware.mail.smtp.handlers.TestCmdAUTH;
import org.buni.meldware.mail.smtp.handlers.TestCmdDATA;
import org.buni.meldware.mail.smtp.handlers.TestCmdEHLO;
import org.buni.meldware.mail.smtp.handlers.TestCmdEXPN;
import org.buni.meldware.mail.smtp.handlers.TestCmdHELO;
import org.buni.meldware.mail.smtp.handlers.TestCmdMAIL;
import org.buni.meldware.mail.smtp.handlers.TestCmdNOOP;
import org.buni.meldware.mail.smtp.handlers.TestCmdQUIT;
import org.buni.meldware.mail.smtp.handlers.TestCmdRCPT;
import org.buni.meldware.mail.smtp.handlers.TestCmdRSET;
import org.buni.meldware.mail.smtp.handlers.TestCmdSTARTTLS;
import org.buni.meldware.mail.store.TestStore;
import org.buni.meldware.mail.userrepository.TestStaticUserRepository;
import org.buni.meldware.mail.util.io.TestDotStuffingCopier;
import org.buni.meldware.mail.util.io.TestDotStuffingLineLimitedCopier;
import org.buni.meldware.mail.util.io.TestDotUnstuffingCopier;
import org.buni.meldware.mail.util.io.TestIOUtil;

/**
 * @author Michael.Barker
 *
 */
public class BaseUnitTestSuite {

    public static Test suite() {
        TestSuite ts = new TestSuite("Base Unit Test Suite");
        ts.addTest(TestABMounts.suite());
        ts.addTest(TestServer.suite());
        ts.addTest(TestMailListenerChain.suite());
        ts.addTest(TestStore.suite());
        ts.addTest(TestDomainGroup.suite());
        //ts.addTest(TestIMAPProtocol.suite());
        ts.addTest(TestMail.suite());
        ts.addTest(TestMessageSize.suite());
        ts.addTest(TestMailAddress.suite());
        ts.addTest(TestMailHeaders.suite());
        ts.addTest(TestMailBodyManager.suite());
        ts.addTest(TestHnMailListManager.suite());
        // Unsupported at the moment.
        //ts.addTest(TestConnection.suite());
        ts.addTest(TestSMTPProtocol.suite());
        ts.addTest(TestMailbox.suite());
        ts.addTest(TestSearch.suite());
        ts.addTest(TestCmdAUTH.suite());
        ts.addTest(TestCmdDATA.suite());
        ts.addTest(TestCmdEHLO.suite());
        ts.addTest(TestCmdEXPN.suite());
        ts.addTest(TestCmdHELO.suite());
        ts.addTest(TestCmdMAIL.suite());
        ts.addTest(TestCmdNOOP.suite());
        ts.addTest(TestCmdQUIT.suite());
        ts.addTest(TestCmdRCPT.suite());
        ts.addTest(TestCmdRSET.suite());
        ts.addTest(TestCmdSTARTTLS.suite());
        ts.addTest(TestPOP.suite());
        ts.addTest(TestStaticUserRepository.suite());
        ts.addTest(new TestSuite(TestLexer.class));
        ts.addTest(new TestSuite(TestMimeParser.class));
        ts.addTest(TestIMAP2.suite());
        ts.addTest(new TestSuite(TestDotStuffingCopier.class));
        ts.addTest(new TestSuite(TestDotStuffingLineLimitedCopier.class));
        ts.addTest(new TestSuite(TestDotUnstuffingCopier.class));
        ts.addTest(new TestSuite(TestIOUtil.class));
        return ts;
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(BaseUnitTestSuite.suite());
    }
}
