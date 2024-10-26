package org.buni.mail.webmail.controller
{
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import com.adobe.cairngorm.business.ServiceLocator;
	import org.buni.mail.webmail.model.UserVO;
	import org.buni.mail.webmail.model.ContactVO;
	import mx.rpc.http.HTTPService;

	/**
	 * Delegate responsible for handling all server interactions related to a Contact.
	 * @author Scotty Scott
	 * @author Aron Sogor
	 * @author James Ward
	 * @author Mike Barker 
	 * 
	 */
	public class ContactDelegate
	{
		/**
		 * Contructor
		 * @param responder All operations on this delegate will call the IResponder for processing results
		 * @return 
		 * 
		 */		
		public function ContactDelegate(responder:IResponder)
		{
			this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
			this.responder = responder;
		}

		/**
		 * Gets all contacts related to the currently logged in user
		 * 
		 */
		public function getContacts():void
		{
			var params:Object = new Object();
			params.op = "getContacts";
			
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}

		/**
		 * Allows for the currently logged in user to search for a contact that is shared
		 * @param searchWord keyword to search for
		 * 
		 */
		public function searchSharedContacts(searchWord:String):void
		{
			var params:Object = new Object();
			params.op = "searchSharedContacts";
			params.searchWord = searchWord;
			
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}
		
		/**
		 * Saves a single contact instance
		 * @param contact Contact to save
		 * 
		 */
		public function saveContact(contact:ContactVO):void
		{
			var params:Object = new Object();
			params.op = "saveContact";
			params.recordId = contact.id;
			params.tabid = contact.tabid;
			params.contactName = contact.contactName;
			params.email = contact.email;
			params.organization = contact.organization;
			params.shortName = contact.shortName;
			params.homePhone = contact.homePhone;
			params.mobilePhone = contact.mobilePhone;
			params.officePhone = contact.officePhone;
			params.faxNumber = contact.faxNumber;
			
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}

		/**
		 * Adds a list of shared contacts to the currently logged in user's contact list
		 * @param contacts Array of ContactVO
		 * @see org.buni.mail.webmail.model.ContactVO
		 */
		public function addSharedContact(contacts:Array):void
		{
			var params:Object = new Object();
			params.op = "addSharedContact";
			params.users = "";
			
			for each (var contact:ContactVO in contacts) {
                if (params.users == "")
                    params.users = contact.userName;
                else
                    params.users += "," + contact.userName;
            }
			
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}
		
		/**
		 * Deletes a single instance of a contact
		 * @param contact Contact to remove from the currently logged in user's contact list
		 * 
		 */
		public function deleteContact(contact:ContactVO):void
		{
			var params:Object = new Object();
			params.op = "deleteContact";
			params.recordId = contact.id;
			
			var call:AsyncToken = service.send(params);
			call.addResponder(responder);
		}
		
		private var responder:IResponder;
		private var service:HTTPService;
	}
	
}
