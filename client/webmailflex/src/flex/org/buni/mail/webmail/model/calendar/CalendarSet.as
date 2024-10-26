package org.buni.mail.webmail.model.calendar
{
	import qs.calendar.CalendarSet;
	import qs.utils.DateRange;

	public class CalendarSet extends qs.calendar.CalendarSet
	{
		private var _range:DateRange = new DateRange();
		
		public function CalendarSet(range:DateRange)
		{
			super();
			this._range = range;
		}
		
		public function get range():DateRange
		{
			return _range;
		}
	}
}