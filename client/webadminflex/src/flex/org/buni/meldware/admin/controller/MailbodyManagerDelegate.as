package org.buni.meldware.admin.controller
{
  import mx.controls.Alert;
  
  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.meldware.admin.model.MailbodyManagerVO;
  import org.buni.meldware.admin.model.ModelLocator;

  public class MailbodyManagerDelegate
  {
    private var responder:Responder;
    private var service:Object;

    public function MailbodyManagerDelegate(responder:Responder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.responder = responder;
    }

    public function getMailbodyManagers():void
    {
      trace("in get mailbodyManagers");
      var params:Object = new Object();
      params.op = "GetMailbodyManagers";
      
      trace("sending get mailbodyManagers");
      var call:Object = service.send(params);
      trace("sent get mailbodyManagers");
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
