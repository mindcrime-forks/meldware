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
  
  import org.buni.mail.webmail.model.UserVO;
  import org.buni.mail.webmail.model.DayVO;
  import org.buni.mail.webmail.model.ModelLocator;
  import org.buni.mail.webmail.model.FolderVO;
  
  /**
   * A command that gets the currently logged in user
   * @author Scotty Scott
   * @author Aron Sogor
   * @author James Ward
   * @author Mike Barker 
   * 
   */
  public class GetUserCommand extends SequenceCommand implements ICommand, IResponder
  {
  
    /**
     * Executes the command
     * @param event
     * 
     */
    override public function execute(event:CairngormEvent):void
    {
      var delegate:UserDelegate = new UserDelegate(this);   
      delegate.getUser();
    }

    /**
     * Gets called on a success response from executing the command.
     * <p>If event.result.user exists then I fire WebmailController.GET_FOLDERS, Webmailcontroller.GET_CONTACTS, 
     * WebmailController.GET_DAYEVENTS otherwise I reset the model</p>
     * @param event event.result.user should be populated if the user is logged in
     * 
     */
    public function result(event:Object):void
    {
      var model:ModelLocator = ModelLocator.getInstance();

      if (event.result.user != null)
      {
        model.user = new UserVO(event.result.user);

        nextEvent = new CairngormEvent(WebmailController.GET_FOLDERS);
        executeNextCommand();
      
        nextEvent = new CairngormEvent(WebmailController.GET_CONTACTS);
        executeNextCommand();
        
        model.selectedDay = new DayVO();
        model.selectedDay.day = new Date();
        // this is a hack to avoid timezone issues
        model.selectedDay.day.setHours(0);
        model.selectedDay.day.setMinutes(0);
        
        nextEvent = new CairngormEvent(WebmailController.GET_DAYEVENTS);
        nextEvent.data = model.selectedDay.day;
        executeNextCommand();
      }
      else
      {
        var delegate:UserDelegate = new UserDelegate(this); 
        delegate.resetModel();
      }
    }

    /**
     * Gets called on a fault response from executing the command. 
     * <p>A fault for this command assumes that the user is not logged in, so we reset the model and ignore the fault</p>
     * @param event
     * 
     */
    public function fault(event:Object):void
    {
      var delegate:UserDelegate = new UserDelegate(this); 
      delegate.resetModel();
    }
    
  }
  
}
