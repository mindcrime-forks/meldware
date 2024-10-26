package org.buni.mail.webmail.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;
   	import mx.collections.ArrayCollection;

   	[Bindable]
	public class ConditionVO implements com.adobe.cairngorm.vo.ValueObject
	{		
		public var headerName : String;
		public var conditionString : String;
		public var conditionSymbol : String;
		public var value : String;
		
        
        public function ConditionVO(condition:Object=null) {
            trace("constructing ConditionVO");
            if (condition != null) {
                trace("Condition was not null");
                this.headerName = condition.headerName;
                this.conditionString = condition.conditionString;
                this.conditionSymbol = condition.conditionSymbol;
                this.value = condition.value; 
            } else {
            	this.headerName="";
            	this.conditionSymbol="";
            	this.conditionString=""; 
            	this.value="";
            }
        }

        public function toString():String {
            trace("toString called on "+this.headerName+" condition");
            if(headerName == null) {
              return "";
            } else {
              return "" + headerName+" "+conditionSymbol+" "+value;
            }
        }
        
        public function toObject():Object {
        	var o:Object = new Object();
        	o.headerName = headerName;
        	o.conditionString = conditionString;
        	o.conditionSymbol = conditionSymbol;
        	o.value = value;
        	return o;
        }
        
        public function toXML():String {
        	var s:String = new String();
        	s += "<condition><headerName>"+this.headerName+"</headerName>";
        	s += "<conditionSymbol>"+this.conditionSymbol+"</conditionSymbol>";
        	s += "<conditionString>"+this.conditionString+"</conditionString>";
        	s += "<value>"+this.value+"</value></condition>";
            return s;
        }
	}
}

