package org.buni.meldware.mail.abmounts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SystemABMounts implements SystemABMountsService {

    private Map<String, ABMountFactory> handlers = new HashMap<String,ABMountFactory>();
    private Map<String, List<ABMount>> domainMounts = new HashMap<String, List<ABMount>>();
    private Map<String, ABMount> allMounts = new HashMap<String, ABMount>();

    public Set<ABMount> getAllABMounts() {
        Set<ABMount> mounts = new HashSet<ABMount>();
        mounts.addAll(allMounts.values());
        return mounts;
    }
    
    public void addMount(String type, Map<String,String> config, String name, String description, Set<String> domains) {
        ABMountFactory factory = handlers.get(type);
        ABMount mount = factory.create(config, name, description);
        allMounts.put(mount.getName(), mount);
        for (String domain : domains) {
            List<ABMount> mounts = this.domainMounts.get(domain);
            if (mounts == null) {
                mounts = new ArrayList<ABMount>();
            }
            mounts.add(mount);
            this.domainMounts.put(domain, mounts);
        }
    }

    public Set<ABMount> getABMountsForDomains(Set<String> domains) {
        Set<ABMount> retval = new HashSet<ABMount>();
        for (String domain : domains) {
            List<ABMount> dmounts = this.domainMounts.get(domain);
            if (dmounts != null) {
                for (ABMount mount : dmounts) {
                    retval.add(mount);
                }
            }
        }
        return retval;
    }

    public void setHanders(Element element) {
        NodeList list = element.getElementsByTagName("handler");
        handlers.clear();
        for (int k = 0; k < list.getLength(); k++) {
            Node n = list.item(k);
            this.addHandler(getNodeText(n).trim());
        }
    }

    private static String getNodeText(Node e) {
        StringBuffer buf = new StringBuffer();
        int type = e.getNodeType();
        if (type == Node.CDATA_SECTION_NODE || type == Node.TEXT_NODE) {
            buf.append(e.getNodeValue());
        } else if (type == Node.ELEMENT_NODE) {
            NodeList list = e.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node child = list.item(i);
                buf.append(getNodeText(child));
            }
        }
        return buf.toString();
    }
    
    public void setMounts(Element element) {
        NodeList list = element.getElementsByTagName("abmount");
        this.domainMounts.clear();
        for (int k = 0; k < list.getLength(); k++) {
            Node n = list.item(k);
            Element e = (Element)n;
            String type = e.getAttribute("type");
            String name = e.getAttribute("name");
            String description = getNodeText(e.getElementsByTagName("description").item(0));
            Element configElement = (Element) e.getElementsByTagName("configuration").item(0);
            Element domainsElement = (Element) e.getElementsByTagName("domains").item(0);
            Map<String, String> config = makeConfigMap(configElement);
            Set<String> domains = makeDomainSet(domainsElement);
            this.addMount(type, config, name, description, domains);
        }
    }

    private Map<String, String> makeConfigMap(Element element) {
        Map<String,String> config = new HashMap<String,String>();
        NodeList list = element.getElementsByTagName("property");
        for (int k = 0; k < list.getLength(); k++) {
            Element p = (Element) list.item(k);
            String key = p.getAttribute("key");
            String val = p.getAttribute("value");
            config.put(key, val);
        }
        return config;
    }

    private Set<String> makeDomainSet(Element element) {
        Set<String> domains = new HashSet<String>();
        NodeList list = element.getElementsByTagName("domain");
        for (int k = 0; k < list.getLength(); k++) {
            Node n = list.item(k);
            domains.add(getNodeText(n).trim());
        }
        return domains;
    }

    public void addHandler(String classname) {
        Class clazz;
        try {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(classname);
            ABMountFactory factory = (ABMountFactory) clazz.newInstance();
            this.handlers.put(factory.getType(),factory);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Set<String> getHandlerNames() {
        Collection<ABMountFactory> hands = this.handlers.values();
        Set<String> retval = new HashSet<String>();
        for (ABMountFactory factory : hands) {
            retval.add(factory.getClass().getName());
        }        
        return retval;
    }

    public void create() {
    }

    public void destroy() {
    }

    public void start() {
    }

    public void stop() {
    }

    public ABMount getMount(String mountName) {
        
        return allMounts.get(mountName);
    }

}
