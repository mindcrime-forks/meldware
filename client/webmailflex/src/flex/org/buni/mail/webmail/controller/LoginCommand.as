package org.buni.mail.webmail.controller
{  
  import flash.events.EventDispatcher;
  
  import mx.controls.Alert;
  import mx.rpc.IResponder;
  import mx.rpc.events.FaultEvent;
  import mx.rpc.events.ResultEvent;
  
  import com.adobe.cairngorm.commands.ICommand;
  import com.adobe.cairngorm.control.CairngormEvent;
  import com.adobe.cairngorm.commands.SequenceCommand;
  
  import org.buni.mail.webmail.controller.WebmailController;
  import org.buni.mail.webmail.controller.LoginDelegate;
  import org.buni.mail.webmail.model.UserVO;
  import org.buni.mail.webmail.model.ModelLocator;
  
  /**
   * A command that attempts to log a user into the application
   * @author Scotty Scott
   * @author Aron Sogor
   * @author James Ward
   * @author Mike Barker 
   * 
   */
  public class LoginCommand extends SequenceCommand implements ICommand, IResponder
  {
  
    /**
     * Executes the command
     * @param event event.data should be an instance of UserVO
     * @see org.buni.mail.webmail.model.UserVO
     * 
     */
    override public function execute(event:CairngormEvent):void
    {
      var delegate:LoginDelegate = new LoginDelegate(this);
      delegate.login(event.data);
    }

    /**
     * Gets called on a success response from executing the command.
     * <p>Fires WebmailController.GET_USER</p>
     * @param event
     * 
     */
    public function result(event:Object):void
    {
      var model:ModelLocator = ModelLocator.getInstance();
      
      nextEvent = new CairngormEvent(WebmailController.GET_USER);
      executeNextCommand();
    }

    public function fault(event:Object):void
    {
      Alert.show("LoginICommand: " + event.fault.toString());
    }
  }
  
}
