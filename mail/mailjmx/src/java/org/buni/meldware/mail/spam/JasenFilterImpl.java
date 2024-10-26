package org.buni.meldware.mail.spam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.buni.filestore.util.Pair;
import org.buni.meldware.common.logging.Log;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.api.FolderMessage;
import org.buni.meldware.mail.mailbox.MailboxService;
import org.buni.meldware.mail.mailbox.MessageBody;
import org.buni.meldware.mail.mailbox.MessageData;
import org.buni.meldware.mail.mailbox.Transformer;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.MailHeaders;
import org.buni.meldware.mail.message.Message;
import org.buni.meldware.mail.util.XMLUtil;
import org.buni.meldware.mail.util.io.IOUtil;
import org.buni.panto.BodyHeader;
import org.buni.panto.ContentType;
import org.buni.panto.HeaderParser;
import org.buni.panto.MimeParser;
import org.jasen.core.JasenModule;
import org.jasen.core.engine.Jasen;
import org.jasen.core.engine.JasenMap;
import org.jasen.core.engine.JasenTrainer;
import org.jasen.error.JasenException;
import org.jasen.interfaces.JasenMessage;
import org.jasen.interfaces.JasenScanResult;
import org.w3c.dom.Element;

/**
 * Scans emails using the jASEN anti-spam tool.  Uses a ContentHandler
 * to strip out the text parts of an email message.
 * 
 * @author Michael Barker
 *
 */
public class JasenFilterImpl implements JasenFilter {

    private final static Log log = Log.getLog(JasenFilterImpl.class);
    private volatile Jasen jasen;
    private Map<String,String> config;
    private File dataDir = null;
	private MailboxService mailboxService;
    
    public Message send(Message message) {
        Mail mail = (Mail)message;
        
        try {
            double d = scan(mail);
            DecimalFormat f = new DecimalFormat("0.00");
            String prob = f.format(d);
            mail.getMailHeaders().addHeader("X-meldware-spam-score", prob);
            if (d >= 0.9) {
                mail.setSpamState(FolderMessage.SpamState.SPAM);
            } else if (d <= 0.1) {
                mail.setSpamState(FolderMessage.SpamState.NOT_SPAM);
            }
        } catch (Exception e) {
            log.warn(e, "Failed to scan message");
            e.printStackTrace();
        }
        
        return mail;
    }
    
    public double scan(Mail mail) {
        try {
            MailHeaders header = mail.getMailHeaders();
            MimeMessage mm = extractHeader(header);
            JasenTextExtractHandler h = new JasenTextExtractHandler();
            MimeParser p = new MimeParser();
            p.setContentHandler(h);
            p.parse(mail.getRawStream(mailboxService.getBodyManager()));
            InternetAddress ia = new InternetAddress(mail.getSender().toSMTPString());
            JasenMessage msg = new SimpleJasenMessage(h.getText(), 
                    h.getHtml(), header.getFrom(), header.getSubject(), 
                    h.getAttachmentNames(), ia);
            log.debug("Scanning message from: %s", header.getFrom());
            JasenScanResult result = jasen.scan(mm, msg, new String[0]);
            return result.getProbability();
        } catch (MessagingException e) {
            throw new MailException(e);
        } catch (IOException e) {
            throw new MailException(e);
        } catch (JasenException e) {
            throw new MailException(e);
        }
    }
    
    private MimeMessage extractHeader(MailHeaders headers) throws MessagingException {
        MimeMessage mm = new MimeMessage((Session) null);
        for (String line : headers) {
            mm.addHeaderLine(line);
        }
        return mm;
    }
    
    public void start() {
        log.info("Initialising jASEN");
        try {
            InputStream in = getDataFile();
            jasen = JasenModule.create(config, in);
        } catch (Exception e) {
            throw new MailException("Failed to load datafile", e);
        }
    }
    
    public void stop() throws JasenException {
        log.info("Stopping jASEN");
        jasen = null;
    }
    
    public void setJasenConfig(Element e) {
        config = XMLUtil.toProperties(e);
    }
    
    public void setMailboxService(MailboxService mailboxService) {
        this.mailboxService = mailboxService;
    }
    
    public MailboxService getMailboxService() {
    	return this.mailboxService;
    }
    
    public void setDataDirectory(String dirName) {
        File f = new File(dirName);
        if (!f.exists()) {
            log.info("Creating spam data directory: %s", f.getAbsolutePath());
            f.mkdirs();
        }
        if (f.exists() && f.isDirectory()) {
            dataDir = f;
        }
    }
    
    private File getDataDir() {
        if (dataDir != null) {
            return dataDir;
        } else {
            File dir = new File(System.getProperty("java.io.tmpdir") 
                    + File.separator + "spamdata");
            dir.mkdirs();
            return dir;
        }
    }
    
