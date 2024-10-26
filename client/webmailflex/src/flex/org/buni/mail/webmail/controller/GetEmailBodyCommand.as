package org.buni.mail.webmail.controller
{	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.controls.Alert;
	import mx.collections.ArrayCollection;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.EmailVO;
	import org.buni.mail.webmail.util.FaultUtil;
	
	/**
	 * A command for getting the body of an email
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 * 
	 */
	public class GetEmailBodyCommand implements ICommand, IResponder
	{
	
    /**
     * Executes the command
     * <p>Only loads an email's body that has not currently been loaded</p>
     * @param event event.data is expected to be an instance of EmailVO
     * @see org.buni.mail.webmail.model.EmailVO
     */
    public function execute(event:CairngormEvent):void
	  {
	    var delegate:EmailDelegate = new EmailDelegate(this);
	    if ((event.data as EmailVO).body == null)  // don't refetch email bodies
	    {
	      delegate.getEmailBody(event.data as EmailVO);
	    }
	  }

	  /**
	   * Gets called on a success response from executing the command.
	   * <p>Sets the model instance of the email body and attachments to the result</p>
	   * @param event event.result.email is expected to be an instance of EmailVO
	   * 
	   */
	  public function result(event:Object):void
	  {
	  	trace("BEGIN GetEmailBodyCommand onResult");
	    var model:ModelLocator = ModelLocator.getInstance();
		model.setEmailAttachment(event.result.email);
        trace('email.body = ' + event.result.email.body);
		model.setEmailBody(event.result.email);
		  
	  	trace("END GetEmailBodyCommand onResult");
	  }

	  public function fault(event:Object):void
	  {
	    FaultUtil.checkFault(event as FaultEvent);
	  }
	}
	
}
