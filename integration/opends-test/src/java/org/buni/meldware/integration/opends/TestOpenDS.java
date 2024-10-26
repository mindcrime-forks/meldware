package org.buni.meldware.integration.opends;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.buni.meldware.common.StringUtil;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.buni.meldware.test.JMXTestWrapper;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeValue;
import org.opends.server.types.Entry;

public class TestOpenDS extends TestCase {
    private static final String MBEAN_OPENDS = "buni.meldware:service=OpenDS";
    OpenDS opends;
    private static String dnAdd; //warning...not thread safe
    
    public TestOpenDS(String name){
        super(name);
    }
    
    public static TestSuite suite() {
        return JMXTestWrapper.suite(TestOpenDS.class);
    }    
    
    protected void setUp() throws Exception {
        if (opends == null) {
            opends = MMJMXUtil.getMBean(MBEAN_OPENDS,OpenDS.class);
        }
    }    

    protected void tearDown() throws Exception {
       
    }
    
    public void testBasicSearch() throws Exception {
        
        List<Entry> results = (List<Entry>)opends.search(null, null, "dc=buni,dc=org", OpenDS.SUBORDINATE_SUBTREE, "(&(objectClass=*)(cn=Aa*))");
        assertEquals("(&(objectClass=*)(cn=Aa*)) results should have one entry", 1, results.size());
        Entry entry = results.get(0);
        List<Attribute> attrs = entry.getAttribute("cn");
        assertEquals("attribute cn results should have one entry", 1,attrs.size());
        Attribute attr = attrs.get(0);
        Set<AttributeValue> vals = attr.getValues();
        assertEquals("attribute cn values should have one entry", 1, vals.size());
        AttributeValue val = vals.iterator().next();
        String str = val.getStringValue();
        assertEquals("attribute cn should be Aaccf Amar", "Aaccf Amar", str);
    }
    
    public void testAddEntry() throws Exception {
        String firstname = randomizedName();
        String lastname = randomizedName(); 
        String uid= firstname+lastname;
        String dn = "uid=user."+uid.toString()+",ou=People,dc=buni,dc=org";
        dnAdd = dn;
        List<String> keys = new ArrayList<String>();
        List<String> vals = new ArrayList<String>();
        keys.add("objectClass");
        vals.add("person");
        keys.add("objectClass");
        vals.add("organizationalPerson");
        keys.add("objectClass");
        vals.add("inetorgperson");
        keys.add("objectClass");
        vals.add("top");
        keys.add("cn");
        vals.add(firstname+" "+lastname);
        
        keys.add("description");
        vals.add("This is the description for "+firstname+" "+lastname);
        
        keys.add("employeeNumber");
        vals.add("0");
        
        keys.add("givenName");
        vals.add(firstname);
               
        keys.add("homePhone");
        vals.add("225-216-5900");     
        
        keys.add("initials");
        vals.add("ASA");     
        
        keys.add("l");
        vals.add("Panama City");      
        
        keys.add("mail");
        vals.add("user."+uid.toString()+"@nowhere.com");  
        
        keys.add("mobile");
        vals.add("010-154-3228");        
        keys.add("pager");
        vals.add("779-041-6341");        
        keys.add("postalAddress");
        vals.add(firstname+" "+lastname+"$01251 Chestnut Street$Panama City, DE  50369");        
        keys.add("postalCode");
        vals.add("50369");        
        keys.add("sn");
        vals.add(lastname);        
        keys.add("st");
        vals.add("DE");        
        keys.add("street");
        vals.add("01251 Chestnut Street");        
        keys.add("telephoneNumber");
        vals.add("685-622-6202");        
        keys.add("uid");
        vals.add("user."+uid.toString());
      //  keys.add("userPassword");
     //   vals.add("e1NTSEF9dWppWkp4MS9VWmMvRG44V09qMWFZMFVJMWgvUnJwVG9UMGRTMkE9PQ==");
//TODO reenable
//        opends.addEntry(null, null, dn, keys, vals);
        
        List<Entry> results = (List<Entry>)opends.search(null, null, "dc=buni,dc=org", 2, "(&(objectClass=*)(uid="+"user."+uid.toString()+"))");
        assertEquals("added results should have one entry", 1, results.size());
        Entry entry = results.get(0);
        List<Attribute> attrs = entry.getAttribute("cn");
        assertEquals("attribute cn results should have one entry", 1,attrs.size());
        Attribute attr = attrs.get(0);
        Set<AttributeValue> vs = attr.getValues();
        assertEquals("attribute cn values should have one entry", 1, vs.size());
        AttributeValue val = vs.iterator().next();
        String str = val.getStringValue();
        assertEquals("attribute cn should be "+firstname+" "+lastname, firstname+" "+lastname, str);

    }
     
    public void testDeleteEntry() throws Exception {
        String dn = dnAdd;

        opends.deleteEntry(null, null, dn);
        List<Entry> results = (List<Entry>)opends.search(null, null, dn, 1, "(objectClass=*)");
        assertEquals("results.size after delete should be 0", 0, results.size());
    }

    private String randomizedName() {
        String val = StringUtil.randomAlphabeticalString(8);
        val = val.charAt(0)+val.toLowerCase().substring(1);//mixed case it
        return val;
    }
}
