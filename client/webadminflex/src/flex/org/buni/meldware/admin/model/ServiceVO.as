package org.buni.meldware.admin.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;

   	[Bindable]
	public class ServiceVO implements com.adobe.cairngorm.vo.ValueObject
	{
		public var name : String;
		public var address : String;
        public var port : Number;
        public var backlog : Number;
        public var life : Number;
        public var timeout : Number;
        public var protocol : String;
        public var usesSSL: Boolean;
        public var sslDomain : String;
        public var threadPool : String;
        
        public function ServiceVO(service:Object=null) {
            trace("constructing ServiceVO");
            if (service != null) {
                trace("Service was not null");
                this.name = service.name;
                        trace("tried to set name");

                        trace("name is " + this.name);                        
                this.address = service.address
                this.port = service.port;
                this.backlog = service.backlog;
                this.life = service.life;
                this.timeout = service.timeout;
                this.protocol = service.protocol;
                this.usesSSL = service.usesSSL;
                this.sslDomain = service.sslDomain;
                this.threadPool = service.threadPool;
            }
        }

                public function toString():String
                {
                        trace("toString called on "+this.name);
                    if(name == null)
                                return "- ";
                        else
                            return "" + name;
                }


	}
}

