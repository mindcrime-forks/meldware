package org.buni.meldware.admin.controller
{
  import mx.controls.Alert;
  
  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.meldware.admin.model.DomainGroupVO;
  import org.buni.meldware.admin.model.ModelLocator;

  public class DomainGroupDelegate
  {
    private var responder:Responder;
    private var service:Object;

    public function DomainGroupDelegate(responder:Responder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.responder = responder;
    }

    public function getDomainGroups(local:Boolean):void
    {
      trace("in get domainGroups");
      var params:Object = new Object();
      params.op = local ? "GetLocalDomainGroups" : "GetDomainGroups";
      
      trace("sending get domainGroups");
      var call:Object = service.send(params);
      trace("sent get domainGroups");
      call.resultHandler = responder.onResult;
      call.faultHandler = responder.onFault;
    }
  }
}