    private InputStream getDataFile() throws IOException {
        File dir = getDataDir();
        
        File[] dataFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".dat");
            }
        });
        
        InputStream in;
        if (dataFiles.length > 0) {
            File dataFile = null;
            for (File f : dataFiles) {
                if (dataFile == null 
                        || f.lastModified() > dataFile.lastModified()) {
                    dataFile = f;
                }
            }
            log.info("Using datafile: %s", dataFile.getAbsolutePath());
            in = new FileInputStream(dataFile);
        } else {
            log.info("No data files found creating default");
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            in = cl.getResourceAsStream("jasen.dat");
        }
        return in;
    }
    
    public void train() {
    	
        ObjectOutputStream out = null;
    	try {
            log.info("Jasen training started.");
            
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream in = cl.getResourceAsStream("jasen.dat");
            
        	JasenTrainer trainer = JasenModule.createTrainer(config, in);
        	JasenMessageTransformer t = new JasenMessageTransformer(mailboxService);
        	List<Pair<MimeMessage,SimpleJasenMessage>> nonSpam = mailboxService.getMessages(t,
        			FolderMessage.SpamState.NOT_SPAM, FolderMessage.SpamState.SPAM);
        	log.info("Found %d non spam messages", nonSpam.size());
        	for (Pair<MimeMessage,SimpleJasenMessage> p : nonSpam) {
            	trainer.train(p.first(), p.second(), JasenMap.HAM);
        	}
        	
        	List<Pair<MimeMessage,SimpleJasenMessage>> spam = mailboxService.getMessages(t,
        			FolderMessage.SpamState.SPAM, FolderMessage.SpamState.NOT_SPAM);
        	
        	log.info("Found %d spam messages", spam.size());
        	for (Pair<MimeMessage,SimpleJasenMessage> p : nonSpam) {
            	trainer.train(p.first(), p.second(), JasenMap.SPAM);
        	}
            
            JasenMap map = trainer.getMap();
            File outFile = new File(getDataDir(), createFilename());
            out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(map);
            out.flush();
            
            log.info("Loading updated jasen data");
            InputStream data = getDataFile();
            Jasen tmpJasen = JasenModule.create(config, data);
            jasen = tmpJasen;
            log.info("Jasen training complete.");
            
    	} catch (JasenException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtil.quietClose(null, out);
        }
    }
    
    private String createFilename() {
        SimpleDateFormat df = new SimpleDateFormat("yyyymmddHHMMSS");
        return "jasen-" + df.format(new Date()) + ".dat";
    }
    
    static class JasenMessageTransformer implements Transformer<MessageData,Pair<MimeMessage,SimpleJasenMessage>> {
    	
    	private MailboxService service;
    	private HeaderParser parser = new HeaderParser();

		public JasenMessageTransformer(MailboxService service) {
    		this.service = service;
    	}

		public Pair<MimeMessage,SimpleJasenMessage> transform(MessageData a) {
			Pair<MimeMessage,SimpleJasenMessage> result = null;
			try {
				SimpleJasenMessage msg = null;
				InternetAddress[] from;
				from = InternetAddress.parse(a.getFrom());
                MailAddress sender = a.getSender();
                String senderAddr = sender != null ? sender.toSMTPString() : a.getFrom();
				if (from.length > 0) {
					msg = new SimpleJasenMessage("", "", senderAddr, 
                            a.getSubject(), new String[0], from[0]);
					MimeMessage mm = new MimeMessage((Session) null);
			        for (String line : a.getHeaders()) {
			            mm.addHeaderLine(line);
			        }
			        result = new Pair<MimeMessage,SimpleJasenMessage>(mm, msg);
				}
			} catch (AddressException e) {
				log.warn(e, "Failed to parse from address");
			} catch (MessagingException e) {
				log.warn(e, "Failed to extract mail headers");
			}
			return result;
		}
		
		public void extractText(MessageData md, StringBuilder text, StringBuilder html) throws IOException {
			BodyHeader header = parser.parseHeader(md.getHeader(), ContentType.DEFAULT);
			header.getContentType();
			String type = header.getContentType().getType();
			String subType = header.getContentType().getSubType();
			String charset = header.getContentType().getParameters().get("charset");
			
			for (MessageBody b : md.getBody()) {
				extractText(b, type, subType, charset, text, html);
			}
		}
		
	    public void extractText(MessageBody body, String type, String subType, String charset, 
	    		StringBuilder text, StringBuilder html) throws IOException {
	    	
			StringBuilder sb;
			
	        if (type == null) {
	            sb = text;
	        } else if("html".equals(subType)) {
	            sb = html;
	        } else if("xhtml".equals(subType)) {
	            sb = html;
	        } else if ("text".equals(type)) {
	            sb = text;
	        } else {
	            sb = null;
	        }
			
	        if (sb != null) {
	            charset = charset != null ? charset : "US-ASCII";
	            Charset cs;
	            try {
	                cs = Charset.forName(charset);
	            } catch (Exception e) {
	                cs = Charset.defaultCharset();
	            }
	            InputStream in = service.getBodyManager().getInputStream(body);
	            Reader r = new InputStreamReader(in, cs);
	            char[] data = new char[8192];
	            int numChars = 0;
	            while((numChars = r.read(data)) != -1) {
	                sb.append(data, 0, numChars);
	            }
	            sb.append("\r\n");
	        }
	        
	        for (MessageBody child : body.getChildren()) {
				BodyHeader cHeader = parser.parseHeader(child.getMimeheader(), ContentType.DEFAULT);
				String cType = cHeader.getContentType().getType();
				String cSubType = cHeader.getContentType().getSubType();
				String cCharset = cHeader.getContentType().getParameters().get("charset");
	        	extractText(child, cType, cSubType, cCharset, text, html);
	        }
		}
    }
}
