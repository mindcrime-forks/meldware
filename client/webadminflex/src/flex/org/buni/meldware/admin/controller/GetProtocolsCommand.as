package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.adobe.cairngorm.control.CairngormEventDispatcher;
    
    import com.adobe.cairngorm.commands.SequenceCommand;
//    import org.buni.meldware.admin.controller.ProtocolEvent;
    import org.buni.meldware.admin.controller.ProtocolDelegate;
    import org.buni.meldware.admin.model.ModelLocator;
    import org.buni.meldware.admin.model.ProtocolVO;
    
    public class GetProtocolsCommand extends SequenceCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        override public function execute( event : CairngormEvent ) : void {
            //model.protocols.isPending = true;
            trace("in GetProtocolsCommand execute");
            
            var delegate : ProtocolDelegate = new ProtocolDelegate( this );   
            //var protocolsEvent : ProtocolEvent = ProtocolsEvent( event );  
            delegate.getProtocols( );          
            trace("post delegate.getProtocolS()");
        }
           
        public function onResult( event : * = null ) : void {    
        	trace("GetProtocols onResult");        
            var model:ModelLocator = ModelLocator.getInstance();
                  model.protocols = new ArrayCollection();

                  var protocols:ArrayCollection;

                  if (event.result.protocol is ArrayCollection) {
                  	    trace("protocols is Arraycollection");
                        protocols = event.result.protocol;
                  } else if (event.result.protocol != null) {
                  	    trace("Protocols is not array collection but is not null");
                        protocols = new ArrayCollection();
                        protocols.addItem(event.result.protocol);
                  }

                  if (protocols != null) {
                      trace("protocols was not null");
                      trace("protocols length ");
                      trace(protocols.length);
                      for (var i:int = 0; i < protocols.length; i++) {
                          var protocol:ProtocolVO = new ProtocolVO(protocols[i]);
                          model.protocols.addItem(protocol);
                      }
                      var e:CairngormEvent = new CairngormEvent(AdminController.GOT_PROTOCOLS);          		    
                      CairngormEventDispatcher.getInstance().dispatchEvent(e); 
         	          executeNextCommand();
                  } else {
                      trace("protocols was null");
                  }

//            model.protocol.protocolVO = ProtocolVO( event.result.hello );
 //           model.protocol.isPending = false;

            //model.workflowState = ModelLocator.VIEWING_PROTOCOLS;
       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
            //model.protocol.statusMessage = "The server did not like your tone or some error occurred.";
            //model.protocol.isPending = false;
        }
    }
}
