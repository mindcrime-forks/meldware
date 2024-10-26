package org.buni.mail.webmail.controller
{  
  import flash.events.EventDispatcher;
  
  import mx.collections.ArrayCollection;
  import mx.controls.Alert;
  import mx.rpc.IResponder;
  import mx.rpc.events.FaultEvent;
  import mx.rpc.events.ResultEvent;
  
  import com.adobe.cairngorm.commands.ICommand;
  import com.adobe.cairngorm.control.CairngormEvent;
  import com.adobe.cairngorm.commands.SequenceCommand;
  
  import org.buni.mail.webmail.controller.WebmailController;
  import org.buni.mail.webmail.controller.ContactDelegate;
  import org.buni.mail.webmail.model.ContactVO;
  import org.buni.mail.webmail.model.ModelLocator;
  import org.buni.mail.webmail.util.FaultUtil;
  
  /**
   * A command that saves a single instance of a contact
   * @author Scotty Scott
   * @author Aron Sogor
   * @author James Ward
   * @author Mike Barker
   * 
   */
  public class SaveContactCommand extends SequenceCommand implements ICommand, IResponder
  {
  
    /**
     * Executes the command
     * @param event event.data is expected to be an instance of ContactVO
     * @see org.buni.mail.webmail.model.ContactVO
     * 
     */
    override public function execute(event:CairngormEvent):void
    {
      var delegate:ContactDelegate = new ContactDelegate(this);
      delegate.saveContact(event.data as ContactVO);
    }

    /**
     * Gets called on a success response from executing the command.
     * <p>Removes the tab from the model if event.result.complete exists</p>
     * <p>Fires WebmailController.GET_CONTACTS</p>
     * @param event event.result.complete is expected to be an instance of EmailVO
     * 
     */
    public function result(event:Object):void
    {
      var model:ModelLocator = ModelLocator.getInstance();
      if (event.result.complete)
      {
        model.removeTabById(event.result.complete.tabid);
      }
      nextEvent = new CairngormEvent(WebmailController.GET_CONTACTS);
      executeNextCommand();
    }

    public function fault(event:Object):void
    {
      FaultUtil.checkFault(event as FaultEvent);
    }
  }
  
}
