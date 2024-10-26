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
  
  import org.buni.meldware.admin.model.UserVO;
  import org.buni.meldware.admin.model.ModelLocator;
  
  public class LogoutUserCommand extends SequenceCommand implements Command, Responder
  {
  
    override public function execute(event:CairngormEvent):void
    {
      var delegate:UserDelegate = new UserDelegate(this);
      delegate.logoutUser();
    }

    public function onResult(event:*=null):void
    {
      nextEvent = new CairngormEvent(AdminController.GET_USER);
      executeNextCommand();
    }

    public function onFault(event:*=null):void
    {
      Alert.show("LogoutUserCommand: " + event.fault.toString());
    }
  }
  
}
