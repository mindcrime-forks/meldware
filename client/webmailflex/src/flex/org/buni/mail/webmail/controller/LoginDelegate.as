package org.buni.mail.webmail.controller
{

  import mx.rpc.IResponder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.mail.webmail.model.UserVO;

  
  import mx.rpc.http.HTTPService;
  import mx.rpc.AsyncToken;

  public class LoginDelegate
  {
    public function LoginDelegate(responder:IResponder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("loginService");
      this.responder = responder;
    }

    public function login(user:UserVO):void
    {
      var params:Object = new Object();
      params.j_username = user.username;
      params.j_password = user.password;
      var call:AsyncToken = service.send(params);
      call.addResponder(responder);

    }

    private var responder:IResponder;
    private var service:HTTPService;
  }
  
}
