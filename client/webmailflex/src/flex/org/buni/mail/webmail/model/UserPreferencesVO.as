package org.buni.mail.webmail.model
{

	import com.adobe.cairngorm.vo.ValueObject;
	import mx.core.IUID;
	import mx.collections.ArrayCollection;
	
  [Bindable]
	public class UserPreferencesVO implements ValueObject, IUID
	{
	    public var id : Number;
		public var timeZone:TimeZone;
		
		public function UserPreferencesVO(preferences:Object=null,id:Number=0)
		{
		  this.id = id;
          if (preferences.timeZone != null)
          {
            this.timeZone = Const.getTimeZoneByName(preferences.timeZone);
          }
		}
	
		public function get uid():String
		{
		  return id.toString();
		}
		
		public function set uid(n:String):void
		{
		  id = new Number(n);
		}		
	}
}
