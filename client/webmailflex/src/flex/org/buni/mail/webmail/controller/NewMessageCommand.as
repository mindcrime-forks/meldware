package org.buni.mail.webmail.controller
{
	import flash.utils.*;

	import mx.controls.Alert;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.EmailVO;
	import org.buni.mail.webmail.model.DayVO;
	import org.buni.mail.webmail.model.Const;
	import org.buni.mail.webmail.model.EmailAddressVO;
	
	/**
	 * A command for creating a new email
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker
	 * 
	 */
	public class NewMessageCommand implements ICommand
	{
	
	   /**
	    * Executes the command
	    * <p>Creates a new email and adds a tab to the model</p>
	    * @param event
	    * 
	    */
	   public function execute( event : CairngormEvent ) : void
	   {
	     var model:ModelLocator = ModelLocator.getInstance();
	     var e:EmailVO = new EmailVO();
	     e.status = Const.NEW_EMAIL;
	     e.sender = new EmailAddressVO(model.user.defaultAlias);
	     e.tabid = (new Date()).getTime();
	     model.tabs.addItem(e);
	   }

	}
	
}
