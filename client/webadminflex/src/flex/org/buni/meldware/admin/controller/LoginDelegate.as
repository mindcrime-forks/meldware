package org.buni.meldware.admin.controller
{

  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.meldware.admin.model.UserVO;

  import mx.controls.Alert;

  public class LoginDelegate
  {
    public function LoginDelegate(responder:Responder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("loginService");
      this.responder = responder;
    }

    public function login(user:UserVO):void
    {
      var params:Object = new Object();
      params.j_username = user.username;
      params.j_password = user.password;
      
      var call:Object = service.send(params);

      call.resultHandler = responder.onResult;
      call.faultHandler = responder.onFault;
    }

    private var responder:Responder;
    private var service:Object;
  }
  
}
