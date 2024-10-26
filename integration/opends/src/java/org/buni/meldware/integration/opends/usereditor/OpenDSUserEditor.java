package org.buni.meldware.integration.opends.usereditor;

import org.buni.meldware.integration.opends.OpenDS;
import org.buni.meldware.mail.usereditor.UserEditor;

public interface OpenDSUserEditor extends UserEditor {
    void setOpenDS(OpenDS openDS);
    OpenDS getOpenDS();
    
    void setUserRoot(String userDN);
    String getUserRoot();
    
    void setRolesRoot(String rolesDN);
    String getRolesRoot();
    
}
