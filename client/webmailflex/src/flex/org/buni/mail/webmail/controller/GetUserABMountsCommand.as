package org.buni.mail.webmail.controller {

    import mx.rpc.IResponder;
    import mx.rpc.events.ResultEvent;

    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.commands.ICommand;        
    import com.adobe.cairngorm.commands.SequenceCommand;
    import com.adobe.cairngorm.control.CairngormEventDispatcher;
    import com.adobe.cairngorm.control.CairngormEvent;
    import org.buni.mail.webmail.controller.AddressBookDelegate;
    import org.buni.mail.webmail.model.ModelLocator;
    import org.buni.mail.webmail.model.UserABMountVO;
    
    public class GetUserABMountsCommand extends SequenceCommand implements ICommand, IResponder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public override function execute(event:CairngormEvent):void {
            trace("in GetUserABMountsCommand execute");
            
            var delegate : AddressBookDelegate = new AddressBookDelegate( this );   
            delegate.getUserABMounts( );          
            trace("post delegate.getUserABMounts()");
        }
           
        public function result(event:Object):void {    
        	trace("GetUserABMounts onResult");         
            var model:ModelLocator = ModelLocator.getInstance();
                  model.userABMounts = new ArrayCollection();

                  var userABMounts:ArrayCollection;
                  if (!event.result.hasOwnProperty("userABMount")) {
                  	    userABMounts = new ArrayCollection();
                  } else if (event.result.userABMount is ArrayCollection) {
                  	    trace("userABMounts is Arraycollection");
                        userABMounts = event.result.userABMount;
                  } else if (event.result.userABMount != null) {
                  	    trace("UserABMou ts is not array collection but is not null");
                        userABMounts = new ArrayCollection();
                        userABMounts.addItem(event.result.userABMount);
                  }

                  if (userABMounts != null) {
                      trace("userABMounts was not null");
                      trace("userABMounts length ");
                      trace(userABMounts.length);
                      for (var i:int = 0; i < userABMounts.length; i++) {
                          var userABMount:UserABMountVO = new UserABMountVO(userABMounts[i]);
                          model.userABMounts.addItem(userABMount);
                      }
                  } else {
                      trace("userABMounts was null");
                  }
              executeNextCommand();
                  

       }
                
       public function fault(event:Object):void {
           // model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
