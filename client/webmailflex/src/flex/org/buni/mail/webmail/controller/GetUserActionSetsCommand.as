package org.buni.mail.webmail.controller {

    import mx.rpc.IResponder;
    import mx.rpc.events.ResultEvent;

    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.commands.ICommand;        
    import com.adobe.cairngorm.commands.SequenceCommand;
    import com.adobe.cairngorm.control.CairngormEventDispatcher;
    import com.adobe.cairngorm.control.CairngormEvent;
    import org.buni.mail.webmail.controller.UserActionSetDelegate;
    import org.buni.mail.webmail.model.ModelLocator;
    import org.buni.mail.webmail.model.UserActionSetVO;
    
    public class GetUserActionSetsCommand extends SequenceCommand implements ICommand, IResponder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public override function execute(event:CairngormEvent):void {
            trace("in GetUserActionSetsCommand execute");
            
            var delegate : UserActionSetDelegate = new UserActionSetDelegate( this );   
            delegate.getUserActionSets( );          
            trace("post delegate.getUserActionSet()");
        }
           
        public function result(event:Object):void {    
        	trace("GetUserActionSets onResult");        
            var model:ModelLocator = ModelLocator.getInstance();
                  model.userActionSets = new ArrayCollection();

                  var userActionSet:ArrayCollection;
                  if (!event.result.hasOwnProperty("userActionSet")) {
                  	    userActionSet = new ArrayCollection();
                  } else if (event.result.userActionSet is ArrayCollection) {
                  	    trace("userActionSet is Arraycollection");
                        userActionSet = event.result.userActionSet;
                  } else if (event.result.userActionSet != null) {
                  	    trace("UserActionSet is not array collection but is not null");
                        userActionSet = new ArrayCollection();
                        userActionSet.addItem(event.result.userActionSet);
                  }

                  if (userActionSet != null) {
                      trace("userActionSet was not null");
                      trace("userActionSet length ");
                      trace(userActionSet.length);
                      for (var i:int = 0; i < userActionSet.length; i++) {
                          var userActionSetItem:UserActionSetVO = new UserActionSetVO(userActionSet[i]);
                          model.userActionSets.addItem(userActionSetItem);
                      }
                  } else {
                      trace("userActionSet was null");
                  }
              executeNextCommand();
                  

       }
                
       public function fault(event:Object):void {
           // model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
