package org.buni.mail.webmail.controller
{
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.commands.SequenceCommand;
	
    import org.buni.mail.webmail.model.FolderVO;
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.util.FaultUtil;
	
	/**
	 * A command for moving a folder
	 * @author HOSHI Seigo
	 * 
	 */
	public class MoveFolderCommand extends SequenceCommand implements ICommand, IResponder
	{
          /**
           * Executes the command
           * @param event
           * 
           */
          override public function execute(event:CairngormEvent):void
          {
            var delegate:EmailDelegate = new EmailDelegate(this);
            delegate.moveFolder(event.data.oldPath, event.data.newPath);
          }

          /**
           * Gets called on a success response from executing the command.
           * <p>Fires WebmailController.GET_FOLDERS</p>
           * @param event
           * 
           */
          public function result(event:Object):void
          {
            nextEvent = new CairngormEvent(WebmailController.GET_FOLDERS);
            executeNextCommand();
          }

          public function fault(event:Object):void
          {
            FaultUtil.checkFault(event as FaultEvent);
          }
        }
}
