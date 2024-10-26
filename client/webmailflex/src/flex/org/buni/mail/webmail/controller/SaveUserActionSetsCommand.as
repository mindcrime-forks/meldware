package org.buni.mail.webmail.controller {

    import mx.collections.ArrayCollection;
    import mx.rpc.IResponder;
    import mx.rpc.events.ResultEvent;

    import com.adobe.cairngorm.commands.ICommand;        
    import com.adobe.cairngorm.commands.SequenceCommand;
    import com.adobe.cairngorm.control.CairngormEventDispatcher;
    import com.adobe.cairngorm.control.CairngormEvent;

    import org.buni.mail.webmail.controller.UserActionSetDelegate;
    import org.buni.mail.webmail.model.ModelLocator;
    import org.buni.mail.webmail.model.UserActionSetVO;
    
    public class SaveUserActionSetsCommand extends SequenceCommand implements ICommand, IResponder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public override function execute(event:CairngormEvent):void {
            trace("in SaveUserActionSetsCommand execute");
            var edata:Object = event.data as Object;
            var delegate : UserActionSetDelegate = new UserActionSetDelegate( this );  
            var uas:ArrayCollection = edata.actionsets as ArrayCollection;
            var user:String = edata.user as String;
            delegate.saveUserActionSets( uas, user );          
            trace("post delegate.saveUserActionSet()");
        }
           
        public function result(event:Object) : void {    
        	trace("SaveUserActionSets onResult");        
  
            var e:CairngormEvent = new CairngormEvent(WebmailController.GET_USER_ACTION_SETS);               
            CairngormEventDispatcher.getInstance().dispatchEvent(e);
            
            executeNextCommand();
       }
                
       public function fault(event:Object) : void {
           trace('test');
           // model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
