package org.buni.mail.webmail.model
{	
	
	/**
	 * 0 CONFIRMED
1 CANCELLED
2 TENTATIVE
3 NEEDS_ACTION
4 COMPLETED
5 IN_PROCESS
6 DRAFT
7 FINAL 
	 */
	public class Status
	{
		private static var _status:Status;
		private static var _statusValues:Array=
		                      [
		                        {label:"Confirmed",data:0},
		                        {label:"Cancelled",data:1},
		                        {label:"Tentative",data:2},
		                        {label:"Needs_Action",data:3},
		                        {label:"Completed",data:4},
		                        {label:"In_Process",data:5},
		                        {label:"Draft",data:6},
		                        {label:"Final",data:7}
		                      ];
		
		public function Status() {
			
		}
		
		public static function getInstance():Status {
			if(_status == null) {
				_status = new Status();
			}
			return _status;
		}
		
		[Bindable]
		public function get statusValues():Array {
			return _statusValues;
		}
		
		public function getValueForCode(code:Number):String {
			return _statusValues[code].label;
		}
		
		public function getCodeForValue(value:String):Number {
			var capValue:String = value.toUpperCase();
			var retval:Number = 0;
			for each (var val:Object in _statusValues) {
				var capVal:String = val.label.toUpperCase();
				if (capVal == capValue) {
					return retval;
				}
				retval++;
			}
			return -1; 
		}
	}
}