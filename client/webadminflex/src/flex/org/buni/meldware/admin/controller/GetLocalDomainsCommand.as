package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
    import com.adobe.cairngorm.control.CairngormEvent;
    import org.buni.meldware.admin.controller.LocalDomainDelegate;
    import org.buni.meldware.admin.model.ModelLocator;
    
    public class GetLocalDomainsCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public function execute( event : CairngormEvent ) : void {
  
            trace("in GetLocalDomainsCommand execute");
            
            var delegate : LocalDomainDelegate = new LocalDomainDelegate( this );   
            delegate.getLocalDomains( );          
            trace("post delegate.getLocalDomainS()");
        }
           
        public function onResult( event : * = null ) : void {    
        	trace("GetLocalDomains onResult");        
            var model:ModelLocator = ModelLocator.getInstance();
                  model.localdomains = new ArrayCollection();
                  model.postmaster = event.result.postmaster;

                  var localdomains:ArrayCollection;

                  if (event.result.domain is ArrayCollection) {
                  	    trace("localdomains is Arraycollection");
                        localdomains = event.result.domain;
                  } else if (event.result.domain != null) {
                  	    trace("LocalDomains is not array collection but is not null");
                        localdomains = new ArrayCollection();
                        localdomains.addItem(event.result.domain);
                  }

                  if (localdomains != null) {
                      trace("localdomains was not null");
                      trace("localdomains length ");
                      trace(localdomains.length);
                      for (var i:int = 0; i < localdomains.length; i++) {
                          model.localdomains.addItem(localdomains[i]);//strings
                      }
                  } else {
                      trace("localdomains was null");
                  }
       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
