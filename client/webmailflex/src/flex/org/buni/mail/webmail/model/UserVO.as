package org.buni.mail.webmail.model
{

	import com.adobe.cairngorm.vo.ValueObject;
	import mx.core.IUID;
	import mx.collections.ArrayCollection;
	
  [Bindable]
	public class UserVO implements ValueObject, IUID
	{
	    public var id : Number;
		public var username:String;
		public var password:String;
		[ArrayElementType("String")]		
		public var aliases:ArrayCollection;
		public var defaultAlias:String;
        public var preferences:UserPreferencesVO;
     //   public var contact:ContactVO;
		
		public function UserVO(user:Object=null)
		{
		  if (user != null)
		  {
		    this.id = user.id;
		    
		    if (user.alias is ArrayCollection)
		    {
		      this.aliases = user.alias;
		    }
		    else if (user.alias != null)
		    {
		      this.aliases = new ArrayCollection();
		      this.aliases.addItem(user.alias);
		    }
		
            this.preferences = new UserPreferencesVO(user.preferences,this.id);
            //this.contact = new ContactVO(user.contact);
            this.username = user.username;
            this.defaultAlias = user.defaultAlias;
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
