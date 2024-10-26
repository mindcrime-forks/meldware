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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.charset.Charset;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;

import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.message.LoopDetectedException;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailBodyManager;
import org.buni.meldware.mail.message.MailCreateAdapter;
import org.buni.meldware.mail.message.MailCreateListener;
import org.buni.meldware.mail.smtp.SMTPProtocolInstance;
import org.buni.meldware.mail.smtp.SMTPProtocolMBean;
import org.buni.meldware.test.JMXTestWrapper;
import org.buni.meldware.mail.tx.AOPTxRunner;
import org.buni.meldware.mail.tx.TxRunner;
import org.buni.meldware.mail.tx.VoidTx;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.buni.meldware.mail.util.io.Copier;
import org.buni.meldware.mail.util.io.DotUnstuffingCopier;
import org.columba.ristretto.auth.AuthenticationFactory;
import org.columba.ristretto.composer.MimeTreeRenderer;
import org.columba.ristretto.imap.IMAPProtocol;
import org.columba.ristretto.imap.SequenceSet;
import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.message.Address;
import org.columba.ristretto.message.BasicHeader;
import org.columba.ristretto.message.Header;
import org.columba.ristretto.message.LocalMimePart;
import org.columba.ristretto.message.MailboxInfo;
import org.columba.ristretto.message.MimeHeader;
import org.columba.ristretto.message.MimeType;
import org.columba.ristretto.smtp.SMTPProtocol;

/**
 * @author Michael Barker
 *
 */
public class MailUtil
{
    public static Mail create(String to, String from, String subject, String body) 
    {
        StringBuffer sb = new StringBuffer();
        sb.append("\r\nMessage-ID: <3F328D84.3080108@localhost>\r\n"); 
        sb.append("Date: Thu, 07 Aug 2003 13:33:56 -0400\r\n");
        sb.append("From: " + from + "\r\n");
        sb.append("User-Agent: Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.3) Gecko/20030312\r\n");
        sb.append("X-Accept-Language: en-us, en\r\n");
        sb.append("MIME-Version: 1.0\r\n");
        sb.append("To:  " + to + "\r\n");
        sb.append("Cc: <tweety@localhost>\r\n");
        sb.append("Bcc: <roadrunner@localhost>\r\n");
        sb.append("Subject: " + subject + "\r\n");
        sb.append("Content-Type: text/plain; charset=us-ascii; format=flowed\r\n");
        sb.append("Content-Transfer-Encoding: 7bit\r\n");
        //sb.append(body + "\r\n");
        //sb.append(".\r\n");
        //byte[] bmail = sb.toString().getBytes();
        //System.out.println(new String(bmail));
        //ByteArrayInputStream stream = new ByteArrayInputStream(bmail);
        //stream.mark(2);
        
        return createMail(sb, body, MailAddress.parseSMTPStyle(from, true), 
                new MailAddress[] { MailAddress.parseSMTPStyle(to, false) }, 
                getSMTPInstance());

//        MailBodyManagerMBean mgr = getSMTPInstance().getManager();
//        MailCreateListener mcl = 
//        return Mail.create(mgr, stream, 1000, 
//                MailAddress.parseSMTPStyle(from, true), 
//                new MailAddress[] { MailAddress.parseSMTPStyle(to, false) });
        
    }
    
    public static SMTPProtocolInstance getSMTPInstance()
    {
        try {
            ObjectName oName = new ObjectName("meldware.mail:type=Protocol,name=SMTPProtocol");
            SMTPProtocolMBean smtp = (SMTPProtocolMBean) MMJMXUtil.getMBean(oName, SMTPProtocolMBean.class);
            return smtp.createInstance();            
        } catch (Exception e) {
            throw new MailException(e);
        }
    }
    
    private static void createMailbox() throws Exception {
        MBeanServerConnection server = JMXTestWrapper.getRemoteJBoss();
        ObjectName name = new ObjectName(TestConstants.MAILBOX_SERVICE_MBEAN);
        server.invoke(name, "createMailbox", new Object[] { "tom" }, 
                new String[] { String.class.getName() });
        long mailboxId = (Long) server.invoke(name, "getMailboxIdByAlias", 
                new Object[] { "tom" }, 
                new String[] { String.class.getName() });
        server.invoke(name, "createAlias", 
                new Object[] { mailboxId, "tom@localhost" }, 
                new String[] { long.class.getName(), String.class.getName() });
        System.out.println("Created Mailbox");
    }
    
