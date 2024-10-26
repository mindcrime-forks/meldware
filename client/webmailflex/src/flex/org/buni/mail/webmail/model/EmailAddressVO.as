package org.buni.mail.webmail.model
{
	import com.adobe.cairngorm.vo.ValueObject;
	import mx.core.IUID;
	import mx.utils.ObjectUtil;
	import mx.utils.ObjectProxy;
	import mx.utils.UIDUtil;
	
  [Bindable]
	public class EmailAddressVO implements ValueObject, IUID
	{
		public var id:String;
		public var name:String;
		public var address:String;
		
		[Bindable]
		public static var model:ModelLocator = ModelLocator.getInstance();
		
		public function EmailAddressVO(o:*)
		{
		  if (o is String)
		  {
		  	parseAddress(o as String);
		  }
		  else
		  {
		    try
		    {
		      name = o.name;
		    }
		    catch (e:Error)
		    {
		    }
		    
		    try
		    {
		      address = o.address;
		    }
		    catch (e:Error)
		    {
		    }
		  }
		  id = UIDUtil.createUID();
		}
		
		public function get uid():String {
			return this.id;
		}
		
		public function set uid(uid:String):void {
			trace("uid now set to "+uid+" was "+id);
			this.id = uid;
		}
		
		public function parseAddress(o:String):void {
		  if (o is String)
		  {
		    // parse the name & email out of the string
		    // this code could be a lot prettier
		    
		    // if there is a space, get the last one and set the name to everything up to that space
		    var i:int = (o as String).lastIndexOf(" ");
		    if (i >= 0)
		    {
		      name = (o as String).substr(0,i);
		      address = (o as String).substr(i + 1);
		    }
		    else
		    {
		      name = "";
		      address = (o as String);
		    }
		    
		    // chop any space from the front of the name
		    while (name.indexOf(" ") == 0)
		    {
		      name = name.substr(1);
		    }
		    
		    // chop any surrounding quotes from the name
		    if (name.indexOf("\"") == 0)
		    {
		      name = name.substr(1);
		    }
		    if (name.indexOf("\"") == (name.length - 1))
		    {
		      name = name.substr(0,name.length - 1);
		    }
		    
		    // chop any surrounding <'s >'s from the address
		    if (address.indexOf("<") >= 0)
		    {
		      address = address.substr(address.indexOf("<") + 1);
		    }
		    if (address.indexOf(">") >= 0) 
		    {
		      address = address.substr(0,address.indexOf(">"));
		    }
		  }

		}
		
		public function set fullAddress(addr:String):void {
			parseAddress(addr);
		}
		
		public function get fullAddress():String {
			return ( (name != null && name.length >0 ) || (address != null && address.length > 0) ) ? 
			        getFormattedEmailAddress() : "";
		}
		
		public function getFormattedEmailAddress():String
		{
		  var s:String = new String();
		  if ((name != null) && (name.length > 0))
		  {
		    s += "\"" + name + "\" ";
		  }
		  s += "<" + address + ">";
		  return s;
		}
		
		public function toString():String
		{
		  return getFormattedEmailAddress();
		}
	}
	
}