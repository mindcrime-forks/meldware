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
package org.buni.meldware.mail.maillist.hn;

import java.util.Iterator;
import java.util.Set;

import org.buni.meldware.mail.maillist.MailList;
import org.buni.meldware.mail.maillist.MailListProperties;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.util.HibernateUtil;
import org.hibernate.Session;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;

/**
 * @author Michael Barker
 * @version $Revision: 1.2 $
 */
public class HnMailList implements MailList
{
   MailListDO mailListDO;

   /**
    * @param mailListDO
    */
   public HnMailList(MailListDO mailListDO)
   {
      this.mailListDO = mailListDO;
   }

   /**
    * @see org.buni.meldware.mail.maillist.MailList#getListAddress()
    */
   public MailAddress getListAddress()
   {
      return MailAddress.parseSMTPStyle(mailListDO.getListEmail());
   }

   /**
    * @see org.buni.meldware.mail.maillist.MailList#getMembers()
    * 
    */
   @Tx(TxType.REQUIRED)
   public MailAddress[] getMembers()
   {
      Session session = HibernateUtil.getSession();
      session.update(mailListDO);
      Set<ListMember> members = mailListDO.getMembers();
      MailAddress[] addresses = new MailAddress[members.size()];
      int idx = 0;
      for (Iterator<ListMember> i = members.iterator(); i.hasNext();)
      {
         String email = i.next().toString();
         addresses[idx++] = MailAddress.parseSMTPStyle(email);
      }
      
      return addresses;
   }

   /**
    * @see org.buni.meldware.mail.maillist.MailList#addMember(org.buni.meldware.mail.message.MailAddress)
    * 
    */
   @Tx(TxType.REQUIRED)
   public void addMember(MailAddress address)
   {
      Session session = HibernateUtil.getSession();
      session.update(mailListDO);
      ListMember member = new ListMember(address);
      member.setList(mailListDO);
      mailListDO.getMembers().add(member);
      session.saveOrUpdate(mailListDO);
   }

   /**
    * @see org.buni.meldware.mail.maillist.MailList#isMember(org.buni.meldware.mail.message.MailAddress)
    * 
    */
   @Tx(TxType.REQUIRED)
   public boolean isMember(MailAddress address)
   {
      Session session = HibernateUtil.getSession();
      session.update(mailListDO);
      return mailListDO.getMembers().contains(address.getRawAddress());
   }

   /**
    * @see org.buni.meldware.mail.maillist.MailList#removeMember(org.buni.meldware.mail.message.MailAddress)
    * 
    */
   @Tx(TxType.REQUIRED)
   public void removeMember(MailAddress address)
   {
      Session session = HibernateUtil.getSession();
      session.update(mailListDO);
      mailListDO.getMembers().remove(address.getRawAddress());
      session.update(mailListDO);
   }

   /**
    * @see org.buni.meldware.mail.maillist.MailList#getProperties()
    */
   public MailListProperties getProperties()
   {
      return mailListDO.getProperties();
   }

   /**
    * @see org.buni.meldware.mail.maillist.MailList#setProperties(org.buni.meldware.mail.maillist.MailListProperties)
    * 
    */
   @Tx(TxType.REQUIRED)
   public void setProperties(MailListProperties properties)
   {
      Session session = HibernateUtil.getSession();
      mailListDO.setProperties(properties);
      session.update(mailListDO);
   }

}
