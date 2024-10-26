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
	
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.EventVO;
	import org.buni.mail.webmail.util.FaultUtil;
	
	/**
	 * A command for saving an event
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker
	 * 
	 */
	public class SaveEventCommand extends SequenceCommand implements ICommand, IResponder
	{
	
	  /**
	   * Executes the command
	   * @param event event.data is expected to be an instance of EventVO
	   * @see org.buni.mail.webmail.model.EventVO
	   * 
	   */
	  override public function execute(event:CairngormEvent):void
	  {		
	    var model:ModelLocator = ModelLocator.getInstance();
	    var delegate:CalendarDelegate = new CalendarDelegate(this);
	    delegate.saveEvent(event.data as EventVO);
	  }

	  /**
	   * Gets called on a success response from executing the command.
	   * <p>Fires WebmailController.GET_DAYEVENTS for the selectedDay in the model</p>
	   * @param event
	   * 
	   */
	  public function result(event:Object):void
	  {
	      var model:ModelLocator = ModelLocator.getInstance();
	      nextEvent = new CairngormEvent(WebmailController.GET_DAYEVENTS);
	      nextEvent.data = model.selectedDay.day;
	      executeNextCommand();
	  }

	  public function fault(event:Object):void
	  {
	   	FaultUtil.checkFault(event as FaultEvent);
	  }
	}
	
}
