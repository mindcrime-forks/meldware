package org.buni.mail.webmail.controller
{
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.controls.Alert;
	import mx.collections.ArrayCollection;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.commands.SequenceCommand;
	
    import org.buni.mail.webmail.model.EmailVO;
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.util.FaultUtil;
	
	/**
	 * A command that deletes a single Email
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker
	 * 
	 */
	public class DeleteMessageCommand extends SequenceCommand implements ICommand, IResponder
	{
          /**
           * Executes the command
           * @param event event.data is expected to be an instance of EmailVO
           * @see org.buni.mail.webmail.model.EmailVO
           * 
           */
          override public function execute(event:CairngormEvent):void
          {
            var delegate:EmailDelegate = new EmailDelegate(this);
            delegate.deleteEmail(event.data as EmailVO);
          }

          /**
           * Gets called on a success response from executing the command.
           * <p>If the result contains an email, I will fire a WebmailController.GET_EMAIL_IN_FOLDER 
           * based on the selectedFolder in the model.</p>
           * @param event
           * 
           */
          public function result(event:Object):void
          {
            var model:ModelLocator = ModelLocator.getInstance();
            if (event.result.email)
            {
              nextEvent = new CairngormEvent(WebmailController.GET_EMAIL_IN_FOLDER);
              nextEvent.data = model.selectedFolder;
              executeNextCommand();
            }
          }

          public function fault(event:Object):void
          {
            FaultUtil.checkFault(event as FaultEvent);
          }
        }
}
