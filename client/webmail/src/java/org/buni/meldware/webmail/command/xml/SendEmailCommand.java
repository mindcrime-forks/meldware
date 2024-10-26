package org.buni.meldware.webmail.command.xml;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SendEmailCommand extends AbstractXMLCommand implements XMLCommand {

    public String execute(HttpServletRequest request, Document doc, PrintWriter out) {
        Element operation = (Element) doc.getElementsByTagName("operation").item(0);
        String user = request.getUserPrincipal().getName();
        String from = getSender(operation);
        String[] to = getTos(operation);
        String[] cc = getCcs(operation);
        String[] bcc = getBccs(operation);
        String subject = getSubject(operation);
        String body = getBody(operation);
        String tabid = getTabId(operation);
        String attachments = getAttachments(operation);

        if (to == null)
        {
          to = new String[0];
        }

        if (cc == null)
        {
          cc = new String[0];
        }

        if (bcc == null)
        {
          bcc = new String[0];
        }
        // attachements should already be on the server since we will make
        // the upload happen when the user selects the file the server will
        // pass an attachement id to the client and on sendEmail the client
        // will just send a comma separated list of attachment id's

        // send the email
        System.out.println("sending email:" + "\nto = " + getString(to) + "\ncc = "
                + getString(cc) + "\nbcc = " + getString(bcc) + "\nsubject = " + subject
                + "\nbody = " + body);
        sender.send(user, from, to, cc, bcc, subject, body);

        out.println("<complete tabid=\"" + tabid
                + "\"/>");
        return XMLCommand.SUCCESS;
    }

    private String getTabId(Element operation) {
        Element bodyElement = (Element) operation.getElementsByTagName("tabid").item(0);
        return bodyElement.getTextContent();
    }

    private String getAttachments(Element operation) {
        // TODO Auto-generated method stub
        return "";
    }

    private String getBody(Element operation) {
        Element bodyElement = (Element) operation.getElementsByTagName("body").item(0);
        return bodyElement.getTextContent();
    }

    private String getSubject(Element operation) {
        Element subjectElement = (Element) operation.getElementsByTagName("subject").item(0);
        return subjectElement.getTextContent();
    }

    private String[] getTos(Element operation) {
        return getAddressElements(operation,"to");
    }
    
    private String[] getBccs(Element operation) {
        return getAddressElements(operation,"bcc");
    }
    
    private String[] getCcs(Element operation) {
        return getAddressElements(operation,"cc");
    }
    
    private String[] getAddressElements(Element operation, String name) {
        Element toElement = (Element)operation.getElementsByTagName(name).item(0);
        NodeList items = toElement.getElementsByTagName("item");
        int size = items.getLength();
        String[] addresses = new String[size];
        for(int i = 0; i < size; i++) {
            addresses[i] = ((Element)items.item(i)).getElementsByTagName("fullAddress").item(0).getTextContent().trim();
        }
        return addresses;
    }

    private String getSender(Element operation) {
        Element fromElement = (Element) operation.getElementsByTagName("from").item(0);
        Element addressElement = (Element) fromElement.getElementsByTagName("fullAddress").item(0);
        String address = addressElement.getTextContent();
        return address;
    }

}
