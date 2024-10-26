package org.buni.meldware.admin.controller
{
  import mx.controls.Alert;
  
  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.meldware.admin.model.ServiceVO;
  import org.buni.meldware.admin.model.ModelLocator;

  public class ServiceDelegate
  {
    private var responder:Responder;
    private var service:Object;

    public function ServiceDelegate(responder:Responder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.responder = responder;
    }

    public function getServices():void
    {
      trace("in get services");
      var params:Object = new Object();
      params.op = "GetServices";
      
      trace("sending get services");
      var call:Object = service.send(params);
      trace("sent get services");
      call.resultHandler = responder.onResult;
      call.faultHandler = responder.onFault;
    }
    
    public function deleteService(servicevo:ServiceVO):void {
        var parms:Object = new Object();
        parms.servicename = servicevo.name;
        parms.op = "DeleteService";
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }   


    public function addService(servicevo:ServiceVO):void {
        var parms:Object = new Object();
        parms.name = servicevo.name;
        parms.address = servicevo.address
        parms.port = servicevo.port;
        parms.backlog = servicevo.backlog;
        parms.life = servicevo.life;
        parms.timeout = servicevo.timeout;
        parms.protocol = servicevo.protocol;
        parms.usesSSL = servicevo.usesSSL;
        parms.sslDomain = servicevo.sslDomain;
        parms.threadPool = servicevo.threadPool;
        parms.op = "AddService";
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }       

    public function editService(servicevo:ServiceVO):void {
        var parms:Object = new Object();
        parms.name = servicevo.name;
        parms.address = servicevo.address
        parms.port = servicevo.port;
        parms.backlog = servicevo.backlog;
        parms.life = servicevo.life;
        parms.timeout = servicevo.timeout;
        parms.protocol = servicevo.protocol;
        parms.usesSSL = servicevo.usesSSL;
        parms.sslDomain = servicevo.sslDomain;
        parms.threadPool = servicevo.threadPool;
        parms.op = "EditService";
    	
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
