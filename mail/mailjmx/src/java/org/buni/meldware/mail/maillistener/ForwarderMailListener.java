/*
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
package org.buni.meldware.mail.maillistener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.buni.meldware.mail.MailException;
import org.buni.meldware.mail.MailListener;
import org.buni.meldware.mail.message.Mail;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.message.Message;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:tom@jboss.org">Tom Elrod</a>
 * @version $Revision: 1.2 $
 */
public class ForwarderMailListener implements ForwarderMailListenerMBean, MailListener
{
   private Map<String,String> forwardConfig = new HashMap<String,String>();

   private static final Logger log = Logger.getLogger(ForwarderMailListener.class);

   // required to be an mbean service
   public void create()
   {
   }

   public void start()
   {
   }

   public void stop()
   {
   }

   public void destroy()
   {
   }

   /**
    * This will set the configuration that should contain the original address and the forward address.
    *
    * @param xml
    */
   public void setConfiguration(Element xml)
   {
      if(xml != null)
      {
         NodeList forwardAttributes = xml.getChildNodes();
         int len = forwardAttributes.getLength();
         for(int x = 0; x < len; x++)
         {
            Node attr = forwardAttributes.item(x);
            if("forward".equals(attr.getNodeName()))
            {
               String name = attr.getAttributes().getNamedItem("originalAddress").getNodeValue();
               String value = attr.getFirstChild().getNodeValue();
               if(value.indexOf('@') != -1)
               {
                  forwardConfig.put(name, value);
               }
               else
               {
                  log.error("Can not accept forward value of " + value + ".  It is not of format user@domain.");
               }
            }
         }
      }
      else
      {
         log.warn("Could not configure ForwarderMailListener.  Configuration is null.");
      }

   }

   /**
    * handle a message.
    *
    * @param msg the message object.
    * @return the message containing what could not be handled by this listener, null if the whole message was consumed
    */
   public Message send(Message msg) throws MailException
   {
      Message retMsg = msg;
      if(msg != null)
      {
         List<MailAddress> forwardMailAddress = new ArrayList<MailAddress>();
         Mail mail = (Mail) msg;
         //MailAddress[] toAddress = mail.getTo();
         //for(int x = 0; x < toAddress.length; x++)
         for (MailAddress currentAddress : mail.getTo())
         {
            //MailAddress currentAddress = toAddress[x];
            String domain = currentAddress.getDomain();
            String user = currentAddress.getUser();
            // check for matching on user, domain, and full address
            String value = (String) forwardConfig.get(domain);
            if(value == null)
            {
               value = (String) forwardConfig.get(user);
            }
            if(value == null)
            {
               value = (String) forwardConfig.get(user + "@" + domain);
            }
            // found match for either user, domain, or full address
            if(value != null)
            {
               MailAddress newAddr = MailAddress.parseSMTPStyle(value);
               forwardMailAddress.add(newAddr);
            }
            else
            {
               forwardMailAddress.add(currentAddress);
            }
         }
         // create new mail based on new list
         //MailAddress[] newAddrs = (MailAddress[]) forwardMailAddress.toArray(new MailAddress[forwardMailAddress.size()]);
         retMsg = new ForwardMail(mail, forwardMailAddress);
      }
      return retMsg;
   }

   public static class ForwardMail extends Mail
   {
      private static final long serialVersionUID = 1L;
      
      List<MailAddress> to;

      ForwardMail(Mail mail, List<MailAddress> to)
      {
         super(mail);
         this.to = to;
      }

      public List<MailAddress> getTo()
      {
         return to;
      }

   }

}