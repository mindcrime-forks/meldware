package org.buni.mail.webmail.controller
{

    import mx.rpc.IResponder;
    import com.adobe.cairngorm.business.ServiceLocator;
    import mx.rpc.AsyncToken;
    
    import org.buni.mail.webmail.model.EmailVO;
    import org.buni.mail.webmail.model.FolderVO;
    import mx.rpc.http.HTTPService;

    /**
     * Delegate responsible for handling all server interactions related to a emails and folders that contain emails
     * @author Scotty Scott
     * @author Aron Sogor
     * @author James Ward
     * @author Mike Barker 
     * @author HOSHI Seigo
     * 
     */
    public class EmailDelegate
    {
      private var responder:IResponder;
      private var service:HTTPService;
      private var xmlservice:HTTPService;
                
        /**
         * Contructor
         * @param responder All operations on this delegate will call the IResponder for processing results
         * @return 
         * 
         */
        public function EmailDelegate( responder : IResponder )
        {
            this.service = ServiceLocator.getInstance().getHTTPService("RPCService");
            this.xmlservice = ServiceLocator.getInstance().getHTTPService("XMLRPCService");
            this.responder = responder;
        }

        /**
         * Gets all emails that the folder contains
         * @param folder
         * 
         */
        public function getEmailInFolder(folder:FolderVO):void
        {
            var params:Object = new Object();
          params.op = "getEmailInFolder";
          params.folder = folder.path;
          
            var call:AsyncToken = service.send(params);
            call.addResponder(responder);
        }
        
        /**
         * Gets all folders that the currently logged in user has
         * 
         */
        public function getFolders():void
        {
          var params:Object = new Object();
          params.op = "getFolders";
          
            var call:AsyncToken = service.send(params);
            call.addResponder(responder);
        }
        
        /**
         * Gets the body portion of the email
         * @param email
         * 
         */
        public function getEmailBody(email:EmailVO):void
        {
          trace('getEmailBody');
          var params:Object = new Object();
          params.op = "getEmailBody";
          params.id = email.id;
          params.folder = email.folder;
          
          var call:AsyncToken = service.send(params);
          call.addResponder(responder);
        }
        
        /**
         * Sends an email
         * @param email
         * 
         */
        public function sendEmail(email:EmailVO):void
        {
          var params:Object = new Object();
          params.operation = new Object();
          params.operation.op = "sendEmail";
          params.operation.from = email.sender;
          params.operation.to = email.toRecipients.toArray();
          params.operation.cc = email.ccRecipients.toArray();
          params.operation.bcc = email.bccRecipients.toArray();
          params.operation.subject = email.subject;
          params.operation.body = email.body;
          params.operation.tabid = email.tabid;
          
          var call:AsyncToken = xmlservice.send(params);
          call.addResponder(responder);
        }

        /**
         * Deletes an email
         * @param email
         * 
         */
        public function deleteEmail(email:EmailVO):void
        {
          var params:Object = new Object();
          params.op = "deleteEmail";
          params.id = email.id;
          params.folder = email.folder;
          
          var call:AsyncToken = service.send(params);
          call.addResponder(responder);
        }

        /**
         * Removes all emails that are currently flagged for deletion
         * 
         */
        public function emptyTrash():void
        {
          var params:Object = new Object();
          params.op = "deleteTrash";
          
          var call:AsyncToken = service.send(params);
          call.addResponder(responder);
        }
        
        /**
         * Creates a new folder for the currently logged in user
         * @param folder
         * 
         */
        public function newFolder(folder:FolderVO):void
        {
          var params:Object = new Object();
          params.op = "newFolder";
          params.folder = folder.path;
          
          var call:AsyncToken = service.send(params);
            call.addResponder(responder);
        }
        
        /**
         * Moves an email from one folder to another
         * @param email
         * @param toFolder
         * @param fromFolder
         * 
         */
        public function moveEmail(email:EmailVO,toFolder:FolderVO,fromFolder:FolderVO):void
        {
          var params:Object = new Object();
          params.op = "moveMail";
          params.targetname = toFolder.path;
          params.fromFolder = fromFolder.path;
          params.id = email.id;
          
          var call:AsyncToken = service.send(params);
          call.addResponder(responder);
        }
        
        /**
         * Saves an email to a folder
         * @param email
         * @param folder
         * 
         */
        public function saveToFolder(email:EmailVO, folder:FolderVO):void
        {
          var params:Object = new Object();
          params.op = "saveToFolder";
          params.from = email.sender;
          params.to = email.toRecipients;
          params.cc = email.ccRecipients;
          params.bcc = email.bccRecipients;
          params.subject = email.subject;
          params.body = email.body;
          params.attachments = email.attachments;
          params.foldername = folder.path;
          params.tabid = email.tabid;

          var call:AsyncToken = service.send(params);
          call.addResponder(responder);
        }
        
        /**
         * Deletes a folder
         * @param folder
         * 
         */
        public function deleteFolder(folder:FolderVO):void
        {
          var params:Object = new Object();
          params.op = "deleteFolder";
          params.folder = folder.path;
          
          var call:AsyncToken = service.send(params);
          call.addResponder(responder);
        }
        
        /**
         * Renames a folder
         * @param oldPath
         * @param newPath
         * 
         */
         // TODO Cleanup doc, not sure what oldPath and newPath should be
        public function renameFolder(oldPath:String, newPath:String):void
        {
          var params:Object = new Object();
          params.op = "moveFolder";
          params.folder = oldPath;
          params.targetname = newPath;
          
          var call:AsyncToken = service.send(params);
          call.addResponder(responder);
        }

        /**
         * Moves a folder
         * @param oldPath
         * @param newPath
         * 
         */
        public function moveFolder(oldPath:String, newPath:String):void
        {
          var params:Object = new Object();
          params.op = "moveFolder";
          params.folder = oldPath;
          params.targetname = newPath;
          
          var call:AsyncToken = service.send(params);
          call.addResponder(responder);
        }
    }
    
}
