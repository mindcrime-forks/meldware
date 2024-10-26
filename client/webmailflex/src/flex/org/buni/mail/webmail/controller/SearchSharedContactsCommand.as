package org.buni.mail.webmail.controller
{	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.ContactVO;
	import org.buni.mail.webmail.util.FaultUtil;
	
	/**
	 * A command that searches the shared contacts repository
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker
	 * 
	 */
	public class SearchSharedContactsCommand implements ICommand, IResponder
	{
	
	  /**
	   * Executes the command
	   * @param event event.data is expected to be a string of keyword to search for
	   * 
	   */
	  public function execute(event:CairngormEvent):void
	  {
	    var delegate:ContactDelegate = new ContactDelegate(this);
	    delegate.searchSharedContacts(event.data as String);
	  }

	  /**
	   * Gets called on a success response from executing the command.
	   * <p>Takes the results and adds them to the sharedContacts ArrayCollection on the model</p>
	   * @param event event.result.contact should be an ArrayCollection of ContactVO or a single instance of ContactVO
	   * @see org.buni.mail.webmail.model.ContactVO
	   * 
	   */
	  public function result(event:Object):void
	  {
	    var model:ModelLocator = ModelLocator.getInstance();
		  model.sharedContacts = new ArrayCollection();
      var contacts:ArrayCollection;
      
      if (event.result.contact is ArrayCollection)
      {
        contacts = event.result.contact;
      }
      else if (event.result.contact != null)
      {
        contacts = new ArrayCollection();
        contacts.addItem(event.result.contact);
      }
		  
	    if (contacts != null)
	    {
		    for (var i:int = 0; i < contacts.length; i++)
		    {
		       var contact:ContactVO = new ContactVO(contacts[i]); 
	           model.sharedContacts.addItem(contact);
		    }
	    }
	  }

	public function fault(event:Object):void
	{
	   	FaultUtil.checkFault(event as FaultEvent);
	}
}
	
}
