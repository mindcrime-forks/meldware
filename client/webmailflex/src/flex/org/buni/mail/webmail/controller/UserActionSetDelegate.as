package org.buni.mail.webmail.controller
{
  import mx.rpc.AsyncToken;
  import mx.rpc.IResponder;
  import mx.controls.Alert;
  import mx.collections.ArrayCollection;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.mail.webmail.model.ModelLocator;

  public class UserActionSetDelegate
  {
    private var responder:IResponder;
    private var service:Object;

    public function UserActionSetDelegate(responder:IResponder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.responder = responder;
    }

    public function getUserActionSets():void
    {
      trace("in get userActionSet");
      var model:ModelLocator = ModelLocator.getInstance();
      if (model.user != null)
      {
        var params:Object = new Object();
        params.op = "getUserActionSets";
        params.user = model.user.username;
        trace("sending get userActionSet");
        var call:AsyncToken = service.send(params);
        call.addResponder(responder);
        trace("sent get userActionSet");
        //call.resultHandler = responder.onResult;
        //call.faultHandler = responder.onFault;
      }
    }

    public function saveUserActionSets(asets:ArrayCollection, user:String):void
    {
      var model:ModelLocator = ModelLocator.getInstance();
      trace("in get userActionSet");
      var params:Object = new Object();
      params.op = "saveUserActionSets";
      params.user = user;
	  var actionset:String = new String();
	  for (var i:int = 0; i < asets.length; i++) {
	  	actionset += asets.getItemAt(i).toXML();
	  } 
      params.actionset = actionset;
      trace("sending save userActionSet");
      var call:AsyncToken = service.send(params);
      call.addResponder(responder);
      trace("sent save userActionSet");
      //call.resultHandler = responder.onResult;
      //call.faultHandler = responder.onFault;
    }
    
    public function resetModel():void
    {
      var model:ModelLocator = ModelLocator.getInstance();

      //model.setServices(null);
    }
  }
}
