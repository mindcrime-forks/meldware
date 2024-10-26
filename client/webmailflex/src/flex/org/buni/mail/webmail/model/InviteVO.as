package org.buni.mail.webmail.model
{
	import com.adobe.cairngorm.vo.ValueObject;
	import mx.core.IUID;
	
   	[Bindable]
	public class InviteVO implements ValueObject, IUID
	{
	    public static const TENATIVE:Number = 0;
        public static const ACCEPTED:Number = 1;
        public static const DECLINED:Number = 2;
        public static const CANCELED:Number = 3;
        
        public var id : Number;
		public var status : Number;
		public var userName : String;
		public var isMine : Boolean;
        public var version : Number;
        public var event : EventVO;
		
		public function InviteVO(o:Object=null,e:EventVO=null)
		{
			if (o != null)
			{
				id = new Number(o.inviteId);
				status = new Number(o.status);
				version = new Number(o.version);
				userName = o.userName;
				isMine = new Boolean(o.isMine);
            } else {
                 status = TENATIVE;
            }
            this.event = e;
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
			return userName.toString();
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
