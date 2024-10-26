package org.buni.mail.webmail.model
{
	import com.adobe.cairngorm.vo.ValueObject;
	import mx.core.IUID;
	
   	[Bindable]
	public class UserABMountVO implements ValueObject, IUID
	{
      public var id : Number;
      private var _order : Number;
      private var _name : String;
      private var _description: String;
      private var _local : Boolean;
      private var _enabled : Boolean;
      
		public function UserABMountVO(o:Object=null)
		{
			if (o != null && !(o is SystemABMountVO))
			{
              id = o.id;
              this._name = o.name;
              this._description = o.description;
              this._local = o.local;
              this._enabled = o.enabled;
			} else if (o != null && (o is SystemABMountVO)) {
			  var x:SystemABMountVO = o as SystemABMountVO;
			  this._name = x.name;
			  this._description = x.description;
			  this._local = x.local;
			  this.id = x.id;
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
		
		public function get order():Number
		{
		  return _order;
		}
		
		public function set order(n:Number):void
		{
		  _order = n;
		}
	
		public function get name():String
		{
		  return this._name;
		}
		
		public function set enabled(n:Boolean):void
		{
		  _enabled = n;
		}
	
		public function get enabled():Boolean
		{
		  return this._enabled;
		}
		
		public function set name(n:String):void
		{
		  this._name = n;
		}
		
		public function get description():String
		{
		  return this._description;
		}
		
		public function set description(n:String):void
		{
		  this._description = n;
		}	
		
		public function get local():Boolean
		{
		  return this._local;
		} 
		
		public function set local(n:Boolean):void
		{
		  this._local = n;
		}
		
		public function toString():String
		{
			    var enabledString:String = enabled ? "enabled" : "disabled";
			    return "* " + name +" - " + enabledString;
		}
			
	}
}