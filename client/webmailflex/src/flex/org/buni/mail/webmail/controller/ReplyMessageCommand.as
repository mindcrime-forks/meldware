package org.buni.mail.webmail.controller
{
	import flash.utils.*;

	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
    import mx.formatters.DateFormatter;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;

	import org.buni.mail.webmail.model.EmailVO
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.Const;
    import org.buni.mail.webmail.util.EmailFormatter;
    import org.buni.mail.webmail.model.EmailAddressVO;

	
	public class ReplyMessageCommand implements ICommand
	{
	
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
	     e.sender = new EmailAddressVO(model.user.defaultAlias);
	     e.tabid = (new Date()).getTime();
	     model.tabs.addItem(e);
	   }

	}
	
}
