package org.buni.mail.webmail.model
{
	import org.buni.mail.webmail.model.EmailAddressVO;


  [Bindable]
	public class RecipientAddressVO extends EmailAddressVO
	{
		public static var TO:int = 0;
		public static var CC:int = 1;
		public static var BCC:int = 2;
		
		private var _type:int;
		public function RecipientAddressVO(address:EmailAddressVO, type:int)
		{
			super(address);
			this._type = type;
		}
		
		public function set type(typeNum:int):void {
			trace("type was set in "+ this.uid +" RecipientAddressVO "+ this.fullAddress +" to "+typeNum);
			this._type = typeNum;
		}
		
		public function get type():int {
			return this._type;
		}
		
		public function set typeString(tString:String):void {
			if (tString == "To:") {
				this._type = TO;
			} else if (tString == "Bcc:") {
				this._type = BCC;
			} else if (tString == "Cc:") {
				this._type = CC;
			}
		}
		
		public function get typeString():String {
			var retval:String = null;
			switch(type) {
				case TO:
				  retval = "To:";
				  break;
				case CC:
				  retval = "Cc:";
				  break;
			    case BCC:
			      retval = "Bcc:";
			      break;
			    default:
			      trace("unknown type of RecipientAddressVO");
			      retval = "To:";
			} 
			return retval;
		}
	}
}