package org.buni.mail.webmail.controller.calendar
{
	
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.collections.ArrayCollection;
	import mx.formatters.DateFormatter;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.http.HTTPService;
	
	import org.buni.mail.webmail.model.CalendarEventVO;
	import org.buni.mail.webmail.model.CalendarInviteVO;
	import org.buni.mail.webmail.model.ModelLocator;
	
	import qs.calendar.Calendar;
	import mx.collections.ArrayCollection;

	// TODO update documentation
	public class CalendarDelegate
	{
	//	private var dateFormat:UTCDateFormatter = null;
		private var responder:IResponder;
		private var service:HTTPService;
		
		public function CalendarDelegate(responder:IResponder)
		{
			this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
			this.responder = responder;
		//	this.dateFormat = new UTCDateFormatter();
		}
		
		public function createEvent(event:CalendarEventVO):void
		{
			var params:Object = new Object();
			var model:ModelLocator = ModelLocator.getInstance();
			
			// http://docs.sun.com/app/docs/doc/819-2434/6n4nme485?a=view
			params.op = "storeevents";
			params.orgUID = model.user.username;
			params.dtstart = UTCDateFormatter.format(event.start);
			params.dtend = UTCDateFormatter.format(event.end);
		    params.location = event.location;
		    params.summary = event.summary;
		    params.status = event.status;
		    params.desc = event.description;
			params.method=2;		    
			
			if((event.invites != null)&&(event.invites.length > 0))
			{
				// ITIP #2 = REQUEST
				var separtor:String = "";
				for each (var invite:CalendarInviteVO in event.invites) {
					params.attendees += separtor;
					if(model.user.username == invite.userName){
						params.attendees += "PARTSTAT=ACCEPTED^RSVP=TRUE^" + invite.userName;
     				}else{
                		params.attendees += "PARTSTAT=NEEDS-ACTION^RSVP=TRUE^" + invite.userName;
         			}
                	separtor = ";";
            	}
   			}
             			
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}
	
		public function updateEvent(event:CalendarEventVO):void
		{
			var params:Object = new Object();
			var model:ModelLocator = ModelLocator.getInstance();
			var invites:ArrayCollection = event.invites;
			
			// http://docs.sun.com/app/docs/doc/819-2434/6n4nme485?a=view
			params.op = "storeevents";
			params.uid = event.uid;
			params.mod = "1"; // THISONEONLY per repeat rule
			params.dtstart = UTCDateFormatter.format(event.start);
			params.dtend = UTCDateFormatter.format(event.end);
		    params.location = event.location;
		    params.summary = event.summary;
		    params.desc = event.description;
		    params.status = event.status;		    
			params.method=1;
			
			if((invites != null)&&(invites.length > 0))
			{
				// ITIP #2 = REQUEST
				params.method=2;
				var separtor:String = "";
				for each (var invite:CalendarInviteVO in invites) {
					params.attendees += separtor;
                	params.attendees += "PARTSTAT=NEEDS-ACTION^RSVP=TRUE^" + invite.userName;
                	separtor = ";";
            	}
   			}
             			
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}
		
		public function getFreebusy(start:Date,end:Date,invite:CalendarInviteVO):void {
			var params:Object = new Object();
			var model:ModelLocator = ModelLocator.getInstance();
			params.op="get_freebusy.wcap";
			params.appid="meldwareCal";
			params.calid=invite.userName;
			params.busyonly = 1;
			params.dtstart = UTCDateFormatter.format(start);
			params.dtend = UTCDateFormatter.format(end);
			//params.fmt-out = "text/xml";
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}
		
		public function loadCalendars(calendars:Array,start:Date,end:Date):void
		{
			var params:Object = new Object();
			var model:ModelLocator = ModelLocator.getInstance();
			var calids:String = "";
			
			params.op = "fetchcomponents_by_range";
             		
            // see http://docs.sun.com/app/docs/doc/819-2434/6n4nme47i?a=view
            params.dtstart = UTCDateFormatter.format(start);
			params.dtend = UTCDateFormatter.format(end);
		    params.calid = calids;
		     			
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}
		
		public function rsvpEvent(invite:CalendarInviteVO):void
		{
			var params:Object = new Object();
			var model:ModelLocator = ModelLocator.getInstance();
			
			// see http://docs.sun.com/app/docs/doc/819-2434/6n4nme46q?a=view
			params.op = "storeevents";
			params.calid = "notused";
			params.uid = invite.event.uid;
			params.method = 4;
			params.attendees = "PARTSTAT=" + invite.formatStatus() + "^RSVP=TRUE^" + invite.userName;
			
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
        }
	}
	
}