package org.buni.meldware.webadmin.command.xml;

import java.util.ArrayList;
import java.util.List;

import org.buni.meldware.mail.userapi.MailSender;
import org.buni.meldware.webadmin.command.xml.XMLCommand;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Mainly this contains utility methods that subclasses will need to deal with XML shit.  
 * Generally "operation" as an arguemnt is the "operation" element sought by getOperation.
 * The getOperation finds the "operation" element which is only called htat because of a 
 * coding convention on the client.  Sometimes however is useful to pass subelements other 
 * than the operation (for instance if hte operation contains an object with fields) to the 
 * "operation" argument of hte methods.  IF this is too confusing look at how AddABEntryCommand works
 * in the addLdap in the UserDelegate in webadminflex client and on the server.  Do other things 
 * similarly
 * @author Andrew C. Oliver
 *
 */
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
    
    public Element getElement(Element operation, String field) {
        Element retval = (Element) operation.getElementsByTagName(field).item(0);
        if (operation == null) { 
            throw new RuntimeException("ill-formed operation");
        }
        return retval;
    }
    
    public String getString(Element operation, String field) {
    	NodeList list = operation.getElementsByTagName(field);
    	if (list == null || list.getLength() == 0) {
    		return null;
    	}
        Element fieldElement = (Element) list.item(0);
        return fieldElement.getTextContent();
    }
    
    /**
     * get string list form an xml-serialized Flex ArrayCollection of strings.  This is 
     * in a weird funky set of field contains list contains source contains item but field
     * also contains source which contains item and list also contains a uid.  It is weird
     * and best you just use this method whenever you have an arraycollection of strings and
     * don't try strain your pretty little head trying to understand it (again).
     * @param operation the elemnet of the overall "operation"
     * @param field (basically the element name inside the operation)
     * @return list of strings
     */
    public List<String> getStringList(Element operation, String field) {
        List<String> list = new ArrayList<String>();
        Element fieldElement = (Element)operation.getElementsByTagName(field).item(0);
        Element listEl = (Element)fieldElement.getElementsByTagName("list").item(0); 
        Element source = (Element)listEl.getElementsByTagName("source").item(0);
        NodeList items = source.getElementsByTagName("item");
        for (int i = 0; i < items.getLength(); i++) {
            list.add(((Element)items.item(i)).getTextContent());
        }
        return list;
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

    public static String toElement(String element, String value) {
        
        return "<"+element+">"+value+"</"+element+">";
    }

}
