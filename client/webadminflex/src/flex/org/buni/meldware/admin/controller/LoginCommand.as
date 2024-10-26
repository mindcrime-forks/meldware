package org.buni.meldware.admin.controller
{  
  import flash.events.EventDispatcher;
  
  import mx.rpc.events.FaultEvent;
  import mx.rpc.events.ResultEvent;
  import mx.controls.Alert;
  
  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.commands.Command;
  import com.adobe.cairngorm.control.CairngormEvent;
  import com.adobe.cairngorm.commands.SequenceCommand;
  
  import org.buni.meldware.admin.controller.AdminController;
  import org.buni.meldware.admin.controller.LoginDelegate;
  import org.buni.meldware.admin.model.UserVO;
  import org.buni.meldware.admin.model.ModelLocator;
  
  public class LoginCommand extends SequenceCommand implements Command, Responder
  {
  
    override public function execute(event:CairngormEvent):void
    {
      var delegate:LoginDelegate = new LoginDelegate(this);
      delegate.login(event.data);
    }

    public function onResult(event:*=null):void
    {
      var model:ModelLocator = ModelLocator.getInstance();
      
      nextEvent = new CairngormEvent(AdminController.GET_USER);
      executeNextCommand();
    }

    public function onFault(event:*=null):void
    {
      Alert.show("LoginCommand: " + event.fault.toString());
    }
  }
  
}
