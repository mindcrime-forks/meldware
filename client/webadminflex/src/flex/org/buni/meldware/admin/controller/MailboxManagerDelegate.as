package org.buni.meldware.admin.controller
{
  import mx.controls.Alert;
  
  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.meldware.admin.model.MailboxManagerVO;
  import org.buni.meldware.admin.model.ModelLocator;

  public class MailboxManagerDelegate
  {
    private var responder:Responder;
    private var service:Object;

    public function MailboxManagerDelegate(responder:Responder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.responder = responder;
    }

    public function getMailboxManagers():void
    {
      trace("in get mailboxManagers");
      var params:Object = new Object();
      params.op = "GetMailboxManagers";
      
      trace("sending get mailboxManagers");
      var call:Object = service.send(params);
      trace("sent get mailboxManagers");
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
