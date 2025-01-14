/**
 * All computational source code, intellectual property or other works 
 * contained herein are deemed Public Domain as per the Creative 
 * Commons Public Domain license.
 * 
 * http://creativecommons.org/licenses/publicdomain/
 * 
 * Author : Jason Hawyluk
 * Date: 26/02/2007
 * Reference : http://flexibleexperiments.wordpress.com
 * 
 * Jason Hawryluk disclaims all warranties with regard to this software, 
 * including all implied warranties of merchantability and fitness, in 
 * no event shall Jason Hawryluk be liable for any special, indirect or 
 * consequential damages or any damages whatsoever resulting from loss of 
 * use, data or profits, whether in an action of contract, negligence or 
 * other tortuous action, arising out of or in connection with the use or 
 * performance of this software.
 **/

package com{
	
	import flash.utils.Timer;
	import flash.events.Event;
	import flash.events.TimerEvent;
		
	public class DelayedTimer extends Timer {
		
		public function DelayedTimer  (){
			//Init the extended timer class.
			super(1000,1);
						
		}
		
		/**
		 * The object that called the timer.
		 **/
		private var _caller:Object;
		public function get caller():Object
		{
			return _caller;
		}
		public function set caller(value:Object):void
		{
			_caller = value;
		}
		
		/**
		 * The item passed. Used to store any other required info the 
		 * called function may require.
		 **/
		private var _item:Object;
		public function get item():Object
		{
			return _item;
		}
		public function set item(value:Object):void
		{
			_item = value;
		}
		
		
		/**
		 * The function passed used in the cleanup of the listener.
		 **/
		private var _func:Function; 
		public function get func():Function
		{
			return _func;
		}
		public function set func(value:Function):void
		{
			_func =value;
		}
		
		
		/**
		* Initialize the timer for the delayed call.
		**/ 
		public function startDelayedTimer(func:Function,event:Event=null, caller:Object=null,delay:Number=1000, repeat:int=1,item:Object=null):void {
			
			if (func == null){return;}
			
			this.item = item;
			
			
			this.func = func;
			
			if (caller !=null){
				this.caller=caller;
				
				if (event !=null){
					if (this.caller != event.target){
						this.caller = event.target;
					}
				}
				
			}
			
			if (running ==true){
				cancelDelayedTimer();
			}	
			
			this.delay = delay;
			this.repeatCount=repeat;
			
			//Use a weak refference so this gets cleaned up.
			addEventListener(TimerEvent.TIMER,func,false,0,true);
			
			start();
			
		}
		
		/**
		 * Clean up the call, the passed object, and stop the timer.
		 **/		
		public function cancelDelayedTimer():void{
			
			if (hasEventListener(TimerEvent.TIMER)){
				removeEventListener(TimerEvent.TIMER,func);
			}
			
			if (running ==true){
								
				_func=null;
				_caller=null;
				_item==null;
				stop();
				reset();
				
			}
			
		}
	}
	
	
	
}