package org.buni.mail.webmail.model
{
	import com.adobe.cairngorm.vo.ValueObject;
	import mx.core.IUID;
	
   	[Bindable]
	public class ContactVO implements ValueObject, IUID
	{
      public var tabid : Number;
      public var id : Number;
      public var contactName : String;
      public var userName : String;
      public var email : String;
      public var organization :String;
      public var shortName : String;
      public var homePhone : String;
      public var mobilePhone : String;
      public var officePhone : String;
      public var faxNumber : String;
      public var isUserAddress : Boolean;
      public var isOwnContact : Boolean;
      public var status : String;
		
		public function ContactVO(o:Object=null)
		{
			if (o != null)
			{
              id = o.id;
              contactName = o.contactName;
              userName = o.userName;
              email = o.email;
              organization = o.organization;
              shortName = o.shortName;
              homePhone = o.homePhone;
              mobilePhone = o.mobilePhone;
              officePhone = o.officePhone;
              faxNumber = o.faxNumber;
              isUserAddress = o.isUserAddress;
              isOwnContact = o.isOwnContact;
              status = o.status;
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
		
		public function get emailAddress():EmailAddressVO
		{
			return new EmailAddressVO({name: contactName, address: email});
		}
		
		public function toString():String
		{
		    if(userName == null)
				return "- " + emailAddress.getFormattedEmailAddress();
			else
			    return "* " + emailAddress.getFormattedEmailAddress();
		}
			
	}
}