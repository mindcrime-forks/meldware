package org.buni.meldware.admin.controller
{
  import mx.controls.Alert;
  
  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.meldware.admin.model.ProtocolVO;
  import org.buni.meldware.admin.model.ModelLocator;

  public class ProtocolDelegate
  {
    private var responder:Responder;
    private var service:Object;

    public function ProtocolDelegate(responder:Responder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.responder = responder;
    }

    public function getProtocols():void
    {
      trace("in get protocols");
      var params:Object = new Object();
      params.op = "GetProtocols";
      
      trace("sending get protocols");
      var call:Object = service.send(params);
      trace("sent get protocols");
      call.resultHandler = responder.onResult;
      call.faultHandler = responder.onFault;
    }
    
    public function deleteProtocol(protocol:ProtocolVO):void {
        var parms:Object = new Object();
        parms.name = protocol.name;
        parms.op = "DeleteProtocol";
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }   
    
    
    public function addProtocol(protocol:ProtocolVO):void {
        var parms:Object = new Object();
     
        parms.op = "AddProtocol";
        parms.name = protocol.name;
        parms.type = protocol.type;
        parms.serverName = protocol.serverName;
		parms.domainGroup = protocol.domainGroup;
        parms.listenerChain = protocol.listenerChain;
        parms.userRepository = protocol.userRepository;
        parms.mailboxManager = protocol.mailboxManager;
        parms.mailbodyManager = protocol.mailbodyManager;
        parms.APOPEnabled = protocol.APOPEnabled;
        parms.APOPUserRepository = protocol.APOPUserRepository;
        parms.sslSecurityDomain = protocol.sslSecurityDomain;
        parms.authRequired = protocol.authRequired;
        parms.authAllowed = protocol.authAllowed;
        parms.verifyIdentity = protocol.verifyIdentity;
        parms.maxMessageSize = protocol.maxMessageSize;
        parms.blockSize = protocol.blockSize;
        parms.enableTls = protocol.enableTls;
        parms.requireTls = protocol.requireTls;
        parms.requireTlsForAuth = protocol.requireTlsForAuth;
        parms.requireClientCert = protocol.requireClientCert;
        parms.postMasterAddress = protocol.postMasterAddress;
        parms.maxReceivedHeaders = protocol.maxReceivedHeaders;
        parms.receivedHeadersThreshold = protocol.receivedHeadersThreshold;
        parms.maxOwnReceivedHeaders = protocol.maxOwnReceivedHeaders;
    	
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }    
    

    public function editProtocol(protocol:ProtocolVO):void {
        var parms:Object = new Object();
     
        parms.op = "EditProtocol";        
        parms.name = protocol.name;
        parms.type = protocol.type;
        parms.serverName = protocol.serverName;
		parms.domainGroup = protocol.domainGroup;
        parms.listenerChain = protocol.listenerChain;
        parms.userRepository = protocol.userRepository;
        parms.mailboxManager = protocol.mailboxManager;
        parms.mailbodyManager = protocol.mailbodyManager;
        parms.APOPEnabled = protocol.APOPEnabled;
        parms.APOPUserRepository = protocol.APOPUserRepository;
        parms.sslSecurityDomain = protocol.sslSecurityDomain;
        parms.authRequired = protocol.authRequired;
        parms.authAllowed = protocol.authAllowed;
        parms.verifyIdentity = protocol.verifyIdentity;
        parms.maxMessageSize = protocol.maxMessageSize;
        parms.blockSize = protocol.blockSize;
        parms.enableTls = protocol.enableTls;
        parms.requireTls = protocol.requireTls;
        parms.requireTlsForAuth = protocol.requireTlsForAuth;
        parms.requireClientCert = protocol.requireClientCert;
        parms.postMasterAddress = protocol.postMasterAddress;
        parms.maxReceivedHeaders = protocol.maxReceivedHeaders;
        parms.receivedHeadersThreshold = protocol.receivedHeadersThreshold;
        parms.maxOwnReceivedHeaders = protocol.maxOwnReceivedHeaders;
    	
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }    
    
    public function resetModel():void
    {
      var model:ModelLocator = ModelLocator.getInstance();

      //model.setServices(null);
    }
  }
}
