package org.buni.mail.webmail.model
{
	import com.adobe.cairngorm.vo.ValueObject;
	import mx.core.IUID;
	
   	[Bindable]
	public class SystemABMountVO implements ValueObject, IUID
	{
      public var id : Number;
      private var _name : String;
      private var _description: String;
      private var _local : Boolean;
      
		public function SystemABMountVO(o:Object=null)
		{
			if (o != null)
			{
              id = o.id;
			  this._name = o.name;
			  this._description = o.description;
			  this._local = o.local;
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

		public function get name():String
		{
		  return this._name;
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
			    return "* " + name;
		}
			
	}
}