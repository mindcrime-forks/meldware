/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC, and individual contributors as
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
package org.buni.meldware.integration.opends;
import static java.lang.String.format;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.opends.server.api.Backend;
import org.opends.server.core.AddOperation;
import org.opends.server.core.BindOperation;
import org.opends.server.core.DeleteOperation;
import org.opends.server.core.DirectoryServer;
import org.opends.server.core.ModifyOperation;
import org.opends.server.extensions.ConfigFileHandler;
import org.opends.server.protocols.internal.InternalClientConnection;
import org.opends.server.protocols.internal.InternalSearchOperation;
import org.opends.server.tasks.TaskUtils;
import org.opends.server.types.AuthenticationInfo;
import org.opends.server.types.ByteString;
import org.opends.server.types.ByteStringFactory;
import org.opends.server.types.DereferencePolicy;
import org.opends.server.types.DirectoryException;
import org.opends.server.types.LDIFImportConfig;
import org.opends.server.types.ModificationType;
import org.opends.server.types.RawAttribute;
import org.opends.server.types.RawModification;
import org.opends.server.types.SearchScope; 

public class OpenDSService implements OpenDS {
  private DirectoryServer ds;
  private String configFile; 
  private String rootDir;
  public void setConfigFile(String configFile) {
      this.configFile = configFile;
  }

  public String getConfigFile() {
      return this.configFile;
  }

  public void setRootDir(String rootDir) {
      this.rootDir = rootDir;
  }

  public String getRootDir() {
      return this.rootDir;
  }
  
  private static void createDir(File dir) {
      if (!dir.exists()) {
          dir.mkdirs();
      } else if (dir.isFile()) {
          throw new RuntimeException(format("%s is a file not a directory", dir.getAbsolutePath()));
      }
  }
  
  private static void createLockDir(String path) {
      File dir = new File(path);
      createDir(dir);
      createDir(new File(dir, "locks"));
      createDir(new File(dir, "logs"));
  }

  public void start() {
      try {
          createLockDir(rootDir);
          System.setProperty("org.opends.server.ServerRoot", rootDir);
          this.ds = DirectoryServer.getInstance();
          this.ds.bootstrapServer();
          this.ds.initializeConfiguration(ConfigFileHandler.class.getName(), configFile);
          this.ds.startServer();
      } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e);
      }
  }

  public void stop() {
      try {
          DirectoryServer.shutDown(DirectoryServer.class.getName(), "stop called");
      } catch (Exception e) {
          e.printStackTrace();  //we don't stop...
      }
  }

  public void create() {}
  public void destroy() {}

  /*
  public void addEntry(String bindDN, String password, String dn, String strTypes, String svals) {
      List<String> attTypes = ArrayUtil.commaDelToStringList(strTypes);
      List<String> avals = ArrayUtil.commaDelToStringList(svals);
      addEntry(bindDN, password, dn, attTypes, avals);
  } */ 
  
  public void addEntry(String bindDN, String password, String dn, List<String> attTypes, Map<String,List<String>> avals) {
      InternalClientConnection conn = InternalClientConnection.getRootConnection();
      conn=authenticate(bindDN,password, conn);
      List<RawAttribute> attrs = new ArrayList<RawAttribute>(attTypes.size());
      for (int i = 0; i < attTypes.size(); i++) { 
          for(String val: avals.get(attTypes.get(i))) {
              RawAttribute attr = RawAttribute.create(attTypes.get(i), val);
              attrs.add(attr);
          }
      }
      AddOperation ao = conn.processAdd(dn, attrs);
      if (ao.getResultCode().getIntValue() != 0) {
          System.err.println("message "+ao.getErrorMessage()+" num "+ao.getMessageID());
          throw new RuntimeException("An error was thrown "+ao.getMessageID());
      }
  }
  
  public void deleteEntry(String bindDN, String password, String dn) {
      InternalClientConnection conn = InternalClientConnection.getRootConnection();
      conn=authenticate(bindDN,password, conn);
      DeleteOperation delo = conn.processDelete(dn);
      if (delo.getResultCode().getIntValue() != 0) {
          System.err.println("message "+delo.getErrorMessage()+" num "+delo.getMessageID());
          throw new RuntimeException("An error was thrown "+delo.getMessageID());
      }
  }


  private InternalClientConnection authenticate(String bindDN, String password, InternalClientConnection con) {
      InternalClientConnection conn = con;
      AuthenticationInfo authInf = null;
      if(bindDN != null) {
           ByteString bsBindDN = ByteStringFactory.create(bindDN);
           ByteString bsPassword = ByteStringFactory.create(password);
           BindOperation bind = conn.processSimpleBind(bsBindDN, bsPassword);
           authInf = bind.getAuthenticationInfo();
           conn = new InternalClientConnection(authInf);
      }
      return conn;
  }
  
