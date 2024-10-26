package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
    import com.adobe.cairngorm.control.CairngormEvent;
    import org.buni.meldware.admin.controller.MailListDelegate;
    import org.buni.meldware.admin.model.ModelLocator;
    import org.buni.meldware.admin.model.MailListVO;
    
    public class GetMailListsCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public function execute( event : CairngormEvent ) : void {
            trace("in GetMailListsCommand execute");
            
            var delegate : MailListDelegate = new MailListDelegate( this );   
            delegate.getMailLists( );          
            trace("post delegate.getMailLists()");
        }
           
        public function onResult( event : * = null ) : void {    
        	trace("GetMailLists onResult");        
            var model:ModelLocator = ModelLocator.getInstance();
                  model.mailLists = new ArrayCollection();

                  var mailLists:ArrayCollection;

                  if (event.result.mailList is ArrayCollection) {
                  	    trace("mailLists is Arraycollection");
                        mailLists = event.result.mailList;
                  } else if (event.result.mailList != null) {
                  	    trace("MailLists is not array collection but is not null");
                        mailLists = new ArrayCollection();
                        mailLists.addItem(event.result.mailList);
                  }

                  if (mailLists != null) {
                      trace("mailLists was not null");
                      trace("mailLists length ");
                      trace(mailLists.length);
                      for (var i:int = 0; i < mailLists.length; i++) {
                          var mailList:MailListVO = new MailListVO(mailLists[i]);
                          model.mailLists.addItem(mailList);
                      }
                  } else {
                      trace("mailLists was null");
                  }

       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
