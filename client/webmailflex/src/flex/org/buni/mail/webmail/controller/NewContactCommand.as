package org.buni.mail.webmail.controller
{
	import flash.utils.*;

	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.buni.mail.webmail.model.ContactVO;
	import org.buni.mail.webmail.model.ModelLocator;
	
	
	/**
	 * A command for creating a new contact.
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @aithor Mike Barker
	 */
	public class NewContactCommand implements ICommand
	{
	
	   /**
	    * Executes the command
	    * <p>Creates a new contact and adds a tab to the model</p>
	    * @param event
	    * 
	    */
	   public function execute( event : CairngormEvent ) : void
	   {
	     var model:ModelLocator = ModelLocator.getInstance();
	     var c:ContactVO = new ContactVO();
	     c.id = -1;
	     c.tabid = (new Date()).getTime();
	     model.tabs.addItem(c);
	   }

	}
	
}
