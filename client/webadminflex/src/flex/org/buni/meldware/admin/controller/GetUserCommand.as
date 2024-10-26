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
  
  public class GetUserCommand extends SequenceCommand implements Command, Responder
  {
  
    override public function execute(event:CairngormEvent):void
    {
      var delegate:UserDelegate = new UserDelegate(this);   
      delegate.getUser();
    }

    public function onResult(event:*=null):void
    {
      var model:ModelLocator = ModelLocator.getInstance();

      if (event.result.user != null)
      {
      	trace("got a user");
        model.user = new UserVO(event.result.user);
        
        nextEvent = new CairngormEvent(AdminController.GET_SERVICES);
        executeNextCommand();
      }
      else
      {
      	trace("didn't get the user");
        var delegate:UserDelegate = new UserDelegate(this); 
        delegate.resetModel();
      }
    }

    public function onFault(event:*=null):void
    {
      var delegate:UserDelegate = new UserDelegate(this); 
      delegate.resetModel();
    }
    
  }
  
}
