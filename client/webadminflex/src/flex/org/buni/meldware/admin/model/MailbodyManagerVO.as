package org.buni.meldware.admin.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;
   	import mx.collections.ArrayCollection;

   	[Bindable]
	public class MailbodyManagerVO implements com.adobe.cairngorm.vo.ValueObject
	{		
		public var name : String;
        
        public function MailbodyManagerVO(mailbodyManager:Object=null) {
            trace("constructing MailbodyManagerVO");
            if (mailbodyManager != null) {
                trace("MailbodyManager was not null");
                this.name = mailbodyManager.name;        
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

