package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.commands.SequenceCommand;
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.adobe.cairngorm.control.CairngormEventDispatcher;
    import org.buni.meldware.admin.controller.ServiceDelegate;
//    import org.buni.meldware.admin.controller.ServiceEvent;
    import org.buni.meldware.admin.controller.AdminController;
    import org.buni.meldware.admin.model.ModelLocator;
    import org.buni.meldware.admin.model.ThreadPoolVO;
    import org.buni.meldware.admin.model.StatusVO;
    
    public class EditThreadPoolCommand extends SequenceCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        override public function execute( event : CairngormEvent ) : void {
            trace("in EditThreadPoolCommand execute");
            
            var delegate : ThreadPoolDelegate = new ThreadPoolDelegate( this );   
            delegate.editThreadPool( event.data );          
        }
           
       public function onResult( event : * = null ) : void {            
           if (event.result.status != null) {
              var status : StatusVO = new StatusVO( event.result.status );
              model.lastStatus = status;
              var e:CairngormEvent = new CairngormEvent(AdminController.THREADPOOL_EDITED);
              CairngormEventDispatcher.getInstance().dispatchEvent(e); 
              executeNextCommand();
           }
       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
            trace("fault");
            trace(event.toString());
        }
    }
}
