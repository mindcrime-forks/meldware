package org.buni.mail.webmail.controller {

  import com.adobe.cairngorm.control.FrontController;
  import org.buni.mail.webmail.controller.calendar.CreateEventCommand;
  import org.buni.mail.webmail.controller.calendar.AddCalendarToSetCommand;
  import org.buni.mail.webmail.controller.calendar.UpdateEventCommand;
  import org.buni.mail.webmail.controller.calendar.RSVPCommand;
  import org.buni.mail.webmail.controller.calendar.GetFreeBusyCommand;

  
  /**
   * Controller for the Webmail portion of the application
   * @author Scotty Scott
   * @author Aron Sogor
   * @author James Ward
   * @author Mike Barker
   * 
   */
  public class WebmailController extends FrontController
  {
    public function WebmailController()
    {
      addCommand(LOGIN, LoginCommand);

      addCommand(GET_USER, GetUserCommand);
      addCommand(LOGOUT_USER, LogoutUserCommand);
      addCommand(SAVE_PREFERENCES, SavePreferencesCommand);

      addCommand(GET_EMAIL_IN_FOLDER, GetEmailInFolderCommand);
      addCommand(GET_EMAIL_BODY, GetEmailBodyCommand);
      addCommand(SEND_EMAIL, SendEmailCommand);
      addCommand(MOVE_EMAIL, MoveEmailCommand);
      addCommand(EMPTY_TRASH, EmptyTrashCommand);

      addCommand(GET_FOLDERS, GetFoldersCommand);
      addCommand(NEW_FOLDER, NewFolderCommand);
      addCommand(DELETE_FOLDER, DeleteFolderCommand);
      addCommand(RENAME_FOLDER, RenameFolderCommand);
      addCommand(MOVE_FOLDER, MoveFolderCommand);

      addCommand(NEW_MESSAGE, NewMessageCommand);
      addCommand(REPLY_MESSAGE, ReplyMessageCommand);
      addCommand(REPLY_ALL_MESSAGE, ReplyAllMessageCommand);
      addCommand(FORWARD_MESSAGE, ForwardMessageCommand);
      addCommand(DELETE_MESSAGE, DeleteMessageCommand);
      addCommand(SAVE_DRAFT, SaveDraftCommand);

      addCommand(GET_CONTACTS, GetContactsCommand);
      addCommand(NEW_CONTACT, NewContactCommand);
      addCommand(SAVE_CONTACT, SaveContactCommand);
      addCommand(EDIT_CONTACT, EditContactCommand);
      addCommand(DELETE_CONTACT, DeleteContactCommand);
      addCommand(SEARCH_SHARED_CONTACT, SearchSharedContactsCommand);
      addCommand(ADD_SHARED_CONTACT, AddSharedContactsCommand);
      addCommand(VIEW_CONTACT, ViewContactCommand);
      
      addCommand(GET_DAYEVENTS, GetDayCommand);
      addCommand(CHANGE_INVITE_STATUS, ChangeInviteStatusCommand);
      addCommand(ADD_INVITES, AddInvitesCommand);
      
      addCommand(CREATE_EVENT, CreateEventCommand);
      addCommand(SAVE_EVENT, UpdateEventCommand);
      addCommand(ADD_CALENDAR_TO_SET, AddCalendarToSetCommand);
      addCommand(RSVP_INVITE, RSVPCommand);
      
      addCommand(GET_USER_ACTION_SETS, GetUserActionSetsCommand);
      addCommand(SAVE_USER_ACTION_SETS, SaveUserActionSetsCommand);
      
      addCommand(GET_USER_AB_MOUNTS, GetUserABMountsCommand);
      addCommand(GET_SYSTEM_AB_MOUNTS, GetSystemABMountsCommand);
      addCommand(SAVE_USER_AB_MOUNTS, SaveUserABMountsCommand);
      
      addCommand(GET_FREEBUSY, GetFreeBusyCommand);
      addCommand(GET_SUGGESTIONS, GetSuggestionsCommand);
   
      addCommand(SEARCH_AB, SearchABCommand);
    }

    public static const LOGIN:String = "login";
    
    public static const GET_USER:String = "getUser";
    public static const LOGOUT_USER:String = "logoutUser";
    public static const SAVE_PREFERENCES:String = "savePreferences";
    
    public static const GET_EMAIL_IN_FOLDER:String = "getEmailInFolder";
    public static const GET_EMAIL_BODY:String = "getEmailBody";
    
    public static const SEND_EMAIL:String = "sendEmail";
    
    public static const MOVE_EMAIL:String = "moveEmail";
    
    public static const EMPTY_TRASH:String = "emptyTrash";
    
    public static const GET_FOLDERS:String = "getFolders";
    public static const NEW_FOLDER:String = "newFolder";
    public static const DELETE_FOLDER:String = "deleteFolder";
    public static const RENAME_FOLDER:String = "renameFolder";
    public static const MOVE_FOLDER:String = "moveFolder";
    
    public static const NEW_MESSAGE:String = "newMessage";
    public static const REPLY_MESSAGE:String = "replyMessage";
    public static const REPLY_ALL_MESSAGE:String = "replyAllMessage";
    public static const FORWARD_MESSAGE:String = "forwardMessage";
    public static const DELETE_MESSAGE:String = "deleteMessage";
    public static const SAVE_DRAFT:String = "saveDraft";
    
    public static const GET_CONTACTS:String = "getContacts";
    public static const NEW_CONTACT:String = "newContact";
    public static const EDIT_CONTACT:String = "editContact";
    public static const SAVE_CONTACT:String = "saveContact";
    public static const DELETE_CONTACT:String = "deleteContact";
    public static const SEARCH_SHARED_CONTACT:String = "SearchSharedContacts";
    public static const ADD_SHARED_CONTACT:String = "AddSharedContacts";
    public static const VIEW_CONTACT:String = "viewContact";
    
    public static const GET_DAYEVENTS:String = "getDayEvents";
    public static const ADD_CALENDAR_TO_SET:String = "addCalendarToSet";
    public static const SAVE_EVENT:String = "saveCalendarEvent";
    public static const CREATE_EVENT:String = "createCalendarEvent";
    public static const CHANGE_INVITE_STATUS:String = "changeInviteStatus";
    public static const ADD_INVITES:String = "AddInvites";
    public static const RSVP_INVITE:String = "RSVPInvite";
    

    public static const GET_USER_ACTION_SETS:String = "GetUserActionSets"; 
    public static const SAVE_USER_ACTION_SETS:String = "SaveUserActionSets"; 
    
    public static const GET_USER_AB_MOUNTS:String = "GetUserABMounts"; 
    public static const SAVE_USER_AB_MOUNTS:String = "SaveUserABMounts"; 
    public static const GET_SYSTEM_AB_MOUNTS:String = "GetSystemABMounts";  
    
    public static const GET_FREEBUSY:String = "GetFreebusy";  
    public static const GET_SUGGESTIONS:String = "GetSuggestions"; 
    
    public static const SEARCH_AB:String = "SearchAB"; 
    public static const SHOW_AB_SEARCH:String = "ShowABSearch";
    public static const SHOW_AB_ENTRY:String = "ShowABEntry";
  }
}
