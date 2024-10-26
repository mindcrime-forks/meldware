package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
    import com.adobe.cairngorm.control.CairngormEvent;
    import org.buni.meldware.admin.controller.SSLDomainDelegate;
//    import org.buni.meldware.admin.controller.SSLDomainEvent;
    import org.buni.meldware.admin.model.ModelLocator;
    import org.buni.meldware.admin.model.SSLDomainVO;
    
    public class GetSSLDomainsCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public function execute( event : CairngormEvent ) : void {
            //model.ssldomains.isPending = true;
            trace("in GetSSLDomainsCommand execute");
            
            var delegate : SSLDomainDelegate = new SSLDomainDelegate( this );   
            //var ssldomainsEvent : SSLDomainEvent = SSLDomainsEvent( event );  
            delegate.getSSLDomains( );          
            trace("post delegate.getSSLDomainS()");
        }
           
        public function onResult( event : * = null ) : void {    
        	trace("GetSSLDomains onResult");        
            var model:ModelLocator = ModelLocator.getInstance();
                  model.ssldomains = new ArrayCollection();

                  var ssldomains:ArrayCollection;

                  if (event.result.sslDomain is ArrayCollection) {
                  	    trace("ssldomains is Arraycollection");
                        ssldomains = event.result.sslDomain;
                  } else if (event.result.sslDomain != null) {
                  	    trace("SSLDomains is not array collection but is not null");
                        ssldomains = new ArrayCollection();
                        ssldomains.addItem(event.result.sslDomain);
                  }

                  if (ssldomains != null) {
                      trace("ssldomains was not null");
                      trace("ssldomains length ");
                      trace(ssldomains.length);
                      for (var i:int = 0; i < ssldomains.length; i++) {
                          var ssldomain:SSLDomainVO = new SSLDomainVO(ssldomains[i]);
                          model.ssldomains.addItem(ssldomain);
                      }
                  } else {
                      trace("ssldomains was null");
                  }

//            model.ssldomain.ssldomainVO = SSLDomainVO( event.result.hello );
 //           model.ssldomain.isPending = false;

            //model.workflowState = ModelLocator.VIEWING_THREADPOOLS;
       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
            //model.ssldomain.statusMessage = "The server did not like your tone or some error occurred.";
            //model.ssldomain.isPending = false;
        }
    }
}
