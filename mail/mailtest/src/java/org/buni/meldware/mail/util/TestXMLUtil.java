package org.buni.meldware.mail.util;

import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class TestXMLUtil extends TestCase {

    
    String s = "<Properties>" 
        + "<DiskMapStore>"
        + "<storePath>jasen-data/default/jasen.dat</storePath>"
        + "</DiskMapStore>"
        + "</Properties>";
    
    public void testToConfig() throws Exception {
        DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document d = b.parse(new InputSource(new StringReader(s)));
        Map<String,String> config = XMLUtil.toProperties(d.getDocumentElement());
        assertEquals("jasen-data/default/jasen.dat", config.get("DiskMapStore.storePath"));
    }
}
