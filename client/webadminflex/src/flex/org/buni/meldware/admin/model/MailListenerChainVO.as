package org.buni.meldware.admin.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;
   	import mx.collections.ArrayCollection;

   	[Bindable]
	public class MailListenerChainVO implements com.adobe.cairngorm.vo.ValueObject
	{		
		public var name : String;
        
        public function MailListenerChainVO(mailListenerChain:Object=null) {
            trace("constructing MailListenerChainVO");
            if (mailListenerChain != null) {
                trace("MailListenerChain was not null");
                this.name = mailListenerChain.name;        
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

