package org.buni.meldware.admin.controller
{
  import mx.controls.Alert;
  
  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.meldware.admin.model.MailListVO;
  import org.buni.meldware.admin.model.ModelLocator;

  public class MailListDelegate
  {
    private var responder:Responder;
    private var service:Object;

    public function MailListDelegate(responder:Responder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.responder = responder;
    }

    public function getMailLists():void
    {
      trace("in get mailLists");
      var params:Object = new Object();
      params.op = "GetMailLists";
      
      trace("sending get mailLists");
      var call:Object = service.send(params);
      trace("sent get mailLists");
      call.resultHandler = responder.onResult;
      call.faultHandler = responder.onFault;
    }
    
    public function getMailListMembers(mailList:MailListVO, pattern:String):void {
        var parms:Object = new Object();
        parms.listAddress = mailList.listAddress;
        parms.pattern = pattern;
        parms.op = "GetMailListMembers";
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }
    
    public function addMailListMember(mailList:MailListVO, address:String):void {
        var parms:Object = new Object();
        parms.listAddress = mailList.listAddress;
        parms.address = address;
        parms.op = "AddMailListMember";
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }
    
    public function deleteMailListMember(mailList:MailListVO, address:String):void {
        var parms:Object = new Object();
        parms.listAddress = mailList.listAddress;
        parms.address = address;
        parms.op = "DeleteMailListMember";
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }
       
    public function addMailList(mailList:MailListVO):void {
        var parms:Object = new Object();
        parms.listAddress = mailList.listAddress;
        parms.attachmentsAllowed = mailList.attachmentsAllowed;
        parms.membersOnly = mailList.membersOnly;
        parms.prefixAutoBracketed = mailList.prefixAutoBracketed;
        parms.replyToList = mailList.replyToList;
        parms.op = "CreateMailList";
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }
           
    public function editMailList(mailList:MailListVO):void {
        var parms:Object = new Object();
        parms.listAddress = mailList.listAddress;
        parms.attachmentsAllowed = mailList.attachmentsAllowed;
        parms.membersOnly = mailList.membersOnly;
        parms.prefixAutoBracketed = mailList.prefixAutoBracketed;
        parms.replyToList = mailList.replyToList;
        parms.op = "EditMailList";
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }  
    
    public function deleteMailList(mailList:MailListVO):void {
        var parms:Object = new Object();
        parms.listAddress = mailList.listAddress;
      
        parms.op = "DeleteMailList";
    	
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
