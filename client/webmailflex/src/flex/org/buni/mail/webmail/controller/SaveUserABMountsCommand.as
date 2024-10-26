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
    
    public class SaveUserABMountsCommand extends SequenceCommand implements ICommand, IResponder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public override function execute(event:CairngormEvent):void {
            trace("in SaveUserABMountsCommand execute");
            var delegate : AddressBookDelegate = new AddressBookDelegate( this );  
            var uam:ArrayCollection = model.userABMounts as ArrayCollection;
            delegate.saveUserABMounts( uam );          
            trace("post delegate.saveUserABMountsCommand()");
        }
           
        public function result(event:Object) : void {    
        	trace("SaveUserABMounts onResult");        
  
            var e:CairngormEvent = new CairngormEvent(WebmailController.GET_USER_AB_MOUNTS);               
            CairngormEventDispatcher.getInstance().dispatchEvent(e);
            
            executeNextCommand();
       }
                
       public function fault(event:Object) : void {
           trace('test');
           // model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
