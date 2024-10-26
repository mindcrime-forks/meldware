package org.buni.meldware.common.preferences.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.buni.meldware.common.preferences.UserProfile;


public class UserProfileImpl implements UserProfile {
    private static final String UP_ID = "UP_ID";
    private static final String UP_USERNAME = "UP_USERNAME";
    private static final String UP_DEFAULT_ALIAS = "UP_DEFAULT_ALIAS";
    private static final String A_ALIAS = "A_ALIAS";
    private static final String UPP_KEY = "UPP_KEY";
    private static final String UPP_VAL = "UPP_VAL";
    private String uid;
    private String username;
    private String defaultAlias;
    private Set<String> aliases;
    
    private Map<String,String> preferences;

    
    public UserProfileImpl() {
        this.preferences = new HashMap<String, String>();
        this.aliases = new HashSet<String>();
    }
    
    public UserProfileImpl(String uid, String username, String defaultAlias) {
        this();
        this.uid = uid;
        this.username=username;
        this.defaultAlias=defaultAlias;
    }

    public UserProfileImpl(ResultSet rs) throws SQLException {
        this.uid = rs.getString(UP_ID);
        this.username = rs.getString(UP_USERNAME);
        this.defaultAlias = rs.getString(UP_DEFAULT_ALIAS);
        preferences = populatePrefs(rs);
    }
    
    public void setAliases(ResultSet rs) throws SQLException {
        this.aliases = new HashSet<String>();
        do {
            String alias = rs.getString(A_ALIAS);
            aliases.add(alias);
        } while (rs.next());
    }

    private Map<String, String> populatePrefs(ResultSet rs) throws SQLException {
        Map<String, String> results = new HashMap<String, String>();
        do {
            String key = rs.getString(UPP_KEY);
            String val = rs.getString(UPP_VAL);
            results.put(key, val);
        } while (rs.next());
        return results;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void putPreference(String key, String val) {
        this.preferences.put(key, val);
    }
    
    public String getPreference(String key) {
        return this.preferences.get(key);
    }

    
    public String getDefaultAlias() {
        return defaultAlias;
    }

    
    public void setDefaultAlias(String defaultAlias) {
        this.defaultAlias = defaultAlias;
    }
    
    public Set<String> getAliases() {
        return this.aliases;
    }

    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
    }
    
    public String getUid() { 
        return uid;
    }

    public Map<String, String> getPreferences() {
        return this.preferences;
    }

    public int getPreferenceAsInt(String key) {
        String pref = this.getPreference(key);
        pref = pref == null ? ""+0 : pref;
        int retval = 0; 
        try {
            retval = Integer.valueOf(pref);
        } catch (Exception e) {}

        return retval;
    }

    public boolean getPreferenceAsBoolean(String key) {
        String pref = this.getPreference(key);
        pref = pref == null ? ""+false : pref;
        boolean retval = false;
            
        try {
            retval = Boolean.parseBoolean(pref);
        } catch (Exception e) {}
        return retval;
    }

 }
