package org.buni.meldware.mail.smtp.sender;

import java.io.InputStream;
import java.net.InetAddress;
import java.nio.charset.Charset;

import junit.framework.TestCase;

import org.columba.ristretto.auth.AuthenticationFactory;
import org.columba.ristretto.composer.MimeTreeRenderer;
import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.message.Address;
import org.columba.ristretto.message.BasicHeader;
import org.columba.ristretto.message.Header;
import org.columba.ristretto.message.LocalMimePart;
import org.columba.ristretto.message.MimeHeader;
import org.columba.ristretto.message.MimeType;
import org.columba.ristretto.smtp.SMTPProtocol;

public class TestSender extends TestCase {

    public void testSendMail() throws Exception {
        
        Address fromAddress = Address.parse("tom@localhost");
        //Address toAddress = Address.parse("mbarker@bunisoft.com");
        //Address toAddress = Address.parse("mbarker@mail.buni.org");
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
        
    }
}
