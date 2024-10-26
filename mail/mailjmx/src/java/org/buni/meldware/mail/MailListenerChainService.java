/**
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc.,
 *
 * Portions of this software are Copyright 2006, JBoss Inc., and 
 * individual contributors as indicated by the @authors tag.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
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
package org.buni.meldware.mail;

import java.util.ArrayList;
import java.util.List;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.buni.meldware.mail.message.Message;
import org.buni.meldware.mail.util.MMJMXUtil;
import org.jboss.logging.Logger;
import org.jboss.system.ServiceMBeanSupport;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * MailListenerChainService aggregates mail listeners into an ordered list and
 * allows them to process mails.
 * 
 * @author Andrew C. Oliver
 * @version $Revision: 1.5 $
 */
public class MailListenerChainService extends ServiceMBeanSupport implements MailListenerChainServiceMBean {
    private static final Logger log = Logger
            .getLogger(MailListenerChainService.class);

    // preserves the key, location
    private List<PositionedListener> listeners;

    public MailListenerChainService() {
        listeners = new ArrayList<PositionedListener>();
    }

    /**
     * @see org.buni.meldware.mail.MailListenerChain#addListener(javax.management.ObjectName)
     */
    public void addListener(ObjectName listenerName) {
        addListener(listenerName, listeners.size());
    }

    /**
     * @see org.buni.meldware.mail.MailListenerChain#addListener(javax.management.ObjectName,
     *      int)
     */
    public void addListener(ObjectName listenerName, int position) {
        MailListener listener = findListener(listenerName);
        PositionedListener plistener = new PositionedListener(listener,
                listenerName.toString());
        listeners.add(position, plistener);
    }

    /**
     * creates a MailListener proxy to an underlying maillistener registered on
     * the JMX bus.
     * 
     * @param listerName
     *            to query the JMX bus
     * @return MailListener representing the passed in ObjectName
     */
    private MailListener findListener(ObjectName listenerName) {
        MailListener listener;
        try {
            listener = (MailListener) MMJMXUtil.getMBean(listenerName,
                    MailListener.class);
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
        return listener;
    }

    /**
     * @see org.buni.meldware.mail.MailListenerChain#removeListener(javax.management.ObjectName)
     */
    public void removeListener(ObjectName listener) {
        for (int k = 0; k < listeners.size(); k++) {
            PositionedListener pl = (PositionedListener) listeners.get(k);
            if (pl.name.equals(listener.toString())) {
                listeners.remove(k);
                break;
            }
        }
    }

    /**
     * @see org.buni.meldware.mail.MailListenerChain#removeListener(int)
     */
    public void removeListener(int position) {
        listeners.remove(position);
    }

    /**
     * @see org.buni.meldware.mail.MailListenerChain#getNumberListeners()
     */
    public int getNumberListeners() {
        return listeners.size();
    }

    /**
     * @see org.buni.meldware.mail.MailListenerChain#listListeners()
     */
    public String[] listListeners() {
        String[] retval = new String[listeners.size()];
        for (int k = 0; k < retval.length; k++) {
            retval[k] = ((PositionedListener) listeners.get(k)).name;
        }
        return retval;
    }

    /**
     * @see org.buni.meldware.mail.MailListenerChain#getListeners()
     */
    public Element getListeners() {
        String listenersList[] = this.listListeners();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {

            e.printStackTrace();
        }
        DOMImplementation impl = builder.getDOMImplementation();

        DocumentType DOCTYPE = impl.createDocumentType("non", "non", "non");
        Document doc = impl.createDocument("non", "non", DOCTYPE);
        Element listeners = doc.createElement("listeners");
        for (int k = 0; k < listenersList.length; k++) {
            Element listener = doc.createElement("listener");
            listener.appendChild(doc.createTextNode(listenersList[k]));
            listeners.appendChild(listener);
        }

        return listeners;
    }

    /**
     * @see org.buni.meldware.mail.MailListenerChain#setListeners(org.w3c.dom.Element)
     */
    public void setListeners(Element listeners) {
        this.listeners = new ArrayList<PositionedListener>();
        NodeList list = listeners.getElementsByTagName("listener");
        int num = list.getLength();
        for (int k = 0; k < num; k++) {
            String listener = list.item(k).getFirstChild().getNodeValue();
            try {
                this.addListener(new ObjectName(listener));
            } catch (MalformedObjectNameException e) {
                throw new RuntimeException(e);
            }
        }
        return;
    }

    /**
     * @see org.buni.meldware.mail.MailListenerChain#processMail(org.buni.meldware.mail.message.Message)
     */
    public Message processMail(Message mail) throws MailException {
        Message msg = mail;
        for (int i = 0; i < listeners.size(); i++) {
            if (msg != null) {
                MailListener listener = ((PositionedListener) listeners.get(i)).listener;
                msg = listener.send(msg);                
            } else {
                break;
            }
        }
        return msg;
    }

    /**
     * required JBoss lifecycle method
     */
    public void start() {
    }

    /**
     * required JBoss lifecycle method
     */
    public void stop() {
    }
}

/**
 * used for the list to associate a name with the listener
 * 
 * @author acoliver
 */
class PositionedListener {
    MailListener listener;

    String name;

    public PositionedListener(MailListener listener, String name) {
        this.listener = listener;
        this.name = name;
    }
}
