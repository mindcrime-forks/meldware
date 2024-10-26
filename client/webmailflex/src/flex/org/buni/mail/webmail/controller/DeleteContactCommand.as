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
	
    import org.buni.mail.webmail.model.ContactVO;
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.util.FaultUtil;
	
	/**
	 * A Command that deletes a contact from the contact list
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 * 
	 */
	public class DeleteContactCommand extends SequenceCommand implements ICommand, IResponder
	{
          /**
		   * Executes the command
           * @param event event.data should be an instance of ContactVO
           * @see org.buni.mail.webmail.model.ContactVO
           */
          override public function execute(event:CairngormEvent):void
          {
            var delegate:ContactDelegate = new ContactDelegate(this);
            delegate.deleteContact(event.data as ContactVO);
          }

          /**
		   * Gets called on a success response from executing the command.
    	   * <p>Fires a WebmailController.GET_CONTACTS</p>
           * @param event
           * 
           */
          public function result(event:Object):void
          {
            nextEvent = new CairngormEvent(WebmailController.GET_CONTACTS);
            executeNextCommand();
          }

          public function fault(event:Object):void
          {
            FaultUtil.checkFault(event as FaultEvent);
          }
	}
}
