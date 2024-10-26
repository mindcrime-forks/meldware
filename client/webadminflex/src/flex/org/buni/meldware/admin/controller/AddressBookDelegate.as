package org.buni.meldware.admin.controller
{
	
	
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import mx.collections.ArrayCollection;
  import mx.rpc.AsyncToken;
  import mx.rpc.IResponder;
  import mx.rpc.http.HTTPService;
  
  import org.buni.meldware.admin.model.ModelLocator;
  //import org.buni.meldware.admin.model.UserABMountVO;
  import org.buni.meldware.admin.model.ModelLocator;

  public class AddressBookDelegate
  {
    private var responder:IResponder;
    private var service:Object;

    private var xmlservice:HTTPService;

    public function AddressBookDelegate(responder:IResponder)
    {
      
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");

      this.xmlservice = ServiceLocator.getInstance().getHTTPService("XMLRPCService");
      this.responder = responder;
    }
    
    public function searchAddressBook(name:String, email:String):void
    {
      trace("in search address book");

        var params:Object = new Object();
        params.operation = new Object();
        params.operation.op = "searchAddressBook";
        params.operation.name = name;
        params.operation.email = email;
        trace("sending search addressbook");
        var call:AsyncToken = xmlservice.send(params);
        call.addResponder(responder);
        trace("sent search addressbook");
    }
    
    public function getSuggestions(typed:String):void
    {
      trace("in get suggestion");
      if (typed != null)
      {
        var params:Object = new Object();
        params.op = "getSuggestions";
        params.typed = typed;
        trace("sending get suggestions");
        var call:AsyncToken = service.send(params);
        call.addResponder(responder);
        trace("sent get suggestions");
      }
    }

    public function getUserABMounts():void
    {
      trace("in get userAddressBooks");
      var model:ModelLocator = ModelLocator.getInstance();
      if (model.user != null)
      {
        var params:Object = new Object();
        params.op = "getUserABMounts";
        params.user = model.user.username;
        trace("sending get getUserAddressBooks");
        var call:AsyncToken = service.send(params);
        call.addResponder(responder);
        trace("sent get userAddressBooks");
      }
    }
    
    /*
    public function saveUserABMounts(uam:ArrayCollection):void 
    {
     trace("in get saveUserAddressBooks");
      var model:ModelLocator = ModelLocator.getInstance();
      if (model.user != null)
      {
        var params:Object = new Object();
        params.op = "saveUserABMounts";
        params.user = model.user.username;
        params.order = new ArrayCollection();
        params.id = new ArrayCollection();
        params.name = new ArrayCollection();
        params.description = new ArrayCollection();
        params.enabled = new ArrayCollection();
        params.local = new ArrayCollection();
        
        for(var i:int = 0; i < model.userABMounts.length; i++) {
        	var mount:UserABMountVO = model.userABMounts.getItemAt(i) as UserABMountVO;
        	params.order.addItem(new Number(i));
        	params.id.addItem(new String(mount.uid));
        	params.name.addItem(mount.name);
        	params.description.addItem(mount.description);
        	params.enabled.addItem(new String(mount.enabled));
        	params.local.addItem(new String(mount.local));
        }
        
        trace("sending save userAddressBooks");
        var call:AsyncToken = service.send(params);
        call.addResponder(responder);
        trace("sent save userAddressbooks");      
      }
    }
    */
    
    public function getSystemABMounts():void
    {
      trace("in get systemAddressBooks");
      var model:ModelLocator = ModelLocator.getInstance();
      if (model.user != null)
      {
        var params:Object = new Object();
        params.operation = new Object();
        params.operation.op = "getSystemABMounts";
        trace("sending get getSystemAddressBooks");
        var call:AsyncToken = xmlservice.send(params);
        call.addResponder(responder);
        trace("sent get SystemABMouts");
      }
    }
        
    public function resetModel():void
    {
      var model:ModelLocator = ModelLocator.getInstance();
    }
  }
}
