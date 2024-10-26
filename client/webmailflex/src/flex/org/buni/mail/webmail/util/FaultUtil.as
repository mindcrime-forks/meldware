package org.buni.mail.webmail.util
{
  import mx.rpc.events.FaultEvent;
  import mx.controls.Alert;
  import com.adobe.cairngorm.control.CairngormEvent;
  import com.adobe.cairngorm.control.CairngormEventDispatcher;
  import org.buni.mail.webmail.controller.WebmailController;
  
  public class FaultUtil
  {
    /**
     * Assumes that any fault with Server.Error.Request means that the user is logged out.
     * <p>Fires WebmailController.GET_USER to reset everything</p>
     * @param event
     * 
     */
    static public function checkFault(event:FaultEvent):void
    {
      if (event.fault.faultCode == "Server.Error.Request")
      {
        var e:CairngormEvent = new CairngormEvent(WebmailController.GET_USER);
  	    CairngormEventDispatcher.getInstance().dispatchEvent(e);
      }
      else
      {
        Alert.show(event.fault.toString());
      }
    }
  }
}