package org.buni.mail.webmail.controller
{
	
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.http.HTTPService;
	
	import org.buni.mail.webmail.model.EventVO;
	import org.buni.mail.webmail.model.InviteVO;
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.UserVO;

	/**
	 * Delegate responsible for handling all server interactions related to a Calendar.
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 * 
	 */
	public class CalendarDelegate
	{
		/**
		 * Contructor
		 * @param responder All operations on this delegate will call the IResponder for processing results
		 * @return 
		 * 
		 */
		public function CalendarDelegate(responder:IResponder)
		{
			this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
			this.responder = responder;
		}

		/**
		 * Gets all events that are related to a day
		 * @param theday The day you want to get events for.
		 * 
		 */
		public function getDaysEvent(theday:Date):void
		{
			var params:Object = new Object();
			params.op = "getDayEvents";
			params.day = theday.time;
			
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}
		
		/**
		 * Saves an instance of the event
		 * @param event
		 * 
		 */
		public function saveEvent(event:EventVO):void
		{
			var params:Object = new Object();
			params.op = "updateEvent";
			serializeEvent(event,params);
			
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}
		
		/**
		 * Creates an event
		 * @param event
		 * 
		 */
		public function createEvent(event:EventVO):void
		{
			var params:Object = new Object();
			var model:ModelLocator = ModelLocator.getInstance();
			
			params.op = "createEvent";
			params.users = model.user.defaultAlias;
			serializeEvent(event,params);
			
			for each (var invite:InviteVO in event.invites) {
                 params.users += "," + invite.userName;
             }
             			
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}
		
		/**
		 * Changes the invite status
		 * @param invite
		 * 
		 */
		public function changeInviteStatus(invite:InviteVO):void
		{
			var params:Object = new Object();
			params.op = "changeInviteStatus";
			params.id = invite.id;
			params.status = invite.status;

			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}
		
		/**
		 * Adds invites to the related user(s) and event
		 * @param invites Array of InviteVO
		 * @see org.buni.mail.webmail.model.InviteVO
		 */
		public function addInvites(invites:Array):void
		{
			var params:Object = new Object();
			params.op = "addInvites";

			params.users = "";
			
			for each (var invite:InviteVO in invites) {
                if (params.users == "")
                {
                    params.users = invite.userName;
                    params.eventId = invite.event.id;
                }
                else
                    params.users += "," + invite.userName;
             }
             
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}
		
		private function serializeEvent(event:EventVO, params:Object):void
		{
			params.startDate = event.startDate.time;
			params.endDate = event.endDate.time;
		    params.title = event.title;
		    params.location = event.location;
		    params.note = event.note;
		    params.eventId = event.id;
		}
		
		private var responder:IResponder;
		private var service:HTTPService;
	}
	
}
