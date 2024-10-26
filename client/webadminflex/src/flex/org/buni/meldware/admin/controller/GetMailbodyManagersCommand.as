package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
    import com.adobe.cairngorm.control.CairngormEvent;
    import org.buni.meldware.admin.controller.MailbodyManagerDelegate;
    import org.buni.meldware.admin.model.ModelLocator;
    import org.buni.meldware.admin.model.MailbodyManagerVO;
    
    public class GetMailbodyManagersCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public function execute( event : CairngormEvent ) : void {
            trace("in GetMailbodyManagersCommand execute");
            
            var delegate : MailbodyManagerDelegate = new MailbodyManagerDelegate( this );   
            delegate.getMailbodyManagers( );          
            trace("post delegate.getMailbodyManagers()");
        }
           
        public function onResult( event : * = null ) : void {    
        	trace("GetMailbodyManagers onResult");        
            var model:ModelLocator = ModelLocator.getInstance();
                  model.mailbodyManagers = new ArrayCollection();

                  var mailbodyManagers:ArrayCollection;

                  if (event.result.mailbodyManager is ArrayCollection) {
                  	    trace("mailbodyManagers is Arraycollection");
                        mailbodyManagers = event.result.mailbodyManager;
                  } else if (event.result.mailbodyManager != null) {
                  	    trace("MailbodyManagers is not array collection but is not null");
                        mailbodyManagers = new ArrayCollection();
                        mailbodyManagers.addItem(event.result.mailbodyManager);
                  }

                  if (mailbodyManagers != null) {
                      trace("mailbodyManagers was not null");
                      trace("mailbodyManagers length ");
                      trace(mailbodyManagers.length);
                      for (var i:int = 0; i < mailbodyManagers.length; i++) {
                          var mailbodyManager:MailbodyManagerVO = new MailbodyManagerVO(mailbodyManagers[i]);
                          model.mailbodyManagers.addItem(mailbodyManager);
                      }
                  } else {
                      trace("mailbodyManagers was null");
                  }

       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
