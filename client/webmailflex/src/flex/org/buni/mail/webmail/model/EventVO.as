package org.buni.mail.webmail.model
{
	import com.adobe.cairngorm.vo.ValueObject;
    import mx.collections.ArrayCollection;
	import mx.core.IUID;
	
  [Bindable]
	public class EventVO implements ValueObject, IUID
	{
		public var id : Number;
		public var title : String;
		public var location : String;
		public var note : String;
		public var startDate:Date;
		public var endDate:Date;
		[ArrayElementType("org.buni.mail.webmail.model.InviteVO")]		
		public var invites:ArrayCollection;
		public var displayColNum:Number;
		public var editable:Boolean;
		
		public function EventVO(o:Object=null)
		{
			if (o != null)
			{
				id = o.eventid;
				title = o.title;
				location = o.location;
				note = o.note;
				editable = new Boolean(o.editable);
				
		        if (o.startDate != null)
		        {
		          startDate = new Date(new Number(o.startDate));
		        }
		        if (o.endDate != null)
		        {
		          endDate = new Date(new Number(o.endDate));
		        }
		        
		        invites = new ArrayCollection();
		        
		        if (o.invite != null)
				{
				      var newInvites:ArrayCollection;
				      if (o.invite is ArrayCollection)
				      {
				        newInvites = o.invite;
				      }
				      else if (o.invite != null)
				      {
				        newInvites = new ArrayCollection();
				        newInvites.addItem(o.invite);
				      }
				      for (var i:int = 0; i < newInvites.length; i++)
				      {
				        invites.addItem(new InviteVO(newInvites[i],this));
				      }
		    		}
            }
            else
            {
                 title = "New Event";
                 location = "";
                 note = "";
                 invites = new ArrayCollection();
                 editable = true;
            }
		}
		
		public function get durationInMinutes():Number
		{
		  return Math.round((endDate.getTime() - startDate.getTime()) / 60000);
		}
		
		public function get currentUserInvite():InviteVO
		{
		  	for each (var invite:InviteVO in invites) {
				if (invite.isMine)
				{
	  				return invite;
				}
			}
			// is there a way to do an assert??
			// this should never happen
			throw new Error("No user invite in event:" + id); 
		}
		
		public function get uid():String
		{
		  return id.toString();
		}
		
		public function set uid(n:String):void
		{
		  id = new Number(n);
		}
		
		public function toString():String
		{
			return id.toString() + " - " + title;
		}
			
	}
}
