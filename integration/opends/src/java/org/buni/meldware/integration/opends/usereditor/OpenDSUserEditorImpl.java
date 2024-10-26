package org.buni.meldware.integration.opends.usereditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.buni.meldware.common.HashBucketThing;
import org.buni.meldware.integration.opends.OpenDS;
import org.opends.server.types.Attribute;
import org.opends.server.types.AttributeValue;
import org.opends.server.types.Entry;

public class OpenDSUserEditorImpl implements OpenDSUserEditor {

    private OpenDS openDS;
    private String rolesDN;
    private String userDN;
    
    static final String OBJECT_CLASS = "objectClass";
    static final String OC_PERSON = "person";
    static final String OC_ORG_PERSON = "organizationalperson";
    static final String OC_INET_PERSON = "inetorgperson";
    static final String OC_TOP = "top";
    static final String OC_GROUP_OF_NAMES = "groupOfNames";
    
    static final String KEY_PASSWORD = "userPassword";
    static final String KEY_USER_ID = "uid";
    static final String KEY_SN = "sn";
    static final String KEY_CN = "cn";
    static final String KEY_MEMBER = "member";

    public void addRole(String login, String role) {        
        String dn = "cn="+role+","+rolesDN;
        String filter = "(&(objectClass=groupOfNames)(cn="+role+"))";
        List<Entry> roles = (List<Entry>)this.openDS.search(null, null, rolesDN, OpenDS.SUBORDINATE_SUBTREE, filter);
        if (roles == null || roles.size() < 1) {
            HashBucketThing<String,String> fields = new HashBucketThing<String, String>();
            fields.put(OBJECT_CLASS, OC_GROUP_OF_NAMES);
            fields.putAnother(OBJECT_CLASS, OC_TOP);
            fields.put(KEY_CN, role);
            List<String> attrs = fields.getKeys();
            openDS.addEntry(null, null, dn, attrs, fields.getMap());
        } 
        
            Map<String,Integer> types = new HashMap<String,Integer>();
            types.put(KEY_MEMBER,OpenDS.ADD);
            List<String> attTypes = new ArrayList<String>();
            attTypes.add(KEY_MEMBER);
            List<String> avals = new ArrayList<String>();
            avals.add("uid="+login+","+userDN);
            this.openDS.updateEntry(null, null, "cn="+role+","+rolesDN, types, attTypes, avals);            
       
    }

    public void addUser(String login, String password) {
        HashBucketThing<String,String> fields = new HashBucketThing<String, String>();
        fields.put(OBJECT_CLASS, OC_PERSON);
        fields.putAnother(OBJECT_CLASS, OC_ORG_PERSON);
        fields.putAnother(OBJECT_CLASS, OC_INET_PERSON);
        fields.putAnother(OBJECT_CLASS, OC_TOP);
        fields.put(KEY_SN, "unknown");
        fields.put(KEY_CN, "unknown");
        fields.put(KEY_USER_ID, login);
        fields.put(KEY_PASSWORD, password);
        
        List<String> attrs = fields.getKeys();
        String dn = "uid="+login+","+this.userDN;
        openDS.addEntry(null, null, dn, attrs, fields.getMap());
    }

    public void changePassword(String login, String password) {
        Map<String,Integer> types = new HashMap<String,Integer>();
        types.put("userPassword",OpenDS.UPDATE);
        List<String> attTypes = new ArrayList<String>();
        attTypes.add("userPassword");
        List<String> avals = new ArrayList<String>();
        avals.add(password);
        this.openDS.updateEntry(null, null, "uid="+login+","+userDN, types, attTypes, avals);
    }

    public void deleteRole(String login, String role) {
        this.openDS.deleteAttribute(null,null, "cn="+role+","+rolesDN,login);
    }

    public void deleteUser(String login) {
        this.openDS.deleteEntry(null, null, "uid="+login+","+userDN);  
    }

    public List getRoles(String login) {
        String filter = "(&(objectClass=*)(member="+login+"))";
        return dsEntryToStringList((List<Entry>)this.openDS.search(null, null, rolesDN, OpenDS.SUBORDINATE_SUBTREE, filter),"cn");
    }

    public List getUsers(String pattern) {
        String filter = "(&(objectClass=*)(uid="+pattern+"))";
        return dsEntryToStringList((List<Entry>)this.openDS.search(null, null, userDN, OpenDS.SUBORDINATE_SUBTREE, filter),"uid");
    }

    private List<String> dsEntryToStringList(List<Entry> entries, String attribute) {
        List<String> results = new ArrayList<String>();
        for (Entry entry : entries) {
                results.add(getFlatAttributeValue(entry, attribute));
        }
        return results;
    }
    
    private static String getFlatAttributeValue(Entry entry, String key) {
        List<Attribute> attrs = entry.getAttribute(key.toLowerCase());
        if (attrs == null || attrs.size() == 0) { 
            return null;
        }
        Attribute attr = attrs.get(0);
        Set<AttributeValue> vals = attr.getValues();
        if (vals == null || vals.size() == 0) {
            return null;
        }
        AttributeValue val = vals.iterator().next();
        String retval = val == null ? null : val.getStringValue();
        return retval;
    }

    public void start() {
        // TODO Auto-generated method stub
        
    }

    public void stop() {
        // TODO Auto-generated method stub
        
    }

    public boolean userExists(String login) {
        List list = this.getUsers(login);
        return list != null && list.size() > 0;
    }

    public OpenDS getOpenDS() {
        return this.openDS;
    }

    public String getRolesRoot() {
        return this.rolesDN;
    }

    public String getUserRoot() {
        return this.userDN;
    }

    public void setOpenDS(OpenDS openDS) {
        this.openDS = openDS;
    }

    public void setRolesRoot(String rolesDN) {
        this.rolesDN = rolesDN;
    }

    public void setUserRoot(String userDN) {
        this.userDN = userDN;
    }
 
}
