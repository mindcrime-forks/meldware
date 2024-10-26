/*Copyright (c) 2006 Adobe Systems Incorporated

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
*/
package qs.calendar
{
	import mx.collections.ArrayCollection;
	import mx.collections.Sort;
	import flash.events.Event;
	import flash.utils.IExternalizable;
	import flash.utils.IDataInput;
	import flash.utils.IDataOutput;
	import flash.events.EventDispatcher;
	import qs.utils.SortedArray;
	import qs.calendar.Calendar;
	import qs.calendar.CalendarEvent;
	
	[Event("changeCalendars")]
	[Event("changeEvents")]
	public class CalendarSet extends EventDispatcher
	{
		private var years:Object = {};
		private var _calendars:Array;
		private var _events:Array;
		
		public function CalendarSet(cals:Array = null):void
		{
			_events = [];
			_calendars = (cals == null)? []:cals;
			for(var i:int = 0;i<_calendars.length;i++)
			{
				_events = _events.concat(_calendars[i].events);
			}
		}
		
		[Bindable("changeCalendars")]
		public function get calendars():Array
		{
			return _calendars;
		}

        public function addCalendar(cal:Calendar):void
        {
        	_calendars.push(cal);
        	if(cal.events != null)
        	{
        		_events = _events.concat(cal.events);
        		dispatchEvent(new Event("changeEvents"));
        	}
        	dispatchEvent(new Event("changeCalendars"));
        }
        
        [Bindable("changeEvents")]
		public function get events():Array
		{
			return _events;
		}
		
		
		public function addEvent(event:CalendarEvent,cal:Calendar):void
		{
			cal.addEvent(event);
			_events.push(event);
			dispatchEvent(new Event("changeEvents"));	
		}
		
		public function removeEvent(event:CalendarEvent):void
		{
			var index:int = _events.indexOf(event);
			
			//cut out the removeable
			_events.splice(index,1);
			// cut it out from each calendar
			var cal:Calendar = event.calendar;
            index = cal.events.indexOf(event);
            if(index > -1)
   		    	cal.events.splice(index,1);
			
			dispatchEvent(new Event("changeEvents"));	
		}
		
		public function updateEvent(oldevent:CalendarEvent,newevent:CalendarEvent):void
		{
			var index:int = _events.indexOf(oldevent);
			_events[index] = newevent;
			
		    // swap the old event out in calendar too
		    var cal:Calendar = oldevent.calendar;
			index = cal.events.indexOf(oldevent);
            if(index > -1)
              	cal.events[index] = newevent;
			
			newevent.calendar = oldevent.calendar;
			
			dispatchEvent(new Event("changeEvents"));	
		}
	}
}