    public static void sendMail(int num) throws Exception {
        
        for (int i = 0; i < num; i++) {
            Address fromAddress = Address.parse("tom@localhost");
            Address toAddress = Address.parse("tom@localhost");
            
            String body = "Test Body";
            String subject = "Test Subject";
            String user = "tom";
            String pass = "tom";
            
            Header header = new Header();
            BasicHeader basicHeader = new BasicHeader(header);

            basicHeader.setFrom(fromAddress);
            basicHeader.setTo(new Address[] { toAddress });
            basicHeader.setSubject(subject, Charset.forName("ISO-8859-1"));
            basicHeader.set("X-Mailer", "SimpleSMTP example / Ristretto API");
            
            MimeHeader mimeHeader = new MimeHeader(header);
            mimeHeader.set("Mime-Version", "1.0");
            mimeHeader.setMimeType(new MimeType("text", "plain"));
            LocalMimePart root = new LocalMimePart(mimeHeader);
            
            root.setBody(new CharSequenceSource(body));
            
            InputStream messageSource = MimeTreeRenderer.getInstance().renderMimePart( root );
            SMTPProtocol protocol = new SMTPProtocol("localhost", 9025);
            protocol.openPort();            
            
            protocol.ehlo(InetAddress.getLocalHost());
            protocol.auth( AuthenticationFactory.getInstance().getSecurestMethod("AUTH LOGIN PLAIN"), 
                    user, pass.toCharArray() );
            
            protocol.mail(fromAddress);
            protocol.rcpt(toAddress);
            protocol.data(messageSource);
            protocol.quit();
            
            if ((i+1) % (num/10) == 0) {
                System.out.printf("Sent %d Messages\n", (i+1));
            }
        }
    }
    
    public static void getAllMessages() throws Exception {
        IMAPProtocol protocol = new IMAPProtocol("localhost", 9143);

        protocol.openPort();
        protocol.login("tom", "tom".toCharArray());
        MailboxInfo info = protocol.select("INBOX");
        int exists = info.getExists();
        long t0 = System.currentTimeMillis();
        protocol.fetchFlags(new SequenceSet(1, exists));
        for (int i = 1; i <= exists; i++) {
            InputStream in = protocol.fetchMessage(i);
            while (in.read() != -1);
            if (i % exists == 0) {
                System.out.printf("Received %d Messages\n", i);
            }
        }
        long t1 = System.currentTimeMillis();
        protocol.close();
        protocol.logout();
        System.out.println("Time Taken: " + (t1-t0));
    }

    
    public static void main(String[] args) throws Exception {
        int n = 500;
        //createMailbox();
        sendMail(n);
        getAllMessages();
    }
    
    public static MailBodyManager getMailBodyManager() {
        return MMJMXUtil.getMBean("meldware.mail:type=MailBodyManager,name=MailBodyManager", 
                MailBodyManager.class);
    }
    
    public static MailboxService getMailbox() {
        return MMJMXUtil.getMBean(TestConstants.MAILBOX_SERVICE_MBEAN,
                MailboxService.class);
    }

    public static Mail createMail(StringBuffer headers, MailAddress from, 
            MailAddress[] to, SMTPProtocolInstance smtpProtocol) {
        return createMail(headers, "Hello", from, to, smtpProtocol);
    }
    
    public static void clearData() throws Exception {
        InitialContext ctx = new InitialContext();
        final EntityManager em = ((EntityManager) ctx.lookup("java:/EntityManagers/mail"));
        TxRunner txr = new AOPTxRunner();
        txr.requiresNew(new VoidTx() {
            public void run() {
                em.createQuery("DELETE Alias").executeUpdate();
                //em.createQuery("UPDATE MessageBody SET parent = NULL").executeUpdate();
                //em.createQuery("DELETE MessageBody").executeUpdate();
                em.createQuery("DELETE FolderEntry").executeUpdate();
                em.createQuery("DELETE MessageData").executeUpdate();
                em.createQuery("DELETE ListMember").executeUpdate();
                em.createQuery("DELETE MailListDO").executeUpdate();
                em.createQuery("UPDATE Mailbox SET defaultInFolder = NULL, defaultOutFolder = NULL").executeUpdate();
                em.createQuery("UPDATE Folder SET parent = NULL, mailbox = NULL").executeUpdate();
                //em.createQuery("DELETE FolderSequence").executeUpdate();
                em.createQuery("DELETE Mailbox").executeUpdate();
                em.createQuery("DELETE Folder").executeUpdate();                
            }
        });
    }
    
    
    /** 
     * Takes a set of headers and creates a mail object with a standard body
     * 
     * @param headers The headers
     * @param from The sender
     * @param to The receivers
     * @param smtpProtocol Initialised SMTPProtocolInstance
     * @return
     */
    public static Mail createMail(StringBuffer headers, String body, 
            MailAddress from, MailAddress[] to, 
            SMTPProtocolInstance smtpProtocol) 
    throws LoopDetectedException {
    	headers.append("\r\n");
        headers.append(body);
    	headers.append("\r\n.\r\n");
    
    	byte[] bytes = headers.toString().getBytes();
    	InputStream in = new ByteArrayInputStream(bytes);
    
        in = new BufferedInputStream(in);
        MailCreateListener mcl = new TestCreateAdapter(from, to);
        Mail mail = Mail.create(smtpProtocol.getManager(), in, mcl);
        
    	return mail;
    }
    
    public static class TestCreateAdapter extends MailCreateAdapter {
        
        private MailAddress from;
        private MailAddress[] to;

        public TestCreateAdapter(MailAddress from, MailAddress[] to) {
            this.from = from;
            this.to = to;
        }
        
        public Copier getCopier() {
            return new DotUnstuffingCopier();
        }
        
        public MailAddress getFrom() {
            return from;
        }
        
        public MailAddress[] getTo() {
            return to;
        }
    }
    
}
