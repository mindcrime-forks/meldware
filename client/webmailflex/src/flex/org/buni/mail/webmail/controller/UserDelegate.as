package org.buni.mail.webmail.controller
{
  import mx.controls.Alert;
  import flash.events.Event;
  import mx.rpc.AsyncToken;
  import mx.rpc.IResponder;
  import com.adobe.cairngorm.business.ServiceLocator;
  
  import org.buni.mail.webmail.model.UserVO;
  import org.buni.mail.webmail.model.FolderVO;
  import org.buni.mail.webmail.model.UserPreferencesVO;
  import org.buni.mail.webmail.model.ModelLocator;
  import mx.rpc.http.HTTPService;
 

  /**
   * Delegate responsible for handling all server interactions related to a User.
   * @author Scotty Scott
   * @author Aron Sogor
   * @author James Ward
   * @author Mike Barker
   * 
   */
  public class UserDelegate
  {
  	
    /**
     * Constructor
     * @param responder All operations on this delegate will call the IResponder for processing results
     * @return 
     * 
     */
    public function UserDelegate(responder:IResponder)
    {
      this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
      this.responder = responder;
    }

    /**
     * Gets the currently logged in user
     * 
     */
    public function getUser():void
    {
      var params:Object = new Object();
      params.op = "getUser";
      
      var call:AsyncToken = service.send(params);
      call.addResponder(responder);
    }
    
    /**
     * Saves preferences for the currently logged in user
     * @param userPref
     * 
     */
    public function savePreferences(userPref:UserPreferencesVO):void
    {
      var params:Object = new Object();
      params.op = "savePreferences";
      params.timeZone = userPref.timeZone.name;
      
      var call:AsyncToken = service.send(params);
      call.addResponder(responder);
    }
    
    /**
     * Logs out the user
     * 
     */
    public function logoutUser():void
    {
      var params:Object = new Object();
      params.op = "logoutUser";
      
      var call:AsyncToken = service.send(params);
      call.addResponder(responder);
    }
    
    /**
     * Resets the model
     * <p>removes all tabs, removes all folders, removes all contacts</p>
     * <p>Fires "emailsChanged", "selectedEmailsChanged" on the model</p>
     * 
     */
    public function resetModel():void
    {
		// TODO is this still used? I have seen similar functions in other parts of code    	
      var model:ModelLocator = ModelLocator.getInstance();
    
      model.user = null;
      
      model.selectedFolder = new FolderVO();
      model.dispatchEvent(new Event("emailsChanged"));
      model.dispatchEvent(new Event("selectedEmailsChanged"));
      
      if (model.selectedDay != null)
      {
        model.selectedDay = null;
      }
      
      if (model.tabs != null)
      {
		    model.tabs.removeAll();
      }
      
      if (model.folders != null)
      {
		    model.folders.removeAll();
      }
      
      if (model.contacts != null)
      {
        model.contacts.removeAll();
      }
    }

    private var responder:IResponder;
    private var service:HTTPService;
  }
  
}
