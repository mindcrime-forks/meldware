package org.buni.meldware.admin.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;
   	import mx.collections.ArrayCollection;

   	[Bindable]
	public class MailboxManagerVO implements com.adobe.cairngorm.vo.ValueObject
	{		
		public var name : String;
        
        public function MailboxManagerVO(mailboxManager:Object=null) {
            trace("constructing MailboxManagerVO");
            if (mailboxManager != null) {
                trace("MailboxManager was not null");
                this.name = mailboxManager.name;        
            }
        }

        public function toString():String {
            trace("toString called on "+this.name);
            if(name == null) {
              return "- ";
            } else {
              return "" + name;
            }
        }
	}
}

