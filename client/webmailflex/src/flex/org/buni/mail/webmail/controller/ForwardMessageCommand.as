package org.buni.mail.webmail.controller
{
	import mx.controls.Alert;
	
	import org.buni.mail.webmail.model.EmailVO;
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.Const;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import org.buni.mail.webmail.model.EmailAddressVO;
	
	/**
	 * A command that creates a new email, sets up the model's tab for forwarding an email
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 * 
	 */
	public class ForwardMessageCommand implements ICommand
	{
	
	   /**
	    * Executes the command
	    * <p>event.data.subject should be the subject of the new email<br/>
	    * event.data.body should be the body of the new email<br/>
	    * The new email's sender is assigned to the currently logged in user's email address</p>
	    * 
		* @param event
	    * 
	    */
	   public function execute( event : CairngormEvent ) : void
	   {
	     var model:ModelLocator = ModelLocator.getInstance();
	     var e:EmailVO = new EmailVO();
	     e.status = Const.NEW_EMAIL;
	     e.subject = "FW: " + event.data.subject;
	     e.body = "\n\n" + event.data.body;
	     e.sender = new EmailAddressVO(model.user.defaultAlias);
	     e.tabid = (new Date()).getTime();
	     model.tabs.addItem(e);
	   }

	}
	
}
