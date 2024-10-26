package org.buni.mail.webmail.controller
{	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.controls.Alert;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.buni.mail.webmail.model.UserVO;
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.controller.EmailDelegate;
	import mx.collections.ArrayCollection;
	import org.buni.mail.webmail.model.FolderVO;
	import org.buni.mail.webmail.util.FaultUtil;
	
	/**
	 * A command that gets all folders for the currently logged in user
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 * 
	 */
	public class GetFoldersCommand implements ICommand, IResponder
	{

	   /**
	    * Executes the command
	    * @param event
	    * 
	    */
	   public function execute(event:CairngormEvent):void
	   {
	      var delegate:EmailDelegate = new EmailDelegate(this);         
	      delegate.getFolders();
	   }

	   /**
	    * Gets called on a success response from executing the command.
	    * <p>Parses the results and updates the model</p>
	    * @param event event.result.folder is expected to be a list of FolderVO
	    * @see org.buni.mail.webmail.model.FolderVO
	    * 
	    */
	   public function result(event:Object):void
	   {
	      var model:ModelLocator = ModelLocator.getInstance();
	      var folders:ArrayCollection = new ArrayCollection();
	      
	      for (var i:int = 0; i < event.result.folder.length; i++)
	      {
	        var folder:FolderVO = new FolderVO(event.result.folder[i]);
	        if (folder.path.indexOf(".") == -1)
	        {
	          findSubFolders(folder,event.result.folder);
	          folders.addItem(folder);
	        }
	      }
	      model.folders = folders;
	   }

	   public function fault(event:Object):void
	   {
	   		FaultUtil.checkFault(event as FaultEvent);
	   }
	   
	   private function findSubFolders(folder:FolderVO,folders:ArrayCollection):void
	   {
	     for (var i:int = 0; i < folders.length; i++)
	     {
	       var f:FolderVO = new FolderVO(folders[i]);
	       var p:String = f.path.substr(0,folder.path.length);
	       var fName:String = f.path.substr(folder.path.length + 1);
	       if ((folder.path == p) && (fName != "") && (fName.indexOf(".") == -1))
	       {
           findSubFolders(f,folders);
           if (folder.children == null)
           {
             folder.children = new ArrayCollection();
           }
           folder.children.addItem(f);
	       }
	     }
	   }
	}
	
}
