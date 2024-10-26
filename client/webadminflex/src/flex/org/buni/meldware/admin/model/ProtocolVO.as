package org.buni.meldware.admin.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;

   	[Bindable]
	public class ProtocolVO implements com.adobe.cairngorm.vo.ValueObject
	{		
		public var name : String;
		public var type : String;
		public var serverName : String;
		public var domainGroup : String;
        public var listenerChain : String;
        public var userRepository : String;
        public var mailboxManager : String;
        public var mailbodyManager : String;
        public var APOPEnabled : Boolean;
        public var APOPUserRepository : String;
        public var sslSecurityDomain : String;
        public var authRequired : Boolean;
        public var authAllowed : Boolean;
        public var verifyIdentity : Boolean;
        public var maxMessageSize : Number;
        public var blockSize : Number;
        public var enableTls : Boolean;
        public var requireTls : Boolean;
        public var requireTlsForAuth : Boolean;
        public var requireClientCert : Boolean;
        public var postMasterAddress : String;
        public var maxReceivedHeaders : Number;
        public var receivedHeadersThreshold : Number;
        public var maxOwnReceivedHeaders : Number;
        
        public function ProtocolVO(protocol:Object=null) {
            trace("constructing ProtocolVO");
            if (protocol != null) {
                trace("Protocol was not null");
                this.name = protocol.name;
                        trace("tried to set name");

                        trace("name is " + this.name);   
                this.type = protocol.type;                     
                this.domainGroup = protocol.domainGroup;
                this.listenerChain = protocol.listenerChain;
                this.userRepository = protocol.userRepository;
                this.mailboxManager = protocol.mailboxManager;
                this.mailbodyManager = protocol.mailbodyManager;
                this.sslSecurityDomain = protocol.sslSecurityDomain;
                this.serverName = protocol.serverName;
                this.authRequired = protocol.authRequired;
                this.authAllowed = protocol.authAllowed;
                this.verifyIdentity = protocol.verifyIdentity;
                this.maxMessageSize = protocol.maxMessageSize;
                this.blockSize = protocol.blockSize;
                this.enableTls = protocol.enableTls;
                this.requireTls = protocol.requireTls;
                this.requireTlsForAuth = protocol.requireTlsForAuth;
                this.requireClientCert = protocol.requireClientCert;
                this.postMasterAddress = protocol.postMasterAddress;
                this.maxReceivedHeaders = protocol.maxReceivedHeaders;
                this.receivedHeadersThreshold = protocol.receivedHeadersThreshold;
                this.maxOwnReceivedHeaders = protocol.maxOwnReceivedHeaders;
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

