package org.buni.mail.webmail.model {
  import com.adobe.cairngorm.control.CairngormEvent;
  import com.adobe.cairngorm.control.CairngormEventDispatcher;
  import com.adobe.cairngorm.model.ModelLocator;
  
  import flash.events.Event;
  
  import mx.collections.ArrayCollection;
  import mx.collections.ListCollectionView;
  import mx.events.CollectionEvent;
  import mx.events.CollectionEventKind;
  import mx.utils.ObjectUtil;

  import qs.utils.DateRange;
  
  import org.buni.mail.webmail.controller.WebmailController; 
  import org.buni.mail.webmail.model.calendar.Calendar;
  import org.buni.mail.webmail.model.calendar.CalendarSet;
  import org.buni.mail.webmail.model.UserActionSetVO;
  import org.buni.mail.webmail.model.Status;
  	
  [Event(name="foldersChanged")]
  [Event(name="selectedFolderChanged")]
  [Event(name="emailsChanged")]
  [Event(name="selectedEmailsChanged")]
  
  public class ModelLocator implements com.adobe.cairngorm.model.ModelLocator { 
  	[Bindable]
  	public var selectedABEntry:AddressBookEntryVO;
  	[Bindable]
  	public var searched:ArrayCollection = new ArrayCollection();    
  	[Bindable]
  	public var suggestions:ArrayCollection = new ArrayCollection();       
    [Bindable]
    public var availableABMounts:ArrayCollection;
    [Bindable]
    public var userActionSets:ArrayCollection;
    [Bindable]
    public var selectedActionSet:UserActionSetVO;
    [Bindable]
    public var userABMounts:ArrayCollection;
    [Bindable]
    public var systemABMounts:ArrayCollection;
    private var _user:UserVO;
    private var _selectedDay:DayVO;
    private var _selectedFolder:FolderVO;
    private var _folders:ArrayCollection;
    private var _status:Status = Status.getInstance();

    private static var modelLocator:org.buni.mail.webmail.model.ModelLocator;
    
    public static function getInstance():org.buni.mail.webmail.model.ModelLocator {
      if ( modelLocator == null )
        modelLocator = new org.buni.mail.webmail.model.ModelLocator();
        
      return modelLocator;
    }
     
    public function ModelLocator() {
      if ( org.buni.mail.webmail.model.ModelLocator.modelLocator != null )
        throw new Error( "Only one ModelLocator instance should be instantiated" );  

      _folders = new ArrayCollection();
      tabs = new ArrayCollection();
      calSet = new CalendarSet(new DateRange());
    }

    [Bindable(event="userChanged")]
    public function get user():UserVO {
      return _user;
    }

    public function set user(_user:UserVO):void {
      this._user = _user;
	  this.resetCalendarSet();
      dispatchEvent(new Event("userChanged"));
    }

    [Bindable(event="dayChanged")]
    public function get selectedDay():DayVO {
      return _selectedDay;
    }
    
    [Bindable]
    public function get status():Status {
    	return _status;
    }

    private function resetCalendarSet():void {
      if(this.user != null) {
	   	  // This should be something configureable
	      // set it 2 months back
	      var newCalset:CalendarSet = new CalendarSet(new DateRange())
	      var today:Date = new Date();
	      today.hours = 0;
	      today.minutes = 0;
	      today.seconds = 0;
	      today.milliseconds = 0;
	      today.month-=2;
	      newCalset.range.start = new Date(today);
	      // and a year ahead
	      today.month+=14;
	      newCalset.range.end = today;
	      this.calSet = newCalset;
	      // load the first calendar
	      var calendar:Calendar  = new Calendar();
		  calendar.contextColor = 0xBB0000;
		  calendar.name = "My Calendar";
		  var e:CairngormEvent = new CairngormEvent(WebmailController.ADD_CALENDAR_TO_SET);
		  e.data = calendar;
		  CairngormEventDispatcher.getInstance().dispatchEvent(e);
      } else {
      	this.calSet = null;
      }
    }
    
    [Bindable] 
    public var calSet:CalendarSet = null;

    public function set selectedDay(_selectedDay:DayVO):void {
      this._selectedDay = _selectedDay;
      dispatchEvent(new Event("dayChanged"));
    }
    
    
    public function get selectedFolder():FolderVO {
      return _selectedFolder;
    }

    public function set selectedFolder(_selectedFolder:FolderVO):void {
      this._selectedFolder = _selectedFolder;
      //this._selectedFolder.emails.addEventListener(CollectionEvent.COLLECTION_CHANGE, emailsChanged);
      //this._selectedFolder.selectedEmails.addEventListener(CollectionEvent.COLLECTION_CHANGE, selectedEmailsChanged);
      dispatchEvent(new Event("selectedFolderChanged"));
    }
    
    /*
    private function emailsChanged(event:CollectionEvent):void
    {
      if (event.kind == CollectionEventKind.ADD || 
      	event.kind == CollectionEventKind.REMOVE) {
        dispatchEvent(new Event("emailsChanged"));
      }
    }
    
    private function selectedEmailsChanged(event:Event):void
    {
      dispatchEvent(new Event("selectedEmailsChanged"));
    }
    */
    
    [Bindable(event="foldersChanged")]
    public function get folders():ArrayCollection {
      return _folders;
    }
    
    public function set folders(_folders:ArrayCollection):void {
      this._folders = _folders;
      dispatchEvent(new Event("foldersChanged"));
    }

    [Bindable]
    public var tabs:ArrayCollection;
    
    [Bindable]
    public var contacts:ArrayCollection;
    
    [Bindable]
    public var sharedContacts:ArrayCollection;
    
    public function setEmailBody(email:Object):void {
      var te:EmailVO = new EmailVO(email);
      var e:EmailVO = getEmail(te);
      e.body = email.body;
      trace(ObjectUtil.toString(e));
    }

    public function setEmailAttachment(email:Object):void {
      var te:EmailVO = new EmailVO(email);
      var e:EmailVO = getEmail(te);
      if (e!= null && e.attachments != null) {
         e.attachments.removeAll();
      } else if (e == null) {
      	 return;
      }
      if (email.attachments != null) {
        var files:ArrayCollection;
        if (email.attachments.file is ArrayCollection) {
          files = email.attachments.file;
        } else {
          files = new ArrayCollection();
          files.addItem(email.attachments.file); 
        }
        for (var i:int = 0; i < files.length; i++) {
          var o:Object = new Object();
          o.filename = files[i].filename;
          o.url = files[i].url;
          o.id = files[i].id;
          trace("BEGIN model locator e.attachments.addItem in setEmailAttachemnt");
          if (e.attachments == null) {
              e.attachments = new ArrayCollection();
          }
          e.attachments.addItem(new AttachmentVO(o));
          
          trace("END model locator e.attachments.addItem in setEmailAttachemnt");
        }
      }
    }
   
    public function getFolderByPath(path:String,folderList:ArrayCollection=null):FolderVO {
      if (folderList == null) {
        folderList = folders;
      }
      
      for (var i:int = 0; i < folderList.length; i++) {
        if (folderList[i].path == path) {
          return folderList[i];
        }
        
        if (folderList[i].children != null) {
          var f:FolderVO = getFolderByPath(path,folderList[i].children);
          if (f != null) {
            return f;
          }
        }
      }
      return null;
    }

    public function getFolderById(folderId:Number):FolderVO {
      for (var i:int = 0; i < folders.length; i++)
      {
        if ((folders[i] as FolderVO).id == folderId) {
          return folders[i];
        } 
      }
      return null;
    }
    
    public function getFolderByIdFromCollection(folderId:Number, folders:ArrayCollection):FolderVO {
      
      for (var i:int = 0; i < folders.length; i++)
      {
        var folder:FolderVO = (folders[i] as FolderVO);
        if ((folder).id == folderId) {
          return folder;
        } else if (folder.children != null && folder.children.length > 0) {
          folder = getFolderByIdFromCollection(folderId, folder.children);
          if (folder != null) {
              return folder;
          }
        }
      }
      return null;
      
    }

    public function getEmail(email:EmailVO):EmailVO {
      var f:FolderVO = getFolderByIdFromCollection(email.folderId, folders);
      return f.getEmailById(email.id);
    }

    public function removeTab(o:Object):void {
      tabs.removeItemAt(tabs.getItemIndex(o));
    }
    
    public function removeTabById(id:Object):void {
      removeTab(getTabById(id));
    }
    
    public function getTabById(tabid:Object):Object {
      for (var i:int = 0; i < tabs.length; i++) {
        if (tabs[i].tabid == tabid) {
          return tabs[i];
        }
      }
      return null;
    }

  }
  
}
