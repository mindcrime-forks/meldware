package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.adobe.cairngorm.commands.SequenceCommand;
//    import org.buni.meldware.admin.controller.ThreadPoolEvent;
    import org.buni.meldware.admin.controller.ThreadPoolDelegate;
    import org.buni.meldware.admin.model.ModelLocator;
    import org.buni.meldware.admin.model.ThreadPoolVO;
    
    public class GetThreadPoolsCommand extends SequenceCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        override public function execute( event : CairngormEvent ) : void {
            //model.threadpools.isPending = true;
            trace("in GetThreadPoolsCommand execute");
            
            var delegate : ThreadPoolDelegate = new ThreadPoolDelegate( this );   
            //var threadpoolsEvent : ThreadPoolEvent = ThreadPoolsEvent( event );  
            delegate.getThreadPools( );          
            trace("post delegate.getThreadPoolS()");
        }
           
        public function onResult( event : * = null ) : void {    
        	trace("GetThreadPools onResult");        
            var model:ModelLocator = ModelLocator.getInstance();
                  model.threadpools = new ArrayCollection();

                  var threadpools:ArrayCollection;

                  if (event.result.threadPool is ArrayCollection) {
                  	    trace("threadpools is Arraycollection");
                        threadpools = event.result.threadPool;
                  } else if (event.result.threadpool != null) {
                  	    trace("ThreadPools is not array collection but is not null");
                        threadpools = new ArrayCollection();
                        threadpools.addItem(event.result.threadPool);
                  }

                  if (threadpools != null) {
                      trace("threadpools was not null");
                      trace("threadpools length ");
                      trace(threadpools.length);
                      for (var i:int = 0; i < threadpools.length; i++) {
                          var threadpool:ThreadPoolVO = new ThreadPoolVO(threadpools[i]);
                          model.threadpools.addItem(threadpool);
                      }
                    var e:CairngormEvent = new CairngormEvent(AdminController.GOT_THREADPOOLS);
          		    CairngormEventDispatcher.getInstance().dispatchEvent(e); 
         	        executeNextCommand();
                  } else {
                      trace("threadpools was null");
                  }

//            model.threadpool.threadpoolVO = ThreadPoolVO( event.result.hello );
 //           model.threadpool.isPending = false;

            //model.workflowState = ModelLocator.VIEWING_THREADPOOLS;
       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
            //model.threadpool.statusMessage = "The server did not like your tone or some error occurred.";
            //model.threadpool.isPending = false;
        }
    }
}
