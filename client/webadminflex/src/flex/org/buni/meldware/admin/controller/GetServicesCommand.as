package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
    import com.adobe.cairngorm.control.CairngormEvent;
    import org.buni.meldware.admin.controller.ServiceDelegate;
//    import org.buni.meldware.admin.controller.ServiceEvent;
    import org.buni.meldware.admin.model.ModelLocator;
    import org.buni.meldware.admin.model.ServiceVO;
    
    public class GetServicesCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public function execute( event : CairngormEvent ) : void {
            //model.services.isPending = true;
            trace("in GetServicesCommand execute");
            
            var delegate : ServiceDelegate = new ServiceDelegate( this );   
            //var servicesEvent : ServiceEvent = ServicesEvent( event );  
            delegate.getServices( );          
            trace("post delegate.getServiceS()");
        }
           
        public function onResult( event : * = null ) : void {    
        	trace("GetServices onResult");        
            var model:ModelLocator = ModelLocator.getInstance();
                  model.setServices(new ArrayCollection());

                  var services:ArrayCollection;

                  if (event.result.service is ArrayCollection) {
                  	    trace("services is Arraycollection");
                        services = event.result.service;
                  } else if (event.result.service != null) {
                  	    trace("Services is not array collection but is not null");
                        services = new ArrayCollection();
                        services.addItem(event.result.service);
                  }

                  if (services != null) {
                      trace("services was not null");
                      trace("services length ");
                      trace(services.length);
                      for (var i:int = 0; i < services.length; i++) {
                          var service:ServiceVO = new ServiceVO(services[i]);
                          model.getServices().addItem(service);
                      }
                  } else {
                      trace("services was null");
                  }

//            model.service.serviceVO = ServiceVO( event.result.hello );
 //           model.service.isPending = false;

            //model.workflowState = ModelLocator.VIEWING_SERVICES;
       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
            //model.service.statusMessage = "The server did not like your tone or some error occurred.";
            //model.service.isPending = false;
        }
    }
}
