package org.buni.mail.webmail.model
{

    import com.adobe.cairngorm.vo.ValueObject;
  	import mx.collections.ArrayCollection;
  	import mx.core.IUID;

   	[Bindable]
	public class AddressBookEntryVO implements com.adobe.cairngorm.vo.ValueObject, IUID
	{	
//		private var _id:Number;   //flex UID = ldap UID.hashCode();
		private var _uid:String;	 //ldap uid
		private var _sn:String;
		private var _givenName:String;
		private var _mail:ArrayCollection;
		private var _street:String;
		private var _st:String;
		private var _postalAddress:String;
		private var _postalCode:String;
		private var _mobile:String;
		private var _pager:String;
		private var _telephoneNumber:String;
		private var _l:String;
		private var _initials:String;
		

        public function AddressBookEntryVO(o:Object=null) {
        	if(o!=null) {
        		this.uid = ModelUtil.nvl(o,"uid");
        	//	this.realUid = ModelUtil.nvl(o,"uid");
        		this.sn = ModelUtil.nvl(o,"Surname");
        		this.givenName = ModelUtil.nvl(o,"GivenName");
        		this.street = ModelUtil.nvl(o,"Street");
        		this.st = ModelUtil.nvl(o,"StateCode");
        		this.postalAddress = ModelUtil.nvl(o,"PostalAddress");
        		this.postalCode = ModelUtil.nvl(o,"PostalCode");
        		this.mobile = ModelUtil.nvl(o,"Mobile");
        		this.pager = ModelUtil.nvl(o,"Pager");
        		this.telephoneNumber = ModelUtil.nvl(o,"TelephoneNumber");
        		this.l = ModelUtil.nvl(o,"Location");
        		this.initials = ModelUtil.nvl(o,"Initials");
        		var m:Object = ModelUtil.nvl(o,"Mail");
        	    if(m != null && !(m is ArrayCollection)) {
        			this.mail = new ArrayCollection();
        			this.mail.addItem(m as String);
        		} else if (m != null && m is ArrayCollection) {
        			  var ma:ArrayCollection = m as ArrayCollection;
                      for (var i:int = 0; i < ma.length; i++) {
                      	this.mail.addItem(ma.getItemAt(i));
                      }
        		}
        	}
        }
        
        public function get uid():String {
        	return this._uid;
        }
        
        public function set uid(theUid:String):void {
        	this._uid=theUid;
        }
        
        public function set sn(thesn:String):void {
        	this._sn = thesn;
        }
        
        public function get sn():String {
        	return this._sn;
        }
        
        public function set surname(thesn:String):void {
        	this._sn = thesn;
        }
        
        public function get surname():String {
        	return this._sn;
        }
        
        public function set givenName(gName:String):void {
        	this._givenName = gName;
        }
        
        public function get givenName():String {
        	return this._givenName;	
        }
        
        public function get mail():ArrayCollection {
        	return this._mail;
        }
        
        public function set mail(themail:ArrayCollection):void {
        	this._mail = themail;
        }
      
        
        public function get mailString():String {
        	var mailString:String = "";
        	for (var i:int = 0 ; i < mail.length; i++) {
        		mailString += mail.getItemAt(i);
        	}
        	return mailString;
        }
        
        public function set street(thestreet:String):void {
        	this._street=thestreet;
        }
        
        public function get street():String {
        	return this._street;
        }
        
        public function get st():String {
        	return this._st;
        }
        
        public function set st(tst:String):void {
        	this._st = tst;
        }
        
        public function set postalAddress(paddress:String):void {
        	this._postalAddress=paddress;
        }
        
        public function get postalAddress():String {
        	return this._postalAddress;
        }
        
        public function set postalCode(pcode:String):void {
        	this._postalCode=pcode;
        }
        
        public function get postalCode():String {
        	return this._postalCode;
        }
        
        public function get l():String {
        	return this._l;
        }
        
        public function set l(thel:String):void {
        	this._l = thel;
        }
        
        public function set initials(inits:String):void {
        	this._initials = inits;
        }
        
        public function get initials():String {
        	return this._initials;
        }
        
        public function set telephoneNumber(num:String):void {
        	this._telephoneNumber = num;
        }
        
        public function get telephoneNumber():String { 
        	return this._telephoneNumber;
        }

        public function set mobile(num:String):void {
        	this._mobile = num;
        }
        
        public function get mobile():String { 
        	return this._mobile;
        }
        
        public function set pager(num:String):void {
        	this._pager = num;
        }
        
        public function get pager():String { 
        	return this._pager;
        }

	}

}