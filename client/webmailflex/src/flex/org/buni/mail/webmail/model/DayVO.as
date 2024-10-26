package org.buni.mail.webmail.model
{
	import mx.core.IUID;
	import mx.collections.ArrayCollection;
	
	import com.adobe.cairngorm.vo.ValueObject;
	
	import org.buni.mail.webmail.model.EventVO;
	
  [Bindable]
	public class DayVO implements ValueObject, IUID
	{
		public var day : Date;
		public var displayNumCols : int;
		// TODO: Is this of EventVO or CalendarEventVO?
		public var events : ArrayCollection;
		
		public function DayVO(o:Object=null)
		{
			day = new Date();
			events = new ArrayCollection();
			if (o != null)
			{
			}
		}		
		
		public function get uid():String
		{
		  return day.toString();
		}
		
		public function set uid(n:String):void
		{
		  day = new Date(n);
		}	
	}
}