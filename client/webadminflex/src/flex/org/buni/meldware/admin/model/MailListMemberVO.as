package org.buni.meldware.admin.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;
   	import mx.collections.ArrayCollection;

   	[Bindable]
	public class MailListMemberVO implements com.adobe.cairngorm.vo.ValueObject
	{		
		public var address : String;
        
        public function MailListMemberVO(mailListMember:Object=null) {
            trace("constructing MailListMemberVO");
            if (mailListMember != null) {
                trace("MailListMember was not null");
                this.address = mailListMember.address;        
            }
        }

        public function toString():String {
            trace("toString called on "+this.address);
            if(this.address == null) {
              return "- ";
            } else {
              return "" + address;
            }
        }
	}
}

