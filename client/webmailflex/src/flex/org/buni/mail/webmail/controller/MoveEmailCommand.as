package org.buni.mail.webmail.controller
{	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.commands.SequenceCommand;
	
	import org.buni.mail.webmail.model.UserVO;
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.controller.EmailDelegate;
	import mx.collections.ArrayCollection;
	import org.buni.mail.webmail.model.FolderVO;
	import org.buni.mail.webmail.model.EmailVO;
	import org.buni.mail.webmail.util.FaultUtil;
	
	/**
	 * A command for moving an email from one folder to another
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 * 
	 */
	public class MoveEmailCommand extends SequenceCommand implements ICommand, IResponder
	{

	   /**
	    * Executes the command
	    * <p>event.data.email is expected to contain the EmailVO to be moved<br/>
	    * event.data.toFolder is expected to contain the FolderVO to move the email to<br/>
	    * event.data.fromFolder is expected to contain the FolderVO to move the email from</p>
	    * @param event
	    * @see org.buni.mail.webmail.model.EmailVO
	    * @see org.buni.mail.webmail.model.FolderVO
	    */
	   override public function execute(event:CairngormEvent):void
	   {
	      var delegate:EmailDelegate = new EmailDelegate(this);
	      delegate.moveEmail(event.data.email as EmailVO, event.data.toFolder as FolderVO, event.data.fromFolder as FolderVO);
	   }

	   /**
	    * Gets called on a success response from executing the command.
	    * <p>Fires WebmailController.GET_EMAIL_IN_FOLDER</p>
	    * @param event event.result.complete.fromFolder is expected to be an instance of FolderVO
	    * @see org.buni.mail.webmail.model.FolderVO
	    */
	   public function result(event:Object):void
	   {
	     nextEvent = new CairngormEvent(WebmailController.GET_EMAIL_IN_FOLDER);
	     nextEvent.data = ModelLocator.getInstance().getFolderByPath(event.result.complete.fromFolder);
       executeNextCommand();
	   }

	   public function fault(event:Object):void
	   {
	   		FaultUtil.checkFault(event as FaultEvent);
	   }
	}
	
}
