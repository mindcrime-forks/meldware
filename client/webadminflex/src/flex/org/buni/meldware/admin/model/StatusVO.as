package org.buni.meldware.admin.model {

  import com.adobe.cairngorm.vo.ValueObject;
  import mx.core.IUID;
  import mx.collections.ArrayCollection;
    
  [Bindable]
  public class StatusVO implements ValueObject {
      private var _value : Number;
      private var _message: String;
      
      public function StatusVO(status:Object=null) {
      	if (status != null) {
      	    this._value=status.value;
      	    this._message=status.message;
        }
      }
      
      public function get value(): Number {
          return this._value;
      }
      
      public function set value(value:Number):void {
          this._value = value;
      }
      
      public function get message(): String {
          return this._message;
      }
      
      public function set message(message:String):void {
          this._message = message;
      }
      
      public function toString(): String {
          return this._message;
      }
  }
}