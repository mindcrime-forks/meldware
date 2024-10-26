package org.buni.meldware.mail.abmounts;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.address.MutableAddressBookEntry;
import org.buni.meldware.mail.tx.TxRunner;
import org.buni.meldware.mail.tx.TxRunnerFactory;
import org.buni.meldware.test.JMXTestWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TestABMounts extends TestCase {

    TxRunner txr = TxRunnerFactory.create();

    public TestABMounts(String name) {
        super(name);
    }
    
    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestABMounts.class);
    }    
    
    public void testSetupMounts() throws Exception {
        String typed = "Aaccf";
        SystemABMountsService sabm = new SystemABMounts();
        sabm.setHanders(createHandlerConfig());
        sabm.setMounts(createMountsConfig());
        Set<String> domains = new HashSet<String>();
        domains.add("buni.org");
        Set<ABMount> mounts = sabm.getABMountsForDomains(domains);
        assertEquals("mounts should be 1, the local one", 1, mounts.size());
        ABMount mount = mounts.iterator().next();
        List<MutableAddressBookEntry> mabe = mount.getMatchingAddresses(typed, 0, 100);//arguments except the first are ignored presently
        assertEquals("The number returned should be 1",1, mabe.size());
        MutableAddressBookEntry entry = mabe.get(0);
        String given = entry.getGivenName();
        assertEquals("the found one should be Aaccf and thus equal to typed", typed, given);
    }

    private Element createMountsConfig() throws Exception {
        String val = "<abmounts>"+
              "<abmount type=\"local\" name=\"Buni.org\">"+
              "<description>Buni.org Example Addressbook</description>" +
              "<configuration><property key=\"ObjectName\" value=\"buni.meldware:service=AddressBook\"/></configuration>"+
              "<domains><domain>buni.org</domain></domains>"+
              "</abmount>"+
              "</abmounts>";
        DocumentBuilder bld = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document adoc = bld.parse(new ByteArrayInputStream(val.toString().getBytes()));       
        return (Element)adoc.getChildNodes().item(0);
    }

    private Element createHandlerConfig() throws Exception {
        String val = "<handlers><handler>org.buni.meldware.mail.abmounts.LocalABMountFactory</handler></handlers>";
        DocumentBuilder bld = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document adoc = bld.parse(new ByteArrayInputStream(val.toString().getBytes()));       
        NodeList foo = adoc.getChildNodes();
        Element retval = (Element)foo.item(0);
        return retval;
    }
}
