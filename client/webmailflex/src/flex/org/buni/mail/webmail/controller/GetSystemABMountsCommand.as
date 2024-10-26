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
    import org.buni.mail.webmail.model.SystemABMountVO;
    
    public class GetSystemABMountsCommand extends SequenceCommand implements ICommand, IResponder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public override function execute(event:CairngormEvent):void {
            trace("in GetSystemABMountsCommand execute");
            
            var delegate : AddressBookDelegate = new AddressBookDelegate( this );   
            delegate.getSystemABMounts( );          
            trace("post delegate.getSystemABMounts()");
        }
           
        public function result(event:Object):void {    
        	trace("GetSystemABMounts onResult");         
            var model:ModelLocator = ModelLocator.getInstance();
                  model.systemABMounts = new ArrayCollection();

                  var systemABMounts:ArrayCollection;
                  if (!event.result.hasOwnProperty("systemABMount")) {
                  	    systemABMounts = new ArrayCollection();
                  } else if (event.result.systemABMount is ArrayCollection) {
                  	    trace("systemABMounts is Arraycollection");
                        systemABMounts = event.result.systemABMount;
                  } else if (event.result.systemABMount != null) {
                  	    trace("SystemABMounts is not array collection but is not null");
                        systemABMounts = new ArrayCollection();
                        systemABMounts.addItem(event.result.systemABMount);
                  }

                  if (systemABMounts != null) {
                      trace("systemABMounts was not null");
                      trace("systemABMounts length ");
                      trace(systemABMounts.length);
                      for (var i:int = 0; i < systemABMounts.length; i++) {
                          var systemABMount:SystemABMountVO = new SystemABMountVO(systemABMounts[i]);
                          model.systemABMounts.addItem(systemABMount);
                      }
                  } else {
                      trace("systemABMounts was null");
                  }
              executeNextCommand();
                  

       }
                
       public function fault(event:Object):void {
           // model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
