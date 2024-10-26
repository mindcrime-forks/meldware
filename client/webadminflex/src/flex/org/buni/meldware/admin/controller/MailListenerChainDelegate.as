package org.buni.meldware.admin.controller
{
  import mx.controls.Alert;
  
  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.meldware.admin.model.MailListenerChainVO;
  import org.buni.meldware.admin.model.ModelLocator;

  public class MailListenerChainDelegate
  {
    private var responder:Responder;
    private var service:Object;

    public function MailListenerChainDelegate(responder:Responder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.responder = responder;
    }

    public function getMailListenerChains():void
    {
      trace("in get mailListenerChains");
      var params:Object = new Object();
      params.op = "GetMailListenerChains";
      
      trace("sending get mailListenerChains");
      var call:Object = service.send(params);
      trace("sent get mailListenerChains");
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
