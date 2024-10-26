package org.buni.mail.webmail.model
{
	import com.adobe.cairngorm.vo.ValueObject;
	import mx.core.IUID;
	import mx.utils.UIDUtil;
	
   	[Bindable]
	public class CalendarInviteVO implements ValueObject, IUID
	{
	    public static const TENATIVE:Number = 0;
        public static const ACCEPTED:Number = 1;
        public static const DECLINED:Number = 2;
        public static const CANCELED:Number = 3;
        
        public var id : String;
		public var status : Number;
		public var userName : String;
		public var isMine : Boolean;
        public var version : Number;
        public var event : CalendarEventVO;
        public var isNew:Boolean;
		public var interval1 : Boolean=true;
		public var interval2 : Boolean=true;
		public var interval3 : Boolean=true;
		public var interval4 : Boolean=true;
		public var interval5 : Boolean=true;
		public var interval6 : Boolean=true;
		public var interval7 : Boolean=true;
		public var interval8 : Boolean=true;
		public var interval9 : Boolean=true;
		public var interval10 : Boolean=true;
		public var interval11 : Boolean=true;
		public var interval12 : Boolean=true;
		public var interval13 : Boolean=true;
		public var interval14 : Boolean=true;
		public var interval15 : Boolean=true;
		public var interval16 : Boolean=true;
		public var interval17 : Boolean=true;
		public var interval18 : Boolean=true;
		public var interval19 : Boolean=true;
		public var interval20 : Boolean=true;
		public var interval21 : Boolean=true;
		public var interval22 : Boolean=true;
		public var interval23 : Boolean=true;
		public var interval24 : Boolean=true;
		public var interval25 : Boolean=true;
		public var interval26 : Boolean=true;
		
		public function CalendarInviteVO(o:Object=null,e:CalendarEventVO=null)
		{
			if (o != null)
			{
				status = new Number(o.status);
				version = new Number(o.version);
				userName = o.userName;
				isMine = new Boolean(o.isMine);
				if (o.inviteId != null) {
					id = o.inviteId.toString();
				} else {
					id = UIDUtil.getUID(this);
				}
            } else {
                 status = TENATIVE;
            }
            this.event = e;
		}
		
		public function get uid():String
		{
		  return id;
		}
		
		public function set uid(n:String):void
		{
		  id = n;
		}
		
		public function toString():String
		{
			return id.toString();
		} 
		
		public function get formattedStatus():String {
			return this.formatStatus();
		}
		
		public function formatStatus():String
		{
			switch(status)
			{
			case TENATIVE:  return "TENATIVE"; 
			break
			case ACCEPTED:  return "ACCEPTED"; 
			break
			case DECLINED:  return "DECLINED"; 
			break
			case CANCELED:  return "CANCELED"; 
			break
			}
			return "MISSING";
		}
			
	}
}
