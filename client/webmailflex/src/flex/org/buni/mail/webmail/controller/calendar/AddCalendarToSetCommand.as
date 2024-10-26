package org.buni.mail.webmail.controller.calendar
{	
	import mx.rpc.IResponder;
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.buni.mail.webmail.model.CalendarEventVO;
	import org.buni.mail.webmail.model.CalendarInviteVO;
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.calendar.Calendar;
	
	import qs.utils.DateRange;
	
	// TODO update documentation
	public class AddCalendarToSetCommand implements ICommand, IResponder
	{	  
	  
	  protected var calendar:Calendar = null;
	  private var model:ModelLocator = ModelLocator.getInstance();
	  //protected var dateParser:UTCDateFormatter = new UTCDateFormatter();
	  
	  public function execute(event:CairngormEvent):void
	  {		
	  		  	
	  	
	  	calendar = (event.data as Calendar);
	    
	    var delegate:CalendarDelegate = new CalendarDelegate(this);
	    delegate.loadCalendars([calendar],model.calSet.range.start,model.calSet.range.end);
	  }

	  public function result(event:Object):void
	  {
	    var iCalEvent:Object = event.result.iCalendar.iCal.EVENT;
	    if (iCalEvent is ArrayCollection)
		{
			for each (var o:Object in iCalEvent) 
			{
			    this.calendar.addEvent(parseEvent(o,model.user.defaultAlias));
			}
      	}
      	else if (iCalEvent != null)
      	{
        	this.calendar.addEvent(parseEvent(iCalEvent,model.user.defaultAlias));
      	}
	    
	    model.calSet.addCalendar(this.calendar);	    
	  }

	  public function fault(event:Object):void
	  {
	   	Alert.show(event.fault.toString());
	  }
	  
	  public function parseEvent(o:Object,userName:String):CalendarEventVO
	  {
	  	var result:CalendarEventVO = new CalendarEventVO();
	  	
	  	result.isNew = false;
	  	result.uid = o.UID;
		result.summary = o.SUMMARY;
		result.location = o.LOCATION;
		result.description = o.DESC;
		result.editable = (o.ORGANIZER == userName);
		result.status = (o.STATUS != null ? this.model.status.getCodeForValue(o.STATUS) : 0);
		
		var invite:CalendarInviteVO = new CalendarInviteVO();
		invite.status = parseSTATUS(o.STATUS);  //TODO revisit
		invite.userName = userName;
		invite.isMine = true;
		invite.event = result;
		result.invites.addItem(invite);
				
		if (o.ATTENDEE != null)
		{
			if (o.ATTENDEE is ArrayCollection)
			{
				for each (var i:Object in o.ATTENDEE) 
				{
			    	result.invites.addItem(parseInvite(i,userName,result));
				}
      		}
      		else 
      		{
        		result.invites.addItem(parseInvite(o.ATTENDEE,userName,result));
      		}
		}
		
		if (o.START != null)
		{
			result.start = UTCDateFormatter.parseUTCDate(o.START);
		}
		
		if (o.END != null)
		{
		    result.end = UTCDateFormatter.parseUTCDate(o.END);
		}
		return result;
	  }
	
		public function parseInvite(i:Object,userName:String,event:CalendarEventVO):CalendarInviteVO
		{
			var invite:CalendarInviteVO = new CalendarInviteVO();
			invite.userName = i.value;
			invite.isMine = false;
			invite.status = parsePARSTAT(i.PARTSTAT);
			invite.event = event;
			return invite;
		}
		
		public function parsePARSTAT(status:String): int
		{
		   if(status == "NEEDS-ACTION")
		      return 0;
		   if(status == "ACCEPTED")
		      return 1;
		   if(status == "DECLINED")
		      return 2;
		   return -1;
		}
		
		public function parseSTATUS(status:String): int
		{
		   if(status == "NEEDS-ACTION")
		      return 0;
		   if(status == "CONFIRMED")
		      return 1;
		   return -1;
		}
	}
}
