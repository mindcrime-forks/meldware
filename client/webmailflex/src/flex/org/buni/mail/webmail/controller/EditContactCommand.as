package org.buni.mail.webmail.controller
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.ContactVO;
	
	
	/**
	 * A command that sets up the model's tab for editing a contact
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 * 
	 */
	public class EditContactCommand implements ICommand
	{
	
	   /**
	    * Executes the command
	    * @param event evemt.data is expected to be the ContactVO you want to edit
	    * @see org.buni.mail.webmail.model.ContactVO
	    */
	   public function execute( event : CairngormEvent ) : void
	   {
	     var model:ModelLocator = ModelLocator.getInstance();
	     var copyObj:ContactVO = new ContactVO(event.data as ContactVO);
	     copyObj.tabid = (new Date()).getTime();
	     model.tabs.addItem(copyObj as ContactVO);
	   }

	}
	
}
