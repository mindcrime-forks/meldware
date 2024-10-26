package org.buni.meldware.admin.controller {
	
  import mx.controls.Alert;
  import mx.collections.ArrayCollection;
  
  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.meldware.admin.model.ModelLocator;

  public class LocalDomainDelegate {
    private var responder:Responder;
    private var service:Object;

    public function LocalDomainDelegate(responder:Responder) {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.responder = responder;
    }

    public function getLocalDomains():void {
      trace("in get localdomains");
      var params:Object = new Object();
      params.op = "GetLocalDomains";
      
      trace("sending get localdomains");
      var call:Object = service.send(params);
      trace("sent get localdomains");
      call.resultHandler = responder.onResult;
      call.faultHandler = responder.onFault;
    }
    
    public function saveLocalDomains(postmaster:String,localdomains:ArrayCollection):void {
      var params:Object = new Object();
      params.op = "SetLocalDomains";
      params.domain = localdomains;
      params.postmaster = postmaster;
      
      var call:Object = service.send(params);

      call.resultHandler = responder.onResult;
      call.faultHandler = responder.onFault;
    }
    
    public function resetModel():void {
      var model:ModelLocator = ModelLocator.getInstance();

      //model.setServices(null);
    }
  }
}
