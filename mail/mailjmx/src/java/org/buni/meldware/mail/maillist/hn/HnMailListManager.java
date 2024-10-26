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
import java.util.List;

import org.buni.meldware.mail.maillist.MailList;
import org.buni.meldware.mail.maillist.MailListProperties;
import org.buni.meldware.mail.message.MailAddress;
import org.buni.meldware.mail.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;

/**
 * Hibernate implmentation of the mail list manager.
 * 
 * @author Michael Barker
 * @version $Revision: 1.8 $
 */
public class HnMailListManager implements HnMailListManagerMBean
{

   /**
    * @see org.buni.meldware.mail.maillist.MailListManager#createList(org.buni.meldware.mail.message.MailAddress, org.buni.meldware.mail.maillist.MailListProperties)
    * 
    */
    @Tx(TxType.REQUIRED)
   public boolean createList(MailAddress listAddress, MailListProperties properties)
   {
      Session session = HibernateUtil.getSession();
      MailList mailList = findList(listAddress);
      if (mailList == null)
      {
         MailListDO mailListDO = new MailListDO();
         mailListDO.setListEmail(listAddress.getRawAddress());
         mailListDO.setProperties(properties);
         session.save(mailListDO);
         return true;
      }
      else
      {
         return false;         
      }
   }

   @Tx(TxType.REQUIRED)
   public boolean createList(String listAddress) {
       MailListProperties props = new MailListProperties();
       MailAddress address = MailAddress.parseSMTPStyle(listAddress); 
       return createList(address, props);
   }

   /**
    * @see org.buni.meldware.mail.maillist.MailListManager#deleteList(org.buni.meldware.mail.message.MailAddress)
    * 
    */
    @Tx(TxType.REQUIRED)
   public void deleteList(MailAddress listAddress)
   {
      Session session = HibernateUtil.getSession();
      Criteria crit = session.createCriteria(MailListDO.class);
      crit.add(Expression.eq("listEmail", listAddress.getRawAddress()));
      List mailLists = crit.list();
      for (Iterator i = mailLists.iterator(); i.hasNext();)
      {
         MailListDO mailListDO = (MailListDO) i.next();
         session.delete(mailListDO);
      }
   }

   @Tx(TxType.REQUIRED)
   public void deleteList(String listAddress) {
       MailAddress address = MailAddress.parseSMTPStyle(listAddress); 
       deleteList(address);
   }

   /**
    * @see org.buni.meldware.mail.maillist.MailListManager#findList(org.buni.meldware.mail.message.MailAddress)
    * 
    */
   @Tx(TxType.REQUIRED)
   public MailList findList(MailAddress listAddress)
   {
      MailList mailList = null;
      Session session = HibernateUtil.getSession();
      Criteria crit = session.createCriteria(MailListDO.class);
      crit.add(Expression.eq("listEmail", listAddress.getRawAddress()));
      MailListDO mailListDO = (MailListDO) crit.uniqueResult();
      if (mailListDO != null)
      {
         mailList = new HnMailList(mailListDO);
      }
      return mailList;
   }

   @Tx(TxType.REQUIRED)
   public MailList findList(String listAddress) { 
      MailAddress address = MailAddress.parseSMTPStyle(listAddress); 
      return findList(address);
   }

   @Tx(TxType.REQUIRED)
   public boolean addMember(String listAddress, String member) {
       Session session = HibernateUtil.getSession();
       Criteria crit = session.createCriteria(MailListDO.class);
       crit.add(Expression.eq("listEmail", listAddress));
       MailListDO mailListDO = (MailListDO) crit.uniqueResult(); 

      if (mailListDO == null) { 
         return false;
      }
      mailListDO.addMember(member);
      session.saveOrUpdate(mailListDO);
      return true;
   }

   @Tx(TxType.REQUIRED)
   public boolean removeMember(String listAddress, String member) {
       Session session = HibernateUtil.getSession();
       int i = session.createSQLQuery("delete from LIST_MEMBER where EMAIL = :member and LIST_ID = (select id from MAILLIST where LISTEMAIL = :listAddress)")
                        .setParameter("listAddress", listAddress)
                        .setParameter("member", member).executeUpdate();
       
       return i > 0;
   }

   @Tx(TxType.REQUIRED)
    public List<String> searchLists(String pattern) {
        Session session = HibernateUtil.getSession();
       
        Criteria crit = session.createCriteria(MailListDO.class);
        crit.add(Expression.like("listEmail",pattern)).setProjection(Projections.property("listEmail"));
    
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>)crit.list();
        return result;
    }

   @Tx(TxType.REQUIRED)
    public List<String> searchMembers(String listAddress, String pattern) {
        Session session = HibernateUtil.getSession();
        String query = "select m.email from "+ListMember.class.getName()+" as m where " +
                       "m.list.listEmail = :listAddress and m.email like :pattern";
        Query q = session.createQuery(query).setParameter("listAddress", listAddress).setParameter("pattern", pattern);
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>)q.list();
        return result;
    }

   @Tx(TxType.REQUIRED)
    public void editList(MailList list) {
        Session session = HibernateUtil.getSession();

        Criteria crit = session.createCriteria(MailListDO.class);
        crit.add(Expression.eq("listEmail", list.getListAddress().getRawAddress()));
        MailListDO mailListDO = (MailListDO) crit.uniqueResult();
        mailListDO.setProperties(list.getProperties());
        session.saveOrUpdate(mailListDO);
    }

}
