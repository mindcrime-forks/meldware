package org.buni.mail.webmail.controller.calendar
{	
	import mx.rpc.IResponder;
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.commands.SequenceCommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.buni.mail.webmail.model.CalendarEventVO;
	import org.buni.mail.webmail.model.CalendarInviteVO;
	import org.buni.mail.webmail.model.ModelLocator;
	
	import qs.calendar.CalendarEvent;
	
	/**
	 * A command for RSVPing to a calendar event
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 */
	public class RSVPCommand extends SequenceCommand implements ICommand, IResponder
	{
	
	  private var invite:CalendarInviteVO;
	  
	  /**
	   * Executes the command
	   * @param event event.data is expected to be an instance of CalendarInviteVO
	   * @see org.buni.mail.webmail.model.CalendarInviteVO
	   */
	  override public function execute(event:CairngormEvent):void
	  {		
	    var delegate:CalendarDelegate = new CalendarDelegate(this);
	    this.invite = (event.data as CalendarInviteVO);
	    delegate.rsvpEvent(this.invite);
	  }

	  /**
	   * Gets called on a success response from executing the command.
	   * <p>If the invite status is canceled, remove it from the model's calendarSet</p>
	   * @param result
	   * 
	   */
	  public function result(result:Object):void
	  { 
	    // update invite in event
	    if(invite.status == CalendarInviteVO.CANCELED)
	    {
	    	var model:ModelLocator = ModelLocator.getInstance();
	    	model.calSet.removeEvent(invite.event);
	    }
		executeNextCommand();
	  }

	  public function fault(event:Object):void
	  {
	   	Alert.show(event.fault.toString());
	  }
	}
	
}
