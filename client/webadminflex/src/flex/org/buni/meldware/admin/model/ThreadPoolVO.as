package org.buni.meldware.admin.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;

   	[Bindable]
	public class ThreadPoolVO implements com.adobe.cairngorm.vo.ValueObject
	{		
		public var threadPoolName : String;
        public var initial: Number;
        public var max : Number;
        public var min : Number; 
        public var idleKeepAlive: Number;
        public var poolSize: Number;
        public var activePoolSize: Number;
        
        public function ThreadPoolVO(threadpool:Object=null) {
            trace("constructing ThreadPoolVO");
            if (threadpool != null) {
                trace("ThreadPool was not null");
                this.threadPoolName = threadpool.name;
                        trace("tried to set name");

                        trace("name is " + this.threadPoolName);                        
                this.initial = new Number(threadpool.initial);
                this.max = new Number(threadpool.max);
                this.min = new Number(threadpool.min);
                this.idleKeepAlive = new Number(threadpool.idleKeepAlive);
                this.poolSize = new Number(threadpool.poolSize);
                this.activePoolSize = new Number(threadpool.activePoolSize);
            }
        }

        public function toString():String {
            trace("toString called on "+this.threadPoolName);
            if(threadPoolName == null) {
              return "- ";
            } else {
              return "" + threadPoolName;
            }
        }
	}
}

