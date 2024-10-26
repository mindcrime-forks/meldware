package org.buni.meldware.webmail.command.xml;

import org.buni.meldware.mail.userapi.MailSender;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class AbstractXMLCommand implements XMLCommand {
    protected MailSender sender;

    public void setMailSender(MailSender sender) {
        this.sender = sender;
    }
    
    public Element getOperation(Document doc) {
        Element operation = (Element) doc.getElementsByTagName("operation").item(0);
        if (operation == null) {
        	throw new RuntimeException("ill-formed operation");
        }
        return operation;
    }
    
    public String getString(Element operation, String field) {
    	NodeList list = operation.getElementsByTagName(field);
    	if (list == null || list.getLength() == 0) {
    		return null;
    	}
        Element fieldElement = (Element) list.item(0);
        return fieldElement.getTextContent();
    }
    
    public String[] getStringArray(Element operation, String field, String itemField) {
        Element fieldElement = (Element)operation.getElementsByTagName(field).item(0);
        NodeList items = fieldElement.getElementsByTagName("item");
        int size = items.getLength();
        String[] strArray = new String[size];
        for(int i = 0; i < size; i++) {
            strArray[i] = ((Element)items.item(i)).getElementsByTagName(itemField).item(0).getTextContent().trim();
        }
        return strArray;
    }
    
    protected String getString(String[] s)
    {
      String r = "[";
      for (int i = 0; i < s.length; i++)
      {
        r += s[i];
      }
      r += "]";

      return r;
    }

}
