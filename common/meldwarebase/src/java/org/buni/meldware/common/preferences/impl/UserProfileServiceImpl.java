/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC., and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.buni.meldware.common.preferences.impl;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.buni.meldware.common.db.DbUtil;
import org.buni.meldware.common.preferences.ProfileChangeEvent;
import org.buni.meldware.common.preferences.ProfileChangeListener;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.common.preferences.impl.UserProfileImpl;
import org.jboss.aspects.asynch.Asynchronous;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;
import org.jboss.resource.adapter.jdbc.remote.WrapperDataSourceServiceMBean;


public class UserProfileServiceImpl implements UserProfileService {
    private static final Logger log = Logger.getLogger(UserProfileServiceImpl.class);

    private WrapperDataSourceServiceMBean wds;
    private DataSource ds;

    private String createUserProfileStatement;
    private String createUserPreferencesStatement;
    private String createUserAliasesStatement;

    private String insertUserProfileStatement;

    private String retrieveUserProfileStatement;
    private String retrieveUserAliasesStatement;

    private String deleteUserProfileStatement;
    private String deleteUserAliasesStatement;

    private String updateProfileStatement;

    private String insertPreferenceStatement;
    private String insertUserAliasStatement;

    private String deletePreferencesStatement;
    
    private String findProfileStatement;

    private Set<ProfileChangeListener> changeListeners;
    
    public void create() {}
 
    @Tx(TxType.REQUIRED)
    public UserProfile createUserProfile(String username, String defaultAlias, Set<String> aliases) {
        UserProfile up = new UserProfileImpl(DbUtil.generateUID(), username, defaultAlias);
        DbUtil.executeStatement(ds, this.insertUserProfileStatement, new Object[]{up.getUid(), username,defaultAlias});
        for (String string : aliases) {
            DbUtil.executeStatement(ds, this.insertUserAliasStatement, new Object[]{string,up.getUid()});
        }
        up.setAliases(aliases);
        notifyChange(up, ProfileChangeEvent.PROFILE_CREATED);

        return up;
    }
    

    public void destroy() {
    }

    public WrapperDataSourceServiceMBean getDataSource() {
        return this.wds;
    }

    @Tx(TxType.REQUIRED)
    public UserProfileImpl retrieveUserProfile(String guid) {
        ResultSet rs=null;
        UserProfileImpl up;
        try {
            rs = DbUtil.executeResultStatement(ds, this.retrieveUserProfileStatement, new Object[]{guid});
            up = rs.next() ? new UserProfileImpl(rs) : null;
            DbUtil.closeQuietly(rs);
            rs = DbUtil.executeResultStatement(ds, this.retrieveUserAliasesStatement, new Object[]{guid});
            if (up != null && rs.next()) { 
                up.setAliases(rs);
            }
        } catch (Exception e) { 
            throw new RuntimeException(e);
        } finally {
            DbUtil.closeQuietly(rs);
        }
        return up;
    }

    public void setDataSource(WrapperDataSourceServiceMBean wds) {
        this.wds = wds;
    }

