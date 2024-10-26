package org.buni.meldware.admin.model
{

  	import com.adobe.cairngorm.vo.ValueObject;
   	import mx.core.IUID;
   	import mx.collections.ArrayCollection;

   	[Bindable]
	public class DomainGroupVO implements com.adobe.cairngorm.vo.ValueObject
	{		
		public var name : String;
		public var postmaster: String;
        public var local : Boolean;
        public var domains : ArrayCollection;
        
        public function DomainGroupVO(domainGroup:Object=null) {
            trace("constructing DomainGroupVO");
            if (domainGroup != null) {
                trace("DomainGroup was not null");
                this.name = domainGroup.name;
                this.local = domainGroup.local;
                if (domainGroup.domain != null && domainGroup.domain is ArrayCollection) {
                	this.domains = domainGroup.domain;
                } else if (domainGroup.domain != null) {
                	this.domains = new ArrayCollection();
                	this.domains.addItem(domainGroup.domain);
                }
            }
        }

        public function toString():String {
            trace("toString called on "+this.name);
            if(name == null) {
              return "- ";
            } else {
              return "" + name;
            }
        }
	}
}

