package org.buni.mail.webmail.controller.calendar {

//import mx.rpc.IResponder;
//import com.adobe.cairngorm.commands.ICommand;
import mx.rpc.xml.ComplexString;
import com.adobe.cairngorm.control.CairngormEvent;
import org.buni.mail.webmail.model.CalendarInviteVO;
import org.buni.mail.webmail.model.ModelLocator;
import com.adobe.cairngorm.commands.ICommand;
import mx.rpc.IResponder;
import com.adobe.cairngorm.commands.SequenceCommand;
import mx.collections.ArrayCollection;
import mx.utils.ObjectUtil;
//import flash.utils.trace;

	public class GetFreeBusyCommand extends SequenceCommand implements ICommand, IResponder {
		var invite:CalendarInviteVO = null;
		var times:Array = [700,730,800,830,900,930,1000,1030,1100,1130,1200,1230,
		                   1300,1330,1400,1430,1500,1530,1600,1630,1700,1730,1800,
		                   1830,1900,1930];
    	var busy:Array = [true,true,true,true,true,true,true,true,true,true,true,true,
               true,true,true,true,true,true,true,true,true,true,true,
               true,true,true,true];
		
		
	    override public function execute(event:CairngormEvent):void {		
	  	    var start:Date = event.data.start as Date;
	  	    var end:Date = event.data.end as Date;
	  	    invite = event.data.invite as CalendarInviteVO;
	    
	        var model:ModelLocator = ModelLocator.getInstance();
	        var delegate:CalendarDelegate = new CalendarDelegate(this);
	        delegate.getFreebusy(start,end,invite);
        }

	    public function result(event:Object):void {
	    	var model:ModelLocator = ModelLocator.getInstance();
	    	var fbs:Object = event.result.iCalendar.iCal.FREEBUSY.FB;
	    	
	    	if (fbs is ArrayCollection) {
	    		for each (var o:Object in fbs) {
	    			var datestr:String = o.value as String;
	    			var startstr:String = datestr.slice(0,datestr.indexOf("/"));
	    			var endstr:String = datestr.slice(datestr.indexOf("/")+1,datestr.length);
	    			var start:Date = UTCDateFormatter.parseUTCDate(startstr);
	    			var end:Date = UTCDateFormatter.parseUTCDate(endstr);
	    			setBusy(start,end);
	    			trace("got result "+datestr);
	    		}
	    	} else if (fbs != null) {
	    		trace("fbs="+ObjectUtil.toString(fbs));
	    		var datestr:String = fbs.value as String;
    			var startstr:String = datestr.slice(0,datestr.indexOf("/"));
    			var endstr:String = datestr.slice(datestr.indexOf("/")+1,datestr.length);
    			var start:Date = UTCDateFormatter.parseUTCDate(startstr);
    			var end:Date = UTCDateFormatter.parseUTCDate(endstr);
    			setBusy(start,end);
	    		trace("got single result "+datestr);
	    	} else if (fbs == null) {
	    		trace("got no results");
	    	}
	        for (var x:int = 0; x < times.length; x++) {
	    	    this.invite["interval"+(x+1)] = busy[x];
	    	}
	    	trace(ObjectUtil.toString(this.invite));
      	    executeNextCommand();
	    }
	    
	    private function setBusy(start:Date, end:Date):void {
	    	var stime:int = start.hours * 100 + round(start.minutes);
	    	var etime:int = end.hours * 100 + end.minutes;
	    	for (var i:int = 0; i < times.length; i++) {
	    		setBusyHelper(stime, etime, busy, i);
	    	}

	    }
	    
	    private function setBusyHelper(start:int, end:int, busy:Array, element:int):void {
			if(start <= times[element] && end > times[element]) {
				busy[element] = false;
			}
	    }
	    
        public function fault(event:Object):void {
	   	 //   Alert.show(event.fault.toString());
	    }
	    
	    private function round(val:int):int {
	    	if (val < 30) {
	    	    return 00;
	    	} else {
	    		return 30
	    	}
	    }
	    
    }
} 