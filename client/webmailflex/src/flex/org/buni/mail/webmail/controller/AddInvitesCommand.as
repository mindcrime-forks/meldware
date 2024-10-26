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
	
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.ContactVO;
	import org.buni.mail.webmail.util.FaultUtil;
	
	/**
	 * A Command that adds invites to an event
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 * 
	 */
	public class AddInvitesCommand extends SequenceCommand implements ICommand, IResponder
	{
	
		/**
		 * Executes the command
		 * <p>event.data should be an array of InviteVO</p>
		 * @param event 
		 * @see org.buni.mail.webmail.model.InviteVO
		 */
		override public function execute(event:CairngormEvent):void
		{
			var delegate:CalendarDelegate = new CalendarDelegate(this);
			delegate.addInvites(event.data as Array);
		}

	  /**
	   * Gets called on a success response from executing the command.
	   * <p>Fires a WebmailController.GET_DAYEVENTS based on the currently selected day in the model</p>
	   * @param event
	   * 
	   */
	  public function result(event:Object):void
	  {
	        var model:ModelLocator = ModelLocator.getInstance();
	        nextEvent = new CairngormEvent(WebmailController.GET_DAYEVENTS);
	        nextEvent.data = model.selectedDay.day;
	        executeNextCommand();
	  }

		public function fault(event:Object):void
		{
		  FaultUtil.checkFault(event as FaultEvent);
		}
		
  }
	
}
