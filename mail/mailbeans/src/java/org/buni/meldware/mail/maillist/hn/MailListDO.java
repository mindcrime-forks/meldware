package org.buni.meldware.mail.maillist.hn;


import static java.lang.Boolean.valueOf;
import static org.buni.meldware.mail.maillist.MailListPropertyConstants.ATTACHMENT_ALLOWED;
import static org.buni.meldware.mail.maillist.MailListPropertyConstants.MEMBERS_ONLY;
import static org.buni.meldware.mail.maillist.MailListPropertyConstants.PREFIX_AUTO_BRACKETED;
import static org.buni.meldware.mail.maillist.MailListPropertyConstants.REPLY_TO_LIST;
import static org.buni.meldware.mail.maillist.MailListPropertyConstants.SUBJECT_PREFIX;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.buni.meldware.mail.maillist.MailListProperties;
import org.buni.meldware.mail.message.MailAddress;

/**
 * Representation of a mail list.
 * 
 * @author Michael Barker
 *
 * @hibernate.class table = "MAILLIST"
 * @version $Revision: 1.4 $
 */
@Entity 
@Table(name = "MAILLIST")
public class MailListDO {
 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic
    private String listEmail;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "list")
    private Set<ListMember> members;

    @Basic
    private boolean replyToList;
 
    @Basic
    private String subjectPrefix;

    @Basic
    private boolean prefixAutoBracketed;
  
    @Basic
    private boolean attachmentAllowed;

    @Basic
    private boolean membersOnly;

    /**
     * Identifier for this mailing list.
     * 
     * @return
     * 
     * @hibernate.id column = "ID" generator-class = "native" type = "long"
     */
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * The email address for this maillist.
     * 
     * @return
     * 
     * @hibernate.property column = "LIST_EMAIL"
     */
    public String getListEmail() {
        return this.listEmail;
    }

    public void setListEmail(String listEmail) {
        this.listEmail = listEmail;
    }

    /**
     * The set of email addresses subsribed to the list.
     * 
     * @return
     * 
     * @hibernate.set table = "MAILLIST_MEMBER" lazy = "true"
     * @hibernate.collection-key column = "MAILLIST_ID"
     * @hibernate.collection-element type = "string" column = "EMAIL" not-null = "true"
     */
    public Set<ListMember> getMembers() {
        return this.members;
    }

    public void setMembers(Set<ListMember> members) {
        this.members = members;
    }

    /**
     * The properties assigned to the mailing list.
     * 
     * @return
     */
    public MailListProperties getProperties() {
        MailListProperties p = new MailListProperties();
        p.setProperty(REPLY_TO_LIST, valueOf(getReplyToList()));
        p.setProperty(SUBJECT_PREFIX, getSubjectPrefix());
        p.setProperty(PREFIX_AUTO_BRACKETED, valueOf(getPrefixAutoBracketed()));
        p.setProperty(ATTACHMENT_ALLOWED, valueOf(getAttachmentAllowed()));
        p.setProperty(MEMBERS_ONLY, valueOf(getMembersOnly()));
        return p;
    }

    public void setProperties(MailListProperties p) {
        setReplyToList(p.getPropertyBool(REPLY_TO_LIST));
        setSubjectPrefix(p.getProperty(SUBJECT_PREFIX));
        setPrefixAutoBracketed(p.getPropertyBool(PREFIX_AUTO_BRACKETED));
        setAttachmentAllowed(p.getPropertyBool(ATTACHMENT_ALLOWED));
        setMembersOnly(p.getPropertyBool(MEMBERS_ONLY));
    }

    /**
     * Whether the reply should go to the list.
     * 
     * @return
     * 
     * @hibernate.property column = "REPLY_TO_LIST"
     */
    public boolean getReplyToList() {
        return this.replyToList;
    }

    public void setReplyToList(boolean replyToList) {
        this.replyToList = replyToList;
    }

    /**
     * The text to prefixed on each mailing message.
     * 
     * @return
     * 
     * @hibernate.property column = "SUBJECT_PREFIX"
     */
    public String getSubjectPrefix() {
        return this.subjectPrefix;
    }

    public void setSubjectPrefix(String subjectPrefix) {
        this.subjectPrefix = subjectPrefix;
    }

    /**
     * Whether the prefix text should be surrounded by brackets
     * 
     * @param subjectPrefix
     * 
     * @hibernate.property column = "PREFIX_AUTO_BRACKETED"
     */
    public boolean getPrefixAutoBracketed() {
        return this.prefixAutoBracketed;
    }

    public void setPrefixAutoBracketed(boolean prefixAutoBracketed) {
        this.prefixAutoBracketed = prefixAutoBracketed;
    }

    /**
     * Whether mails sent to the list may include attachements
     * 
     * @return
     * 
     * @hibernate.property column = "ATTACHMENT_ALLOWED"
     */
    public boolean getAttachmentAllowed() {
        return this.attachmentAllowed;
    }

    public void setAttachmentAllowed(boolean attachmentAllowed) {
        this.attachmentAllowed = attachmentAllowed;
    }

    /**
     * Whether this list is for members only.
     * 
     * @return
     * 
     * @hibernate.property column = "MEMBERS_ONLY"
     */
    public boolean getMembersOnly() {
        return this.membersOnly;
    }

    public void setMembersOnly(boolean membersOnly) {
        this.membersOnly = membersOnly;
    }

    public void addMember(String listAddress) {
        ListMember lm = new ListMember(MailAddress.parseSMTPStyle(listAddress));
        lm.setList(this);
    }
}