    public void start() {
        this.changeListeners = new HashSet<ProfileChangeListener>();
        String jndiName = this.wds.getBindName();
        Context ctx;
        try {
            ctx = new InitialContext();
            this.ds=(DataSource) ctx.lookup(jndiName);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        try {
            DbUtil.executeStatement(this.ds, createUserProfileStatement, DbUtil.EMPTY_OBJARRAY);
            DbUtil.executeStatement(this.ds, createUserPreferencesStatement, DbUtil.EMPTY_OBJARRAY);
            DbUtil.executeStatement(this.ds, createUserAliasesStatement, DbUtil.EMPTY_OBJARRAY);
        } catch (Exception e) {
            log.debug("could not create one or more of the UserProfileImpl tables",e);
        }
    }

    public void stop() {
    }

    
    public String getCreateUserPreferencesStatement() {
        return createUserPreferencesStatement;
    }

    
    public void setCreateUserPreferencesStatement(String createUserPreferencesStatement) {
        this.createUserPreferencesStatement = createUserPreferencesStatement;
    }

    
    public String getCreateUserProfileStatement() {
        return createUserProfileStatement;
    }

    
    public void setCreateUserProfileStatement(String createUserProfileStatement) {
        this.createUserProfileStatement = createUserProfileStatement;
    }

    
    public String getInsertUserProfileStatement() {
        return insertUserProfileStatement;
    }

    
    public void setInsertUserProfileStatement(String insertUserProfileStatement) {
        this.insertUserProfileStatement = insertUserProfileStatement;
    }

    @Tx(TxType.REQUIRED)
    public boolean deleteUserProfile(String username) {
        UserProfile profile = username != null ? this.findProfile(username) : null;
        if (profile != null) {
            DbUtil.executeStatement(ds, this.deleteUserProfileStatement, new Object[]{username});
            DbUtil.executeStatement(ds, this.deleteUserAliasesStatement, new Object[]{profile.getUid()});
            DbUtil.executeStatement(ds, this.deletePreferencesStatement, new Object[]{profile.getUid()});
        }
        notifyChange(profile, ProfileChangeEvent.PROFILE_DELETED);
        return false;
    }
    
    @Tx(TxType.REQUIRED)
    public UserProfile findProfile(String alias) {
        ResultSet rs = null;
        try {
            rs = DbUtil.executeResultStatement(ds, this.findProfileStatement, new Object[]{alias});
            if (rs.next()) {
                return this.retrieveUserProfile(rs.getString(1));
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.toString(), (Throwable)e);
        }finally {
            DbUtil.closeQuietly(rs);
        }
        return null;
    }

    @Tx(TxType.REQUIRED)
    public UserProfile updateUserProfile(UserProfile profile) {
        UserProfile oldProfile = this.findProfile(profile.getUsername());
        if (oldProfile != null && profile.getUid() != null && !oldProfile.getUid().equals(profile.getUid())) {
            throw new RuntimeException("attempted to update a profile with differing uid");
        }
        DbUtil.executeStatement(ds, this.updateProfileStatement, new Object[]{profile.getUsername(),
                                                                              profile.getDefaultAlias(),
                                                                              profile.getUid()});   
        Map<String,String> map = profile.getPreferences();
        Set<String> keys = map.keySet();
        DbUtil.executeStatement(ds, this.deletePreferencesStatement, new Object[] {
                                                                     profile.getUid()
        });
        for (String key : keys) {
            String val = map.get(key);
            DbUtil.executeStatement(ds, this.insertPreferenceStatement, new Object[] {
                                                                        key, val, 
                                                                        profile.getUid()
            });
        }
        Set<String> aliases = profile.getAliases();

        DbUtil.executeStatement(ds, this.deleteUserAliasesStatement, new Object[] {profile.getUid()});
        for (String string : aliases) {
            DbUtil.executeStatement(ds, this.insertUserAliasStatement, new Object[]{string, profile.getUid()});
        }
        notifyChange(profile, ProfileChangeEvent.PROFILE_UPDATED);

        return profile;
    }

    
    public String getDeleteUserProfileStatement() {
        return deleteUserProfileStatement;
    }

    
    public void setDeleteUserProfileStatement(String deleteUserProfileStatement) {
        this.deleteUserProfileStatement = deleteUserProfileStatement;
    }

    
    public String getRetrieveUserProfileStatement() {
        return retrieveUserProfileStatement;
    }

    
    public void setRetrieveUserProfileStatement(String retrieveUserProfileStatement) {
        this.retrieveUserProfileStatement = retrieveUserProfileStatement;
    }

    
    public String getDeletePreferencesStatement() {
        return deletePreferencesStatement;
    }

    
    public void setDeletePreferencesStatement(String deletePreferencesStatement) {
        this.deletePreferencesStatement = deletePreferencesStatement;
    }

    
    public String getInsertPreferenceStatement() {
        return insertPreferenceStatement;
    }

    
    public void setInsertPreferenceStatement(String insertPreferenceStatement) {
        this.insertPreferenceStatement = insertPreferenceStatement;
    }

    
    public String getUpdateProfileStatement() {
        return updateProfileStatement;
    }

    
    public void setUpdateProfileStatement(String updateProfileStatement) {
        this.updateProfileStatement = updateProfileStatement;
    }

    public void registerProfileChangeListener(ProfileChangeListener listener) {
        this.changeListeners.add(listener);
    }


    private void notifyChange(final UserProfile profile, final byte changeType) {
        ProfileChangeEvent event = new ProfileChangeEvent(){
        
            public UserProfile getUserProfile() {
                return profile;
            }
        
            public byte getChangeType() {
                return changeType;
            }     
        };
        
        Set<ProfileChangeListener> listeners = this.changeListeners;
        for (ProfileChangeListener listener : listeners) {
            sendNotification(listener, event);
        }
    }

    @Asynchronous
    private void sendNotification(ProfileChangeListener listener, ProfileChangeEvent event) {
        listener.processChangeEvent(event);
    }

    
    public String getDeleteUserAliasesStatement() {
        return deleteUserAliasesStatement;
    }

    
    public void setDeleteUserAliasesStatement(String deleteUserAliasesStatement) {
        this.deleteUserAliasesStatement = deleteUserAliasesStatement;
    }

    
    public String getInsertUserAliasStatement() {
        return insertUserAliasStatement;
    }

    
    public void setInsertUserAliasStatement(String insertUserAliasStatement) {
        this.insertUserAliasStatement = insertUserAliasStatement;
    }

    
    public String getCreateUserAliasesStatement() {
        return createUserAliasesStatement;
    }

    
    public void setCreateUserAliasesStatement(String createUserAliasesStatement) {
        this.createUserAliasesStatement = createUserAliasesStatement;
    }
    
    public void setFindProfileStatement(String findProfileStatement) {
        this.findProfileStatement = findProfileStatement;
    }
    
    public void setRetrieveUserAliasesStatement(String retrieveUserAliasesStatement) {
        this.retrieveUserAliasesStatement = retrieveUserAliasesStatement;
    }
    
    public String getRetrieveUserAliasesStatement() {
        return this.retrieveUserAliasesStatement;
    }

    public Set<UserProfile> findProfiles(String alias) {
        ResultSet rs = null;
        Set<UserProfile> retval = new HashSet<UserProfile>();
        try {
            rs = DbUtil.executeResultStatement(ds, this.findProfileStatement, new Object[]{alias});
            while (rs.next()) {
                retval.add(this.retrieveUserProfile(rs.getString(1)));
            }
        } catch (Exception e) {
            log.error(e.toString(), (Throwable)e);
        }finally {
            DbUtil.closeQuietly(rs);
        }
        return null;
    }
    
}
