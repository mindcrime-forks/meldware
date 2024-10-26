package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
    import com.adobe.cairngorm.control.CairngormEvent;
    import org.buni.meldware.admin.controller.ServiceDelegate;
//    import org.buni.meldware.admin.controller.ServiceEvent;
    import org.buni.meldware.admin.model.ModelLocator;
    import org.buni.meldware.admin.model.UserVO;
    
    public class GetUsersCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public function execute( event : CairngormEvent ) : void {
            trace("in GetUsersCommand execute");
            
            var delegate : UserDelegate = new UserDelegate( this );   
            delegate.getUsers( event.data );          
        }
           
        public function onResult( event : * = null ) : void {            
            var model:ModelLocator = ModelLocator.getInstance();
                  model.users = new ArrayCollection();

                  var users:ArrayCollection;

                  if (event.result.user is ArrayCollection) {
                        users = event.result.user;
                  } else if (event.result.user != null) {
                        users = new ArrayCollection();
                        users.addItem(event.result.user);
                  }

                  if (users != null) {
                      trace("users was not null");
                      trace("users length ");
                      trace(users.length);
                      for (var i:int = 0; i < users.length; i++) {
                          var user:UserVO = new UserVO(users[i]);
                          model.users.addItem(user);
                      }
                  } else {
                      trace("users was null");
                  }
       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
