package org.buni.mail.webmail.controller
{	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.controls.Alert;
	import mx.collections.ArrayCollection;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.commands.SequenceCommand;
	
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.FolderVO;
	import org.buni.mail.webmail.util.FaultUtil;
	
	/**
	 * A command for deleting emails that are flagged for deletion
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 * 
	 */
	public class EmptyTrashCommand extends SequenceCommand implements ICommand, IResponder
	{
	
      /**
       * Executes the command
       * @param event
       * 
       */
      override public function execute(event:CairngormEvent):void
	  {
	    var delegate:EmailDelegate = new EmailDelegate(this);
	    delegate.emptyTrash();
	  }

	  /**
	   * Gets called on a success response from executing the command.
	   * <p>Fires WebmailController.GET_EMAIL_IN_FOLDER for the "Trash" folder</p>
	   * @param event
	   * 
	   */
	  public function result(event:Object):void
	  {
	    nextEvent = new CairngormEvent(WebmailController.GET_EMAIL_IN_FOLDER);
      	nextEvent.data = new FolderVO({name: "Trash", path: "Trash"});
      	executeNextCommand();
	  }

	  public function fault(event:Object):void
	  {
	    FaultUtil.checkFault(event as FaultEvent);
	  }
	}
	
}
