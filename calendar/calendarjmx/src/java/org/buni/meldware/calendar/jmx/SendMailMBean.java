package org.buni.meldware.calendar.jmx;

import java.io.IOException;

import org.columba.ristretto.io.Source;
import org.columba.ristretto.message.Address;
import org.columba.ristretto.message.LocalMimePart;
import org.columba.ristretto.message.MimePart;
import org.columba.ristretto.parser.ParserException;

/**
 * A Ristretto base maile to replace the JavaMail.
 * 
 * @author Aron Sogor
 *
 */
public interface SendMailMBean {

	public  MimePart createAllAlternativeMessage(String subject,
			Address[] toAddress, Address[] ccAddress, Source textBody,
			Source htmlBody, LocalMimePart icsAttachement) throws ParserException;

	public  MimePart createAlternativeWithAttachmentMessage(
			String subject, Address[] toAddress, Address[] ccAddress,
			Source textBody, Source htmlBody, LocalMimePart icsAttachement) throws ParserException;

	public  LocalMimePart createICSAttachment(String filename,
			Source source) throws IOException;

	public  String getFromAddress();

	public  String getPassword();

	public  int getPort();

	public  String getSmtpServer();

	public  String getUsername();

	public  boolean isVerbose();

	public  void sendMail(Address[] toAddress, Address[] ccAddress,
			MimePart message) throws Exception;

	public  void setFromAddress(String from_address);

	public  void setPassword(String password);

	public  void setPort(int port);

	public  void setSmtpServer(String smtp_server);
	
	public  void setUsername(String username);

	public  void setVerbose(boolean verbose);
	
	public  void start();

	public  void stop();

}