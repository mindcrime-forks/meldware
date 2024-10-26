package org.buni.meldware.admin.controller
{
  import mx.controls.Alert;
  
  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.meldware.admin.model.UserRepositoryVO;
  import org.buni.meldware.admin.model.ModelLocator;

  public class UserRepositoryDelegate
  {
    private var responder:Responder;
    private var service:Object;

    public function UserRepositoryDelegate(responder:Responder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.responder = responder;
    }

    public function getUserRepositories():void
    {
      trace("in get userRepositories");
      var params:Object = new Object();
      params.op = "GetUserRepositories";
      
      trace("sending get userRepositories");
      var call:Object = service.send(params);
      trace("sent get userRepositories");
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
