package org.buni.meldware.admin.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;

   	[Bindable]
	public class UserRepositoryVO implements com.adobe.cairngorm.vo.ValueObject
	{		
		public var name : String;
        public var type : String;
        
        public function UserRepositoryVO(userRepository:Object=null) {
            trace("constructing UserRepositoryVO");
            if (userRepository != null) {
                trace("UserRepository was not null");
                this.name = userRepository.name;
                this.type = userRepository.type;
            }
        }

        public function toString():String {
            trace("toString called on "+this.name);
            if(name == null) {
              return "- ";
            } else {
              return "" + name;
            }
        }
	}
}

