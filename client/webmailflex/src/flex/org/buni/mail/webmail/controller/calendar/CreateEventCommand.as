package org.buni.mail.webmail.controller.calendar
{	
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.controls.Alert;
	import mx.collections.ArrayCollection;
	
	import mx.rpc.IResponder;
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.commands.SequenceCommand;
	
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.CalendarEventVO;
	import qs.calendar.CalendarEvent;
	import org.buni.mail.webmail.model.ModelLocator;
	
	/**
	 * A command for creating a calendar event
	 * @author Scotty Scott
	 * 
	 */
	public class CreateEventCommand extends SequenceCommand implements ICommand, IResponder
	{
	
	  private var oldevent:CalendarEventVO;
	  
	  /**
	   * Executes the command
	   * @param event event.data is expected to be an instance of CalendarEventVO
	   * @see org.buni.mail.webmail.model.CalendarEventVO
	   * 
	   */
	  override public function execute(event:CairngormEvent):void
	  {		
	    var delegate:CalendarDelegate = new CalendarDelegate(this);
	    this.oldevent = (event.data as CalendarEventVO);
	    delegate.createEvent(this.oldevent);
	  }

	  /**
	   * Gets called on a success response from executing the command.
	   * @param result
	   * 
	   */
	  public function result(result:Object):void
	  {
	  	// TODO: update docs
		// find the right event to repleace
		var model:ModelLocator = ModelLocator.getInstance();
		for (var i:int = 0; i < model.calSet.events.length; i++) {
			var event:CalendarEventVO = model.calSet.events[i];
		    if(event.uid == this.oldevent.uid)
			{
	  			// this is blunt assuming it allways works put think positive!
	  			// TODO: handel error codes
				this.oldevent.uid = result.result.iCalendar.iCal.EVENT.UID;
				this.oldevent.isNew = false;
				model.calSet.updateEvent(event,this.oldevent);
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
