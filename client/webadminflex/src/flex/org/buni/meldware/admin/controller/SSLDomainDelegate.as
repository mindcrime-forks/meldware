package org.buni.meldware.admin.controller
{
  import mx.controls.Alert;
  
  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.meldware.admin.model.SSLDomainVO;
  import org.buni.meldware.admin.model.ModelLocator;

  public class SSLDomainDelegate
  {
    private var responder:Responder;
    private var service:Object;

    public function SSLDomainDelegate(responder:Responder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.responder = responder;
    }

    public function getSSLDomains():void
    {
      trace("in get ssldomains");
      var params:Object = new Object();
      params.op = "GetSSLDomains";
      
      trace("sending get ssldomains");
      var call:Object = service.send(params);
      trace("sent get ssldomains");
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
