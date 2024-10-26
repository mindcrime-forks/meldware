package org.buni.mail.webmail.controller
{
	import flash.utils.*;

	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.ContactVO;
	import org.buni.mail.webmail.model.Const;
	
	/**
	 * A command for viewing a contact
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker
	 * 
	 */
	public class ViewContactCommand implements ICommand
	{
	
	   /**
	    * Executes the command
	    * <p>Creates a tab on the model for the contact</p>
	    * @param event event.data is expected to be an instance of ContactVO
	    * @see org.buni.mail.webmail.model.ContactVO
	    * 
	    */
	   public function execute( event : CairngormEvent ) : void
	   {
         var model:ModelLocator = ModelLocator.getInstance();
         var copyObj:ContactVO = new ContactVO(event.data as ContactVO);
         copyObj.tabid = (new Date()).getTime();
         copyObj.status = Const.VIEW_CONTACT;
         model.tabs.addItem(copyObj as ContactVO);
	   }

	}
	
}
