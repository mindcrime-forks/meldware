package org.buni.meldware.admin.model {

  import com.adobe.cairngorm.vo.ValueObject;
  import mx.core.IUID;
  import mx.collections.ArrayCollection;
    
  [Bindable]
  public class UserVO implements ValueObject, IUID {
      public var id : Number;
      private var _username:String;
      private var _password:String;
      private var _defaultAlias:String;
      private var _roles:String;
      private var _aliases:String;
        
      public function UserVO(user:Object=null) {
      	  trace("UserVO constructor");
          if (user != null) {
              trace("UserVO: user was not null");
              this.id = user.num;
              this._username = user.name;
              this._defaultAlias = user.defaultAlias;
              this._roles = user.roles;
              this._aliases = user.aliases;
          }
      }
        
      public function get uid():String {
          return id.toString();
      }
        
      public function set uid(n:String):void {
          id = new Number(n);
      }
      
      public function get username(): String {
      	  return this._username;
      }
      
      public function set username(username:String): void {
      	  this._username = username;
      }
      
      public function get defaultAlias(): String {
      	  return this._defaultAlias;
      }
      
      public function set defaultAlias(defaultAlias:String): void {
      	  this._defaultAlias = defaultAlias;
      }
      
      public function get password(): String {
      	  return this._password;
      }
      
      public function set password(password:String): void {
      	  this._password = password;
      }
      
      public function get aliases(): String {
      	  return this._aliases;
      }
      
      public function set aliases(aliases:String): void {
      	  this._aliases = aliases;
      }

      public function get roles(): String {
      	  return this._roles;
      }
      
      public function set roles(roles:String): void {
      	  this._roles = roles;
      }
      
      public function toString(): String {
      	return "* "+username+" {"+roles+"} {"+aliases+"}";
      }
  }
}
