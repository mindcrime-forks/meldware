package org.buni.meldware.admin.controller {
	
  import mx.controls.Alert;
  
  import com.adobe.cairngorm.business.Responder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.meldware.admin.model.UserVO;
  import org.buni.meldware.admin.model.ModelLocator;
  import mx.rpc.http.HTTPService;
  import org.buni.meldware.admin.model.AddressBookEntryVO;

  public class UserDelegate {

    private var responder:Responder;
    private var service:Object;
    private var xmlservice:HTTPService;
      	
    public function UserDelegate(responder:Responder) {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.xmlservice = ServiceLocator.getInstance().getHTTPService("XMLRPCService");
      this.responder = responder;
    }

    public function getUser():void {
      var params:Object = new Object();
      params.op = "GetUser";
      
      var call:Object = service.send(params);

      call.resultHandler = responder.onResult;
      call.faultHandler = responder.onFault;
    }
    
    public function getUsers(pattern:String):void {
    	var parms:Object = new Object();
    	parms.pattern = pattern;
    	parms.op = "GetUsers";
    	
    	var call:Object = service.send(parms);
    	
      call.resultHandler = responder.onResult;
      call.faultHandler = responder.onFault;    	
    }
    
    public function addUserLdap(data:AddressBookEntryVO, addressBookName:String):void {
        var parms:Object = new Object();
        parms.operation = new Object();
        parms.operation.op="addABEntry";
        parms.operation.addressBookEntry = data;
        parms.operation.mountName = addressBookName;
    	
        var call:Object = xmlservice.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }    

    
    public function addUser(user:UserVO):void {
        var parms:Object = new Object();
        parms.username = user.username;
        parms.password = user.password;
        parms.defaultAlias = user.defaultAlias;
        parms.roles = user.roles;
        parms.aliases = user.aliases;
        parms.op = "AddUser";
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }    

    public function editUser(user:UserVO):void {
        var parms:Object = new Object();
        parms.username = user.username;
        parms.password = user.password;
        parms.defaultAlias = user.defaultAlias;
        parms.roles = user.roles;
        parms.aliases = user.aliases;
        parms.op = "EditUser";
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }    

    public function deleteUser(user:UserVO):void {
        var parms:Object = new Object();
        parms.username = user.username;
        parms.op = "DeleteUser";
    	
        var call:Object = service.send(parms);
    	
        call.resultHandler = responder.onResult;
        call.faultHandler = responder.onFault;    	
    }   
    
    public function logoutUser():void {
      var params:Object = new Object();
      params.op = "logoutUser";
      
      var call:Object = service.send(params);
      call.resultHandler = responder.onResult;
      call.faultHandler = responder.onFault;
    }
    
    public function resetModel():void {
      var model:ModelLocator = ModelLocator.getInstance();
      
      model.user = null;
    }
    
  }
}
