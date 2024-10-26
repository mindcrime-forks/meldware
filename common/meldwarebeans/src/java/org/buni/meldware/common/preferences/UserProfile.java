package org.buni.meldware.common.preferences;

import java.util.Map;
import java.util.Set;


public interface UserProfile {

    public String getUsername();

    public void setUsername(String username);

    public void putPreference(String key, String val);

    public String getPreference(String key);
    /**
     * @param key of the preference to return
     * @return Integer.valueOf(getPreference(key)) or 0 if it is null or not a number
     */
    public int getPreferenceAsInt(String key);

    public String getDefaultAlias();
    
    public Set<String> getAliases();
    
    public void setAliases(Set<String> aliases);

    public void setDefaultAlias(String defaultAlias);

    public String getUid();

    public Map<String, String> getPreferences();

    public boolean getPreferenceAsBoolean(String public_freebusy);

}
