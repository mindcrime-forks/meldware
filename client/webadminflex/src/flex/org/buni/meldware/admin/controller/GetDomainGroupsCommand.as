package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
    import com.adobe.cairngorm.control.CairngormEvent;
    import org.buni.meldware.admin.controller.DomainGroupDelegate;
//    import org.buni.meldware.admin.controller.DomainGroupEvent;
    import org.buni.meldware.admin.model.ModelLocator;
    import org.buni.meldware.admin.model.DomainGroupVO;
    
    public class GetDomainGroupsCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public function execute( event : CairngormEvent ) : void {
            //model.domaingroups.isPending = true;
            trace("in GetDomainGroupsCommand execute");
            var local : Boolean = false;
            if (event.data != null) {
            	if (event.data.local == true) {
            		local = true;
            		trace("local == true");
            	}
            }
            
            var delegate : DomainGroupDelegate = new DomainGroupDelegate( this );   
            delegate.getDomainGroups(local);          
            trace("post delegate.getDomainGroupS()");
        }
           
        public function onResult( event : * = null ) : void {    
        	trace("GetDomainGroups onResult");        
            var model:ModelLocator = ModelLocator.getInstance();
            var dgs:ArrayCollection = model.domainGroups;
            if (event.result.localOnly != null && event.result.localOnly == true) {
            	model.localDomainGroups = new ArrayCollection();
            	dgs = model.localDomainGroups;
            } else {
            	model.domainGroups = new ArrayCollection();
            	dgs = model.domainGroups;
            }
            

                  var domaingroups:ArrayCollection;

                  if (event.result.domainGroup is ArrayCollection) {
                  	    trace("domaingroups is Arraycollection");
                        domaingroups = event.result.domainGroup;
                  } else if (event.result.domainGroup != null) {
                  	    trace("DomainGroups is not array collection but is not null");
                        domaingroups = new ArrayCollection();
                        domaingroups.addItem(event.result.domainGroup);
                  }

                  if (domaingroups != null) {
                      trace("domaingroups was not null");
                      trace("domaingroups length ");
                      trace(domaingroups.length);
                      for (var i:int = 0; i < domaingroups.length; i++) {
                          var domaingroup:DomainGroupVO = new DomainGroupVO(domaingroups[i]);
                          dgs.addItem(domaingroup);
                      }
                  } else {
                      trace("domaingroups was null");
                  }
       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
