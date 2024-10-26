package org.buni.mail.webmail.controller.calendar
{	
	import mx.rpc.IResponder;
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.commands.SequenceCommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.buni.mail.webmail.model.CalendarEventVO;
	import org.buni.mail.webmail.model.CalendarInviteVO;
	import org.buni.mail.webmail.model.ModelLocator;
	
	import qs.calendar.CalendarEvent;
	
	/**
	 * A command for updating an existing calendar event
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 * 
	 */
	public class UpdateEventCommand extends SequenceCommand implements ICommand, IResponder
	{
	
	  private var request:ArrayCollection;
	  
	  /**
	   * Executes the command
	   * @param event event.data is expected to be an ArrayCollection, item 0 should be an CalendarEventVO, item 1 should be an ArrayCollection of CalendarInviteVOs
	   * @see org.buni.mail.webmail.model.CalendarEventVO
	   * @see org.buni.mail.webmail.model.CalendarInviteVO
	   */
	  override public function execute(event:CairngormEvent):void
	  {		
	    var delegate:CalendarDelegate = new CalendarDelegate(this);
	    this.request = (event.data as ArrayCollection);
	    delegate.updateEvent((request.getItemAt(0) as CalendarEventVO));
	  }

	  /**
	   * Gets called on a success response from executing the command.
	   * <p>Merges all invites from the result to the calendar event, updates the model calendar set</p>
	   * @param result result.result is expected to be an ArrayCollection, item 0 should be an CalendarEventVO, item 1 should be an ArrayCollection of CalendarInviteVOs
	   * 
	   * @see org.buni.mail.webmail.model.CalendarEventVO
	   * @see org.buni.mail.webmail.model.CalendarInviteVO
	   * 
	   */
	  public function result(result:Object):void
	  { 
	    var newevent:CalendarEventVO = (request.getItemAt(0) as CalendarEventVO);
	    var invites:ArrayCollection = newevent.invites;//(request.getItemAt(1) as ArrayCollection);
	  	// merge invites
	  	for each (var invite:CalendarInviteVO in invites)
	  	{
	  		invite.isNew = false;
	  	}
	  	// this is blunt assuming it allways works put think positive!
	  	// TODO: handel error codes
		newevent.uid = result.result.iCalendar.iCal.EVENT.UID;
		// find the right event to repleace
		var model:ModelLocator = ModelLocator.getInstance();
		for (var i:int = 0; i < model.calSet.events.length; i++) {
			var event:CalendarEventVO = model.calSet.events[i];
		    if(event.uid == newevent.uid)
			{
				model.calSet.updateEvent(event,newevent);
				i = model.calSet.events.length;
			}
		}
		executeNextCommand();
	  }

	  public function fault(event:Object):void
	  {
	   	Alert.show(event.fault.toString());
	  }
	}
	
}
