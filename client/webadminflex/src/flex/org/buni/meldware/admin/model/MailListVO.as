package org.buni.meldware.admin.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;
   	import mx.collections.ArrayCollection;

   	[Bindable]
	public class MailListVO implements com.adobe.cairngorm.vo.ValueObject
	{		
		public var listAddress : String;
		public var attachmentsAllowed : Boolean;
		public var membersOnly : Boolean;
		public var prefixAutoBracketed : Boolean;
		public var replyToList : Boolean;
		public var members : ArrayCollection;
        
        public function MailListVO(mailList:Object=null) {
            trace("constructing MailListVO");
            if (mailList != null) {
                trace("MailList was not null");
                this.listAddress = mailList.listAddress; 
                if(mailList.members != null) {
                	this.members = mailList.members;
                }
                	this.attachmentsAllowed = mailList.attachmentsAllowed;
                	this.membersOnly = mailList.membersOnly;
                	this.prefixAutoBracketed = mailList.prefixAutoBracketed;
                	this.replyToList = mailList.replyToList;
    
            }
        }

        public function toString():String {
            trace("toString called on "+this.listAddress);
            if(listAddress == null) {
              return "- ";
            } else {
              return "" + listAddress;
            }
        }
	}
}

