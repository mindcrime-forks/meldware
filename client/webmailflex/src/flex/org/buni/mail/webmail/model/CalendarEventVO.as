package org.buni.mail.webmail.model
{
	import com.adobe.cairngorm.vo.ValueObject;
    import mx.collections.ArrayCollection;
	import qs.calendar.CalendarEvent;
	import org.buni.mail.webmail.model.CalendarEventVO;
	
  [Bindable]
	public class CalendarEventVO extends CalendarEvent implements ValueObject
	{
		[ArrayElementType("org.buni.mail.webmail.model.InviteVO")]
		private var _invites:ArrayCollection;
		public var editable:Boolean;
		public var isNew:Boolean;
		public var status:Number;
		
		public function CalendarEventVO()
		{
			super();
			this.uid = "new";
            this.summary = "New Event";
            this.location = "";
            this.description = "";
            this.isNew = true;
            this.status = 0;
            _invites = new ArrayCollection();
            editable = true;
		}
		
		public function get invites():ArrayCollection
		{
			return this._invites;	
		}
		
		public function get durationInMinutes():Number
		{
		  return Math.round((this.end.getTime() - this.start.getTime()) / 60000);
		}
		
		public function get currentUserInvite():CalendarInviteVO
		{
		  	for each (var invite:CalendarInviteVO in _invites) {
				if (invite.isMine)
				{
	  				return invite;
				}
			}
			// is there a way to do an assert??
			// this should never happen
			throw new Error("No user invite in event:" + uid); 
		}
		
		public function clone():CalendarEventVO
		{
			var clone:CalendarEventVO = new CalendarEventVO();
			clone.uid = this.uid;
			clone.allDay = this.allDay;
			clone.calendar = this.calendar;
			clone.start = this.start;
			clone.end = this.end;
			clone.summary = this.summary;
			clone.location = this.location;
			clone.description = this.description;
			clone._invites = this._invites;
			clone.editable = this.editable;
			return clone;
		}
			
	}
}
