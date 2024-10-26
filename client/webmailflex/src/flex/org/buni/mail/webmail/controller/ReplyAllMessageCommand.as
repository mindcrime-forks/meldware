package org.buni.mail.webmail.controller
{
	import flash.utils.*;

	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
    import mx.formatters.DateFormatter;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.buni.mail.webmail.model.EmailVO;
	import org.buni.mail.webmail.model.Const;
	import org.buni.mail.webmail.model.ModelLocator;
    import org.buni.mail.webmail.util.EmailFormatter;
    import org.buni.mail.webmail.model.EmailAddressVO;
	
	/**
	 * A command that creates an email based on an existing email
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker
	 * 
	 */
	public class ReplyAllMessageCommand implements ICommand
	{
	
    /**
     * Executes the command
     * <p>Creates a new email based on an existing email, adds the email to the tabs of the model</p>
     * @param event event.data is expected to be the EmailVO you are replying to
     * 
     */
    public function execute( event : CairngormEvent ) : void
	  {
        var df:DateFormatter = new DateFormatter();
        df.formatString = "EEEE, MMM. D, YYYY at L:NN A";

	    var model:ModelLocator = ModelLocator.getInstance();
	    var e:EmailVO = new EmailVO();
	    e.status = Const.NEW_EMAIL;
	    e.subject = "RE: " + event.data.subject;
        e.body = "\n\nOn " + df.format(event.data.date) + ", " + event.data.sender.toString() + ", wrote:" + EmailFormatter.getEmailReplyText(event.data.body);
	    e.toRecipients = new ArrayCollection();
	    e.toRecipients.addItem(event.data.sender);
	    for (var i:int = 0; i < event.data.toRecipients.length; i++)
	    {
	      e.toRecipients.addItem(event.data.toRecipients[i]);
	    }
	    e.ccRecipients = event.data.ccRecipients;
	    e.sender = new EmailAddressVO(model.user.defaultAlias);
	    e.tabid = (new Date()).getTime();
	    model.tabs.addItem(e);
	  }

	}
	
}
