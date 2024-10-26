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
package org.buni.meldware.common.preferences;

import java.util.Set;

import org.jboss.resource.adapter.jdbc.remote.WrapperDataSourceServiceMBean;


public interface UserProfileService { 

    public void create();

    public UserProfile createUserProfile(String username, String defaultAlias, Set<String> aliases);

    public void destroy();

    public WrapperDataSourceServiceMBean getDataSource();

    public UserProfile retrieveUserProfile(String username);

    public void setDataSource(WrapperDataSourceServiceMBean wds);

    public void start();

    public void stop();

    public String getCreateUserPreferencesStatement();

    public void setCreateUserPreferencesStatement(String createUserPreferencesStatement);

    public String getCreateUserProfileStatement();

    public void setCreateUserProfileStatement(String createUserProfileStatement);

    public String getInsertUserProfileStatement();

    public void setInsertUserProfileStatement(String insertUserProfileStatement);

    public boolean deleteUserProfile(String username);

    public UserProfile updateUserProfile(UserProfile profile);

    public String getDeleteUserProfileStatement();

    public void setDeleteUserProfileStatement(String deleteUserProfileStatement);

    public String getRetrieveUserProfileStatement();

    public void setRetrieveUserProfileStatement(String retrieveUserProfileStatement);

    public String getDeletePreferencesStatement();

    public void setDeletePreferencesStatement(String deletePreferencesStatement);

    public String getInsertPreferenceStatement();

    public void setInsertPreferenceStatement(String insertPreferenceStatement);

    public String getUpdateProfileStatement();

    public void setUpdateProfileStatement(String updateProfileStatement);
    
    public void registerProfileChangeListener(ProfileChangeListener listener);
    
    public String getDeleteUserAliasesStatement();
    
    public void setDeleteUserAliasesStatement(String deleteUserAliasesStatement);
    
    public String getInsertUserAliasStatement();
    
    public void setInsertUserAliasStatement(String insertUserAliasStatement);
        
    public String getCreateUserAliasesStatement();

    public void setCreateUserAliasesStatement(String createUserAliasesStatement);
    
    public void setFindProfileStatement(String findUserProfileStatement);
    
    public UserProfile findProfile(String alias);
    
    public void setRetrieveUserAliasesStatement(String retrieveUserAliasesStatement);
    public String getRetrieveUserAliasesStatement();

    public Set<UserProfile> findProfiles(String userName);
}