public Object search(String bindDN, String password, String baseDN, int scope,
                       String filter) {
      InternalClientConnection conn = InternalClientConnection.getRootConnection();
      conn = authenticate(bindDN, password, conn);
      SearchScope searchScope = null;
      switch (scope) {
        case BASE_OBJECT:
         searchScope = SearchScope.BASE_OBJECT;
         break;
        case SINGLE_LEVEL:
         searchScope = SearchScope.SINGLE_LEVEL;
         break;
        case SUBORDINATE_SUBTREE: 
         searchScope = SearchScope.SUBORDINATE_SUBTREE;
         break;
        case WHOLE_SUBTREE:
         searchScope = SearchScope.WHOLE_SUBTREE;
         break;
      }
      InternalSearchOperation op;
      try {
          op = conn.processSearch(baseDN, searchScope,
                  DereferencePolicy.DEREF_ALWAYS, 0, 0,
                  false, filter, new LinkedHashSet<String>(0));
      } catch (DirectoryException e) {
          e.printStackTrace();
          throw new RuntimeException(e);
       }
      return op.getSearchEntries();  
  }

  public void updateEntry(String bindDN, String password, String dn, Map<String,Integer> types, List<String> attTypes, List<String> avals) {
      InternalClientConnection conn = InternalClientConnection.getRootConnection();
      conn = authenticate(bindDN, password, conn);
      List<RawModification> mods = new ArrayList<RawModification>();
      for (int i = 0; i < attTypes.size(); i++) {
          String key = attTypes.get(i);
          if (types.get(key) != NONE) {
              ModificationType type = types.get(key) == DELETE ? ModificationType.DELETE : null;
              type = types.get(key) == ADD ? ModificationType.ADD : type;
              type = types.get(key) == UPDATE ? ModificationType.REPLACE : type;
              
              RawModification rm = type == ModificationType.DELETE ? RawModification.create(type, key) :
                                                                     RawModification.create(type, key, avals.get(i));
              mods.add(rm);
          }
      }  
      
      ModifyOperation mo = mods.size() > 0 ? conn.processModify(dn, mods) : null;
      if (mo != null && mo.getResultCode().getIntValue() != 0) {
          System.err.println("message "+mo.getErrorMessage()+" num "+mo.getMessageID());
          throw new RuntimeException("An error was thrown "+mo.getMessageID());
      }      
    
  }
  
  public void importLDIF(String bindDN, String password, String name, String file) {
      try {
          InternalClientConnection conn = InternalClientConnection.getRootConnection();
          conn = authenticate(bindDN, password, conn);
          Backend be = DirectoryServer.getBackend(name);

          InputStream stream = new BufferedInputStream(new FileInputStream(file));
          LDIFImportConfig ldic = new LDIFImportConfig(stream);

          TaskUtils.disableBackend(name);
          be.importLDIF(ldic);
          TaskUtils.enableBackend(name);
      } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e);
      }
  }


  public void deleteAttribute(String bindDN, String password, String dn, String attribute) {
      this.deleteAttribute(bindDN, password, dn, attribute,null);
  }
  
    public void deleteAttribute(String bindDN, String password, String dn, String attribute, String value) {
        InternalClientConnection conn = InternalClientConnection.getRootConnection();
        conn = authenticate(bindDN, password, conn);
        List<RawModification> mods = new ArrayList<RawModification>();
       
            ModificationType type = ModificationType.DELETE;  
            
            RawModification rm = value == null  ? RawModification.create(type, attribute) : RawModification.create(type, attribute, value);
            mods.add(rm); 
        
        ModifyOperation mo = conn.processModify(dn, mods);
        if (mo.getResultCode().getIntValue() != 0) {
            System.err.println("message "+mo.getErrorMessage()+" num "+mo.getMessageID());
            throw new RuntimeException("An error was thrown "+mo.getMessageID());
        }
        
    }

}
