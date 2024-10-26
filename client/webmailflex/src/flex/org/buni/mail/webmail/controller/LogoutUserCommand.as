package org.buni.mail.webmail.controller
{  
  import flash.events.EventDispatcher;
  
  import mx.rpc.IResponder;
  import mx.rpc.events.FaultEvent;
  import mx.rpc.events.ResultEvent;
  import mx.controls.Alert;
  
  import com.adobe.cairngorm.commands.ICommand;
  import com.adobe.cairngorm.control.CairngormEvent;
  import com.adobe.cairngorm.commands.SequenceCommand;
  
  import org.buni.mail.webmail.model.UserVO;
  import org.buni.mail.webmail.model.ModelLocator;
  import org.buni.mail.webmail.util.FaultUtil;
  
  /**
   * A command that logs out the currently logged in user
   * @author Scotty Scott
   * @author Aron Sogor
   * @author James Ward
   * @author Mike Barker 
   * 
   */
  public class LogoutUserCommand extends SequenceCommand implements ICommand, IResponder
  {
  
    /**
     * Executes the command
     * @param event
     * 
     */
    override public function execute(event:CairngormEvent):void
    {
      var delegate:UserDelegate = new UserDelegate(this);
      delegate.logoutUser();
    }

    /**
     * Gets called on a success response from executing the command.
     * <p>Fires WebmailController.GET_USER</p>
     * @param event
     * 
     */
    public function result(event:Object):void
    {
      nextEvent = new CairngormEvent(WebmailController.GET_USER);
      executeNextCommand();
    }

    public function fault(event:Object):void
    {
      FaultUtil.checkFault(event as FaultEvent);
    }
  }
  
}
