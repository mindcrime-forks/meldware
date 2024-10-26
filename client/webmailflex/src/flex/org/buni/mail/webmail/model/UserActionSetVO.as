package org.buni.mail.webmail.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;
   	import mx.collections.ArrayCollection;

   	[Bindable]
	public class UserActionSetVO implements com.adobe.cairngorm.vo.ValueObject
	{		
		public var name : String;
		public var all: Boolean;
        public var actions: ArrayCollection;
        public var conditions: ArrayCollection;
        
        public var selectedAction: ActionVO;
        public var selectedCondition: ConditionVO;
        import mx.utils.ObjectUtil;
        
        public function UserActionSetVO(uas:Object=null) {
            trace("constructing UserActionSetVO");
            if (uas != null && !(uas is String)) {
                trace("UserActionSet was not null");
                this.name = uas.name; 
                this.all = uas.all;
                if (uas.hasOwnProperty("condition")) {
                    if (uas.condition is ArrayCollection) {
                        trace("condition is Arraycollection");
                        this.conditions = new ArrayCollection();
                        for(var i:int = 0; i < uas.condition.length;i++) {
                        	this.conditions.addItem(new ConditionVO(uas.condition[i]));
                        }
                    } else if (uas.condition != null) {
                  	    trace("condition is not array collection but is not null");
                        this.conditions = new ArrayCollection();
                        this.conditions.addItem(new ConditionVO(uas.condition));
                    }
                }
                
                if (uas.hasOwnProperty("action")) {
                
                    if (uas.action is ArrayCollection) {
                        trace("action is Arraycollection");
                        this.actions = new ArrayCollection();
                        for(i = 0;i < uas.action.length; i++) {
                        	this.actions.addItem(new ActionVO(uas.action[i]));
                        }
                    } else if (uas.action != null) {
                  	    trace("action is not array collection but is not null");
                        this.actions = new ArrayCollection();
                        this.actions.addItem(new ActionVO(uas.action));
                    }
                    this.selectedAction = actions.getItemAt(0) as ActionVO;
                }
            } else if (uas is String) {
   	        	    this.name=uas as String;
   	        	    trace("uas was string and now name is "+this.name);
                    this.actions = new ArrayCollection();
        	        this.conditions = new ArrayCollection();
        	        this.all = false;
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
        
        public function toObject():Object {
        	var o:Object = new Object();
        	o.name = name;
        	o.all = all;
        	o.actions = new ArrayCollection();
        	o.conditions = new ArrayCollection();
        	for (var i:int = 0; i < actions.length; i++) {
        		o.actions.add(actions.getItemAt(i).toObject);
        	}
        	for (i = 0; i < conditions.length; i++) {
        		o.conditions.add(conditions.getItemAt(i).toObject);
        	}
        	return o;
        }
        
        public function conditionString():String {
        	var s:String = new String();
        	for (var i:int = 0; i < conditions.length; i++) {
        		s+= conditions.getItemAt(i).toXML();
        	}
        	return s;
        }
        
        public function actionString():String {
        	var s:String = new String();
        	for (var i:int = 0; i < actions.length; i++) {
        		trace("actionString element "+i);
        		trace(ObjectUtil.toString(actions.getItemAt(i)));
        		s+= actions.getItemAt(i).toXML();
        	}
        	return s;
        }
        
        public function toXML():String {
        	var s:String = new String();
        	s += "<userActionSet>";
        	s += "<name>"+name+"</name>";
        	s += "<all>"+all+"</all>";
        	s += actionString();
        	s += conditionString();
        	s += "</userActionSet>";
        	return s;
        }
	}
}

