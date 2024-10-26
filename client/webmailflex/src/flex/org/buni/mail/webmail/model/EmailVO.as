package org.buni.mail.webmail.model
{
	import mx.collections.ArrayCollection;
  import mx.collections.ICollectionView;
	import com.adobe.cairngorm.vo.ValueObject;
	import mx.core.IUID;
	
  [Bindable]
	public class EmailVO implements ValueObject, IUID
	{
	  public var tabid:Number;
	  public var id:String;
		public var sender:EmailAddressVO;
		public var status:String;
		[ArrayElementType("org.buni.mail.webmail.model.RecipientAddressVO")]
		private var _recipients:ArrayCollection;
/*		[ArrayElementType("org.buni.mail.webmail.model.EmailAddressVO")]
		public var toRecipients:ArrayCollection;
		[ArrayElementType("org.buni.mail.webmail.model.EmailAddressVO")]		
		public var ccRecipients:ArrayCollection;
		[ArrayElementType("org.buni.mail.webmail.model.EmailAddressVO")]		
		public var bccRecipients:ArrayCollection;*/
		public var subject:String;
		public var date:Date;
		public var body:String;
		[ArrayElementType("org.buni.mail.webmail.model.AttachmentVO")]		
		public var attachments:ArrayCollection;
		public var folder:String;
		public var folderId:Number;
		
		// converts an unstructred Email object to a structured one
		public function EmailVO(o:Object=null)
		{
			if (o != null)
			{
			  id = o.id;
			  folder = o.folder;
			  folderId = new Number(o.folderId);
				sender = new EmailAddressVO(o.sender);
				status = o.status;
				if (o.recipients != null) {
                    var toRecs:ArrayCollection = o.recipients.to != null ? 
                                   getEmailAddresses(o.recipients.to,RecipientAddressVO.TO) : new ArrayCollection();
                    var ccRecs:ArrayCollection = o.recipients.cc != null ? 
                                   getEmailAddresses(o.recipients.cc,RecipientAddressVO.CC) : new ArrayCollection();
				    var bccRecs:ArrayCollection = o.recipients.bcc != null ? 
				                    getEmailAddresses(o.recipients.bcc,RecipientAddressVO.BCC) : new ArrayCollection();
					this._recipients = new ArrayCollection();
					for each (var to:RecipientAddressVO in toRecs) {
						this._recipients.addItem(to);
					} 
					for each (var cc:RecipientAddressVO in ccRecs) {
						this._recipients.addItem(cc);
					} 
					for each (var bcc:RecipientAddressVO in bccRecs) {
						this._recipients.addItem(bcc);
					} 
				}
                else
                {
					this._recipients = new ArrayCollection();
                }

				subject = o.subject;
				date = new Date(o.date);
				body = o.body;
				if (o.attachments != null) {
				     attachments = getAttachments(o.attachments);
				}
			}
            else
            {

					this._recipients = new ArrayCollection();
            }
		}
		
		public function get uid():String
		{
		  return folderId.toString() + ":" + id;
		}
		
		public function set uid(s:String):void
		{
		  //id = s;
		}
		
		private function getEmailAddresses(a:*, type:int):ArrayCollection
		{
			var addresses:ArrayCollection = new ArrayCollection();
			
			if (a is Array)
			{
				for (var i:int = 0; i < a.length; i++)
				{
					var e1:RecipientAddressVO = new RecipientAddressVO( a[i], type);
					addresses.addItem(e1);
				}
			} else if (a is ArrayCollection) {
				for (var x:int = 0; x < a.length; x++)
				{
					var e2:RecipientAddressVO = new RecipientAddressVO( a.getItemAt(x), type);
					addresses.addItem(e2);
				}
			} else if (a is Object)
			{
				var e3:RecipientAddressVO = new RecipientAddressVO(new EmailAddressVO(a), type);
				addresses.addItem(e3);
			}
			
			return addresses;
		}
		
		private function getAttachments(a:*):ArrayCollection {
			var _attachments:ArrayCollection = new ArrayCollection();
			
			if (a is Array) {
				for (var i:int = 0; i < a.length; i++) {
					var e1:AttachmentVO = new AttachmentVO(a[i]);
					_attachments.addItem(e1);
				}
			} else if (a is ICollectionView) {
				for (var x:int = 0; x < a.length; x++) {
					var e3:AttachmentVO = new AttachmentVO(a.getItemAt(x));
					_attachments.addItem(e3);
				}
			} else if (a is Object) {
				var e2:AttachmentVO = new AttachmentVO(a);
				_attachments.addItem(e2);
			}
			
			return _attachments;
		}
		
		[ArrayElementType("org.buni.mail.webmail.model.RecipientAddressVO")]
		public function get recipients():ArrayCollection {
			return this._recipients;
		}
		
		public function set recipients(recs:ArrayCollection):void {
			trace("set recipients has been called on "+this.subject+" "+this.uid);
			this._recipients = recs;
		}
		
		[ArrayElementType("org.buni.mail.webmail.model.RecipientAddressVO")]
		public function get toRecipients():ArrayCollection {
			var tos:ArrayCollection = new ArrayCollection();
			for(var i:int = 0; i < _recipients.length; i++) {
				var r:RecipientAddressVO = _recipients.getItemAt(i) as RecipientAddressVO;
				if(r.type == RecipientAddressVO.TO) {
					tos.addItem(r);
				}
			}
			return tos;
		}
		
		public function set toRecipients(tos:ArrayCollection):void {
			this._recipients = this._recipients != null ? this._recipients : new ArrayCollection();
			for(var i:int = 0; i < tos.length; i++) {
				this._recipients.addItem(new RecipientAddressVO(tos.getItemAt(i) as RecipientAddressVO, RecipientAddressVO.TO));
			}
		}
		
		[ArrayElementType("org.buni.mail.webmail.model.RecipientAddressVO")]
		public function get ccRecipients():ArrayCollection {
			var ccs:ArrayCollection = new ArrayCollection();
			for(var i:int = 0; i < _recipients.length; i++) {
				var r:RecipientAddressVO = _recipients.getItemAt(i) as RecipientAddressVO;
				if(r.type == RecipientAddressVO.CC) {
					ccs.addItem(r);
				}
			}
			return ccs;
		}
	
		public function set ccRecipients(ccs:ArrayCollection):void {
			this._recipients = this._recipients != null ? this._recipients : new ArrayCollection();
			for(var i:int = 0; i < ccs.length; i++) {
				this._recipients.addItem(new RecipientAddressVO(ccs.getItemAt(i) as RecipientAddressVO, RecipientAddressVO.CC));
			}
		}	
	
		[ArrayElementType("org.buni.mail.webmail.model.RecipientAddressVO")]
		public function get bccRecipients():ArrayCollection {
			var bccs:ArrayCollection = new ArrayCollection();
			for(var i:int = 0; i < _recipients.length; i++) {
				var r:RecipientAddressVO = _recipients.getItemAt(i) as RecipientAddressVO;
				if(r.type == RecipientAddressVO.BCC) {
					bccs.addItem(r);
				}
			}
			return bccs;
		}	

	
		public function set bccRecipients(bccs:ArrayCollection):void {
			this._recipients = this._recipients != null ? this._recipients : new ArrayCollection();
			for(var i:int = 0; i < bccs.length; i++) {
				this._recipients.addItem(new RecipientAddressVO(bccs.getItemAt(i) as RecipientAddressVO,RecipientAddressVO.BCC));
			}
		}	
	

		
		public function toString():String {
			return id + " " + body;
		}
	}
	
}
