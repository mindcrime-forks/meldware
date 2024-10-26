package org.buni.mail.webmail.controller.calendar
{
	import mx.formatters.SwitchSymbolFormatter;
	
	public class UTCDateFormatter
	{
		private static var dateLenght:int = (4+2+2+1+2+2+2+1);
		
		public static function format(date:Date):String
		{
			var result:String = "";
			result+=date.getUTCFullYear();
			// 0 based month index
			result+=leadingZeros(date.getUTCMonth()+1);
			result+=leadingZeros(date.getUTCDate());
			result+="T";
			result+=leadingZeros(date.getUTCHours());
			result+=leadingZeros(date.getUTCMinutes());
			result+=leadingZeros(date.getUTCSeconds());
			result+="Z";
			return result;
		}
		
		public static function parseUTCDate(date:String):Date
		{
			if(date.length!=dateLenght)
				throw new DateParseError("Date is invalid lenght: " + date.length);
			var year:int = int(date.slice(0,4));
			var month:int = int(date.slice(4,6))-1;
			var day:int = int(date.slice(6,8));
			var hour:int = int(date.slice(9,11));
			var minute:int = int(date.slice(11,13));
			var seconds:int = int(date.slice(13,15));
			return new Date(Date.UTC(year,month,day,hour,minute,seconds));
		}
		
		public static function leadingZeros(number:int):String
		{
			if(number < 10)
				return "0" + number;
			else
				return "" + number;
		}
	}
}