package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
    import com.adobe.cairngorm.control.CairngormEvent;
    import org.buni.meldware.admin.controller.MailListenerChainDelegate;
    import org.buni.meldware.admin.model.ModelLocator;
    import org.buni.meldware.admin.model.MailListenerChainVO;
    
    public class GetMailListenerChainsCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public function execute( event : CairngormEvent ) : void {
            trace("in GetMailListenerChainsCommand execute");
            
            var delegate : MailListenerChainDelegate = new MailListenerChainDelegate( this );   
            delegate.getMailListenerChains( );          
            trace("post delegate.getMailListenerChains()");
        }
           
        public function onResult( event : * = null ) : void {    
        	trace("GetMailListenerChains onResult");        
            var model:ModelLocator = ModelLocator.getInstance();
                  model.mailListenerChains = new ArrayCollection();

                  var mailListenerChains:ArrayCollection;

                  if (event.result.mailListenerChain is ArrayCollection) {
                  	    trace("mailListenerChains is Arraycollection");
                        mailListenerChains = event.result.mailListenerChain;
                  } else if (event.result.mailListenerChain != null) {
                  	    trace("MailListenerChains is not array collection but is not null");
                        mailListenerChains = new ArrayCollection();
                        mailListenerChains.addItem(event.result.mailListenerChain);
                  }

                  if (mailListenerChains != null) {
                      trace("mailListenerChains was not null");
                      trace("mailListenerChains length ");
                      trace(mailListenerChains.length);
                      for (var i:int = 0; i < mailListenerChains.length; i++) {
                          var mailListenerChain:MailListenerChainVO = new MailListenerChainVO(mailListenerChains[i]);
                          model.mailListenerChains.addItem(mailListenerChain);
                      }
                  } else {
                      trace("mailListenerChains was null");
                  }

       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
