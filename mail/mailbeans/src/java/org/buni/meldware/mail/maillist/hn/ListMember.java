package org.buni.meldware.mail.maillist.hn;


import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.buni.meldware.mail.message.MailAddress;

@Entity
@Table(name = "LIST_MEMBER")
public class ListMember {

   public ListMember() {}
   public ListMember(MailAddress addy) {
      email = addy.getRawAddress();
   }

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private long id;

   @ManyToOne
   private MailListDO list;

   @Basic
   private String email;

   public void setList(MailListDO list) {
      this.list = list;
      list.getMembers().add(this);
   }

   public String toString() {
      return email;
   }
   
    public long getId() {
        return id;
    }
    
    public MailListDO getList() {
        return list;
    }
}
