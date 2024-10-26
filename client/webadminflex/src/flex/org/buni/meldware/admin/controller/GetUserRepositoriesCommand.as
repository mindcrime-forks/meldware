package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
    import com.adobe.cairngorm.control.CairngormEvent;
    import org.buni.meldware.admin.controller.UserRepositoryDelegate;
    import org.buni.meldware.admin.model.ModelLocator;
    import org.buni.meldware.admin.model.UserRepositoryVO;
    
    public class GetUserRepositoriesCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public function execute( event : CairngormEvent ) : void {
            trace("in GetUserRepositoriesCommand execute");
            
            var delegate : UserRepositoryDelegate = new UserRepositoryDelegate( this );   
            delegate.getUserRepositories( );          
            trace("post delegate.getUserRepositories()");
        }
           
        public function onResult( event : * = null ) : void {    
        	trace("GetUserRepositories onResult");        
            var model:ModelLocator = ModelLocator.getInstance();
                  model.userRepositories = new ArrayCollection();

                  var userRepositories:ArrayCollection;

                  if (event.result.userRepository is ArrayCollection) {
                  	    trace("userRepositories is Arraycollection");
                        userRepositories = event.result.userRepository;
                  } else if (event.result.userRepository != null) {
                  	    trace("UserRepositories is not array collection but is not null");
                        userRepositories = new ArrayCollection();
                        userRepositories.addItem(event.result.userRepository);
                  }

                  if (userRepositories != null) {
                      trace("userRepositories was not null");
                      trace("userRepositories length ");
                      trace(userRepositories.length);
                      for (var i:int = 0; i < userRepositories.length; i++) {
                          var userRepository:UserRepositoryVO = new UserRepositoryVO(userRepositories[i]);
                          model.userRepositories.addItem(userRepository);
                      }
                  } else {
                      trace("userRepositories was null");
                  }

       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
