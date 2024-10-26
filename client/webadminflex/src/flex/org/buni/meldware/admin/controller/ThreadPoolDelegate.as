package org.buni.meldware.admin.controller
{
  import mx.controls.Alert;
  
  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.meldware.admin.model.ThreadPoolVO;
  import org.buni.meldware.admin.model.ModelLocator;

  public class ThreadPoolDelegate
  {
    private var responder:Responder;
    private var service:Object;

    public function ThreadPoolDelegate(responder:Responder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.responder = responder;
    }

    public function getThreadPools():void
    {
      trace("in get threadpools");
      var params:Object = new Object();
      params.op = "GetThreadPools";
      
      trace("sending get threadpools");
      var call:Object = service.send(params);
      trace("sent get threadpools");
      call.resultHandler = responder.onResult;
      call.faultHandler = responder.onFault;
    }
    
    public function deleteThreadPool(threadpool:ThreadPoolVO):void {
        var parms:Object = new Object();
        parms.name = threadpool.threadPoolName;
        parms.op = "DeleteThreadPool";  	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }   
 
     public function addThreadPool(threadpool:ThreadPoolVO):void {
        var parms:Object = new Object();
        
        parms.op = "AddThreadPool";
        parms.name = threadpool.threadPoolName;
        parms.initial = threadpool.initial;
        parms.max = threadpool.max;
        parms.min = threadpool.min;
        parms.idleKeepAlive = threadpool.idleKeepAlive;
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }   

    public function editThreadPool(threadpool:ThreadPoolVO):void {
        var parms:Object = new Object();
     
        parms.op = "EditThreadPool";
        parms.name = threadpool.threadPoolName;
        parms.initial = threadpool.initial;
        parms.max = threadpool.max;
        parms.min = threadpool.min;
        parms.idleKeepAlive = threadpool.idleKeepAlive;
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }    
    
    public function resetModel():void
    {
      var model:ModelLocator = ModelLocator.getInstance();

      //model.setServices(null);
    }
  }
}
