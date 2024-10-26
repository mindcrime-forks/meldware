package org.buni.mail.webmail.controller
{	
	import flash.events.Event;

	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.controls.Alert;
	import mx.collections.ArrayCollection;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.EmailVO;
	import org.buni.mail.webmail.model.FolderVO;
	import org.buni.mail.webmail.util.FaultUtil;
	
	/**
	 * A command for getting all emails in a specific folder
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 * 
	 */
	public class GetEmailInFolderCommand implements ICommand, IResponder
	{
	
    /**
     * Executes the command
     * @param event event.data is expected to be an instance of FolderVO
     * @see org.buni.mail.webmail.model.FolderVO
     */
    public function execute(event:CairngormEvent):void
	  {
	    if (ModelLocator.getInstance().user != null)
	    {
	      var delegate:EmailDelegate = new EmailDelegate(this);
  	    delegate.getEmailInFolder(event.data as FolderVO);
  	  }
	  }

	  /**
	   * Gets called on a success response from executing the command.
	   * <p>event.result.email is expected to contain either an ArrayCollection of EmailVO or a single instance of EmailVO<br/>
	   * event.result.folder can contain a FolderVO, if so I add all event.result.email to the folder</p>
	   * <p>Once the model has been updated with the results, I dispatch "emailsChanged" and "selectedEmailChanged" on the model</p>
	   * @param event
	   * 
	   */
	   // TODO shouldnt the model know when to dispatch these events?
	  public function result(event:Object):void
	  {
	    var model:ModelLocator = ModelLocator.getInstance();

		  if (event.result.folder != null)
		  {
		    var folder:FolderVO = model.getFolderByPath(event.result.folder.path);
		    
		    var emails:ArrayCollection;
		    
		    if (event.result.email is ArrayCollection)
		    {
		      emails = event.result.email;
		    }
		    else if (event.result.email != null)
		    {
		      emails = new ArrayCollection();
		      emails.addItem(event.result.email);
		    }
		    
        folder.emails.removeAll();

        if (emails != null)
        {
  		    for (var i:int = 0; i < emails.length; i++)
  		    {
  		      var e:EmailVO = new EmailVO(emails[i]);
  		      folder.emails.addItem(e);
  	      }
        }
	      model.dispatchEvent(new Event("emailsChanged"));
	      
	      folder.selectedEmailIds.removeAll();
	      
	      if (folder.emails.length > 0)
	      {
	        folder.selectedEmailIds.addItem(folder.emails[0].id);
	      }
	      model.dispatchEvent(new Event("selectedEmailsChanged"));

	    }
	  }

	  public function fault(event:Object):void
	  {
	    FaultUtil.checkFault(event as FaultEvent);
	  }
	  
	}
	
}
