package org.buni.mail.webmail.controller
{	
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.controls.Alert;
	import mx.collections.ArrayCollection;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.buni.mail.webmail.model.ModelLocator;
	import org.buni.mail.webmail.model.EventVO;
	import org.buni.mail.webmail.model.DayVO;
	import org.buni.mail.webmail.util.FaultUtil;
	
	/**
	 * INOPERATIVE	 *
	 * A command for get all events on a specific date in the calendar
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 * 
	 */
	public class GetDayCommand implements ICommand, IResponder
	{	  
	  /**
	   * Executes the command
	   * @param event event.data is expected to be a Date
	   * 
	   */
	  public function execute(event:CairngormEvent):void
	  {		
	    var model:ModelLocator = ModelLocator.getInstance();
	    var delegate:CalendarDelegate = new CalendarDelegate(this);
	    delegate.getDaysEvent(event.data as Date);
	  }

	  /**
	   * Gets called on a success response from executing the command.
	   * <p>event.result.calevent is expected to be either an ArrayCollection of EventVO or a single EventVO</p>
	   * @param event
	   * @see org.buni.mail.webmail.model.EventVO
	   */
	   //TODO Cleanup documentation, not exactly sure what all this function does: SS
	  public function result(event:Object):void
	  {
	    var model:ModelLocator = ModelLocator.getInstance();
	    
	    var newDay:DayVO = new DayVO();
      newDay.day = model.selectedDay.day;
      
      var events:ArrayCollection;
      if (event.result.calevent is ArrayCollection)
      {
        events = event.result.calevent;
      }
      else if (event.result.calevent != null)
      {
        events = new ArrayCollection();
        events.addItem(event.result.calevent);
      }
      
      if (events != null)
      {
        for (var i:int = 0; i < events.length; i++)
        {
          newDay.events.addItem(new EventVO(events[i]));
        }
        
        var col:int = 1;
        var numHandledEvents:int = 0;
        
        while (numHandledEvents < newDay.events.length)
        {
          for (var t:int = 0; t < (24 * 60); t++)
          {
            var h:Number = Math.floor(t / 60);
            var m:Number = (t % 60);
            for (var j:int = 0; j < newDay.events.length; j++)
            {
              if ( isNaN((newDay.events[j] as EventVO).displayColNum) && 
                   ((newDay.events[j] as EventVO).startDate.getHours() == h) &&
                   ((newDay.events[j] as EventVO).startDate.getMinutes() == m) )
              {
                (newDay.events[j] as EventVO).displayColNum = col;
                numHandledEvents++;
                t += (newDay.events[j] as EventVO).durationInMinutes;
                t--; // go back 1 minute
                break;
              }
            }
          }
          col++;
        }
        
        newDay.displayNumCols = (col - 1);
      }
      
	    model.selectedDay = newDay;
	  }

	  public function fault(event:Object):void
	  {
	   	FaultUtil.checkFault(event as FaultEvent);
	  }
	}
	
}
