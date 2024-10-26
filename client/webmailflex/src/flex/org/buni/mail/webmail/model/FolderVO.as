package org.buni.mail.webmail.model
{
  import flash.events.Event;
  import flash.events.IEventDispatcher;
  
	import mx.collections.ArrayCollection;
  import mx.events.CollectionEvent;
  import mx.core.IUID;
  
	import com.adobe.cairngorm.vo.ValueObject;

  [Bindable]
	public class FolderVO implements ValueObject, IUID
	{
	  public var id:Number;
		public var name:String;
		public var path:String;
		public var numTotalEmails:Number;
		public var numUnreadEmails:Number;
		[ArrayElementType("org.buni.mail.webmail.model.EmailVO")]		
		public var emails:ArrayCollection;
		//public var selectedEmails:ArrayCollection;
		public var children:ArrayCollection;
	  public var acceptDrop:Boolean;
	  public var acceptDrag:Boolean;
		
    private  var _selectedEmailIds:ArrayCollection;
    
    
		public function FolderVO(o:Object=null)
		{
			if (o != null)
			{
			  this.id = new Number(o.id);
				this.name = o.name;
				this.path = o.path;
				this.numTotalEmails = o.numTotalEmails;
				this.numUnreadEmails = o.numUnreadEmails;
				
				if ((path == "INBOX") || (path == "Sent") || (path == "Trash") || (path == "Drafts"))
				{
				  acceptDrag = false;
				}
				else
				{
				  acceptDrag = true;
				}
				
				acceptDrop = true;
			}
			emails = new ArrayCollection();
			//emails.disableAutoUpdate();
			
			//selectedEmails = new ArrayCollection();
			//selectedEmails.disableAutoUpdate();
			
			selectedEmailIds = new ArrayCollection();
			//selectedEmailIds.disableAutoUpdate();
		}
		
	  public function get uid():String
		{
		  return id.toString();
		}
		
		public function set uid(s:String):void
		{
		  id = new Number(s);
		}
		
    [Bindable(event="selectedEmailIdsChanged")]
    public function get selectedEmailIds():ArrayCollection
    {
      return _selectedEmailIds;
    }
    
    public function set selectedEmailIds(_selectedEmailIds:ArrayCollection):void
    {
      this._selectedEmailIds = _selectedEmailIds;
      //this._selectedEmailIds.addEventListener(CollectionEvent.COLLECTION_CHANGE, updateSelectedEmails);
      //updateSelectedEmails();
      dispatchEvent(new Event("selectedEmailIdsChanged"));
    }
    
    /*
    private function updateSelectedEmails(event:Event=null):void
    {
      selectedEmails.removeAll();
      for (var i:int = 0; i < this._selectedEmailIds.length; i++)
      {
        for (var j:int = 0; j < this.emails.length; j++)
        {
          if (this.emails[j].id == this._selectedEmailIds[i])
          {
            selectedEmails.addItem(this.emails[j]);
            break;
          }
        }
      }
    }

  		
		private function isSelectedEmail(email:Object):Boolean
		{
		  for (var i:int = 0; i < selectedEmailIds.length; i++)
		  {
		    if ((email as EmailVO).id == selectedEmailIds[i])
		    {
		      return true;
		    }
		  }
		  return false;
		}
		*/
		
		public function getEmailById(id:String):EmailVO
		{
		  for (var j:int = 0; j < this.emails.length; j++)
      {
        if (this.emails[j].id == id)
        {
          return this.emails[j];
        }
      }
      return null;
		}
		
		public function toString():String
		{
		    var pattern:RegExp = /\./gi;
		    return "/"+path.replace(pattern, "/");
		//  return "name = " + name + "; path = " + path;
		}
		  
 }
}