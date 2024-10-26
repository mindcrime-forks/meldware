package org.buni.mail.webmail.controller
{  
  import mx.collections.ArrayCollection;
  import mx.controls.Alert;
  import mx.rpc.IResponder;
  import mx.rpc.events.FaultEvent;
  import mx.rpc.events.ResultEvent;
  
  import com.adobe.cairngorm.commands.ICommand;
  import com.adobe.cairngorm.control.CairngormEvent;
  
  import org.buni.mail.webmail.model.ModelLocator;
  import org.buni.mail.webmail.model.ContactVO;
  import org.buni.mail.webmail.util.FaultUtil;
  
  /**
   * A command that gets all contacts for the currently logged in user
   * @author Scotty Scott
   * @author Aron Sogor
   * @author James Ward
   * @author Mike Barker 
   * 
   */
  public class GetContactsCommand implements ICommand, IResponder
  {
  
    /**
     * Executes the command
     * @param event
     * 
     */
    public function execute(event:CairngormEvent):void
    {
      var delegate:ContactDelegate = new ContactDelegate(this);
      delegate.getContacts();
    }

    /**
     * Gets called on a success response from executing the command.
     * <p>Refreshes the contacts ArrayCollection on the model with the results</p>
     * @param event event.result.contact is expected to be an ArrayCollection of ContactVO or a single ContactVO
     * 
    */
  public function result(event:Object):void
  {
    var model:ModelLocator = ModelLocator.getInstance();
    model.contacts = new ArrayCollection();
      
    var contacts:ArrayCollection;
      
    if (!(event.result is String))
    {
      if (event.result.contact is ArrayCollection)
      {
        contacts = event.result.contact;
      }
      else if (event.result.contact != null)
      {
        contacts = new ArrayCollection();
        contacts.addItem(event.result.contact);
      }
    }
      
    if (contacts != null)
    {
      for (var i:int = 0; i < contacts.length; i++)
      {
        var contact:ContactVO = new ContactVO(contacts[i]); 
        model.contacts.addItem(contact);
        if(contact.isOwnContact)
        {
         // model.user.contact=contact;
        }
      }
    }
  }

  public function fault(event:Object):void
  {
       FaultUtil.checkFault(event as FaultEvent);
  }
}
  
}
