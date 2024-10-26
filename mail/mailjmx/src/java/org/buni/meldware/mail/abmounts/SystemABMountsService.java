package org.buni.meldware.mail.abmounts;

import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

public interface SystemABMountsService {
    public void setMounts(Element abmountConfig);
    
    public void addMount(String type, Map<String,String> config, String name, String description, Set<String>domains);
    
    public Set<ABMount> getABMountsForDomains(Set<String>domains);
    
    public void setHanders(Element configHandlers);
    
    public void addHandler(String classname);
    public Set<String> getHandlerNames();
    
    Set<ABMount> getAllABMounts();
    
    public void create();
    public void start();
    public void stop();
    public void destroy();

    public ABMount getMount(String mountName);
}

/**
  <abmounts> 
     <abmount type="local">
             <configuration><property key="" value=""/></configuration>
             <domains><domain>buni.org</domain></domains>
     </abmount>
  </abmounts>
*/