package org.buni.mail.webmail.controller
{	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.commands.SequenceCommand;
	
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.EmailVO;
	import org.buni.mail.webmail.util.FaultUtil;
	
	/**
	 * A command that saves an email as a draft
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker
	 * 
	 */
	public class SaveDraftCommand extends SequenceCommand implements ICommand, IResponder
	{
	
    /**
     * Executes the command
     * <p>Saves the email to the "Drafts" folder</p>
     * @param event event.data is expected to be an instance of EmailVO
     * @see org.buni.mail.webmail.model.EmailVO
     * 
     */
    override public function execute(event:CairngormEvent):void
	  {
	    var model:ModelLocator = ModelLocator.getInstance();
	    var delegate:EmailDelegate = new EmailDelegate(this);
	    delegate.saveToFolder(event.data as EmailVO, model.getFolderByPath("Drafts"));
	  }

	  /**
	   * Gets called on a success response from executing the command.
	   * <p>If event.result.complete exists, removes the tab from the model associated with the Email</p>
	   * <p>Fires WebmailController.GET_EMAIL_IN_FOLDER for the model's selected folder</p>
	   * @param event event.result.complete is expected to be an instance of EmailVO
	   * @see org.buni.mail.webmail.model.EmailVO
	   */
	  public function result(event:Object):void
	  {
	    var model:ModelLocator = ModelLocator.getInstance();
	    if (event.result.complete)
	    {
		    model.removeTabById(event.result.complete.tabid);
	    }
	    nextEvent = new CairngormEvent(WebmailController.GET_EMAIL_IN_FOLDER);
      nextEvent.data = model.selectedFolder;
      executeNextCommand();
	  }

	  public function fault(event:Object):void
	  {
	    FaultUtil.checkFault(event as FaultEvent);
	  }
	}
	
}
