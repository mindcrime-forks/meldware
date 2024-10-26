package org.buni.meldware.admin.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;

   	[Bindable]
	public class SSLDomainVO implements com.adobe.cairngorm.vo.ValueObject
	{		
		public var name: String;
		public var keystoreName: String;
		public var url: String;
		public var password: String;
        
        public function SSLDomainVO(ssldomain:Object=null) {
            trace("constructing SSLDomainVO");
            if (ssldomain != null) {
                trace("SSLDomain was not null");
                this.name = ssldomain.name;
                trace("SSLDomain Name is " + this.name);
                this.keystoreName = ssldomain.keystoreName;
                trace("keystoreName is "+this.keystoreName);
                this.url = ssldomain.url;
                trace("url is "+ this.url);
            }
        }

        public function toString():String {
            trace("toString called on "+this.name);
            if(name == null) {
              return "- ";
            } else {
              return keystoreName;
            }
        }
	}
}

