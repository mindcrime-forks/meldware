package org.buni.mail.webmail.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;

   	[Bindable]
	public class ActionVO implements com.adobe.cairngorm.vo.ValueObject
	{		
		public var folderName : String;
		public var actionName : String;
		
		public var selectedFolder: String = "/INBOX";

        public function ActionVO(action:Object=null) {
            trace("constructing ActionVO");
            if (action != null) {
                trace("Action was not null");
                this.folderName = action.folderName;
                this.actionName = action.actionName;
                
                this.selectedFolder = folderName;
            } else {
            	this.folderName = "";
            	this.actionName = "";
            }
        }

        public function toString():String {
            trace("toString called on "+this.folderName+" action");
            if(folderName == null) {
              return "- ";
            } else {
              return "" + folderName+" "+actionName;
            }
        }
        
        public function toObject():Object {
        	var o:Object = new Object();
        	o.folderName=folderName;
        	o.actionName=actionName;
        	return o;
        }
        
        public function toXML():String {
        	var s:String = new String();
        	s += "<action><folderName>"+this.folderName+"</folderName>";
        	s += "<actionName>"+this.actionName+"</actionName></action>";
        	return s;
        }
	}
}

