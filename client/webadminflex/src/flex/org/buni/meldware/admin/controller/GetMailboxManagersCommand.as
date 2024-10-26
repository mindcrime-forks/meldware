package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
    import com.adobe.cairngorm.control.CairngormEvent;
    import org.buni.meldware.admin.controller.MailboxManagerDelegate;
    import org.buni.meldware.admin.model.ModelLocator;
    import org.buni.meldware.admin.model.MailboxManagerVO;
    
    public class GetMailboxManagersCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public function execute( event : CairngormEvent ) : void {
            trace("in GetMailboxManagersCommand execute");
            
            var delegate : MailboxManagerDelegate = new MailboxManagerDelegate( this );   
            delegate.getMailboxManagers( );          
            trace("post delegate.getMailboxManagers()");
        }
           
        public function onResult( event : * = null ) : void {    
        	trace("GetMailboxManagers onResult");        
            var model:ModelLocator = ModelLocator.getInstance();
                  model.mailboxManagers = new ArrayCollection();

                  var mailboxManagers:ArrayCollection;

                  if (event.result.mailboxManager is ArrayCollection) {
                  	    trace("mailboxManagers is Arraycollection");
                        mailboxManagers = event.result.mailboxManager;
                  } else if (event.result.mailboxManager != null) {
                  	    trace("MailboxManagers is not array collection but is not null");
                        mailboxManagers = new ArrayCollection();
                        mailboxManagers.addItem(event.result.mailboxManager);
                  }

                  if (mailboxManagers != null) {
                      trace("mailboxManagers was not null");
                      trace("mailboxManagers length ");
                      trace(mailboxManagers.length);
                      for (var i:int = 0; i < mailboxManagers.length; i++) {
                          var mailboxManager:MailboxManagerVO = new MailboxManagerVO(mailboxManagers[i]);
                          model.mailboxManagers.addItem(mailboxManager);
                      }
                  } else {
                      trace("mailboxManagers was null");
                  }

       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
