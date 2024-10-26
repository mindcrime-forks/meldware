package org.buni.meldware.common.preferences;


public interface ProfileChangeEvent {
    public static final byte PROFILE_CREATED=0;
    public static final byte PROFILE_UPDATED=1;
    public static final byte PROFILE_DELETED=2;
    
    UserProfile getUserProfile();
    byte getChangeType();
}
