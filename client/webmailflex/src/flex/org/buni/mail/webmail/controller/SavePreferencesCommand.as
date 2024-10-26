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
  
  import org.buni.mail.webmail.model.UserPreferencesVO;
  import org.buni.mail.webmail.model.ModelLocator;
  import org.buni.mail.webmail.util.FaultUtil;
  
  /**
   * A command for saving user preferences
   * @author Scotty Scott
   * @author Aron Sogor
   * @author James Ward
   * @author Mike Barker
   * 
   */
  public class SavePreferencesCommand extends SequenceCommand implements ICommand, IResponder
  {
  
    /**
     * Executes the command
     * @param event event.data is expected to be an instance of UserPreferencesVO
     * @see org.buni.mail.webmail.model.UserPreferencesVO
     * 
     */
    override public function execute(event:CairngormEvent):void
    {
      var delegate:UserDelegate = new UserDelegate(this);
      delegate.savePreferences(event.data as UserPreferencesVO);
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
