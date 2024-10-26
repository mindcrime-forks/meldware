package org.buni.meldware.admin.controller {

  import com.adobe.cairngorm.control.FrontController;

  
  public class AdminController extends FrontController {
    public function AdminController() {
      addCommand(LOGIN, LoginCommand);
      addCommand(GET_ABMOUNTS, GetSystemABMountsCommand);

      addCommand(GET_USER, GetUserCommand);
      addCommand(EDIT_USER, EditUserCommand);
      addCommand(DELETE_USER, DeleteUserCommand);
      addCommand(GET_USERS, GetUsersCommand);
      addCommand(ADD_USER, AddUserCommand);
      addCommand(LOGOUT_USER, LogoutUserCommand);

      addCommand(ADD_SERVICE, AddServiceCommand);
      addCommand(EDIT_SERVICE, EditServiceCommand);
      addCommand(DELETE_SERVICE, DeleteServiceCommand);
      
      addCommand(SAVE_LOCALDOMAINS, SaveLocalDomainsCommand);
      
      addCommand(ADD_THREADPOOL, AddThreadPoolCommand);
      addCommand(EDIT_THREADPOOL, EditThreadPoolCommand); 
      addCommand(DELETE_THREADPOOL, DeleteThreadPoolCommand);   
      
      addCommand(ADD_PROTOCOL, AddProtocolCommand);   
      addCommand(EDIT_PROTOCOL, EditProtocolCommand);
      addCommand(DELETE_PROTOCOL, DeleteProtocolCommand);
      
      addCommand(ADD_MAILLIST, AddMailListCommand);
      addCommand(EDIT_MAILLIST, EditMailListCommand);
      addCommand(DELETE_MAILLIST, DeleteMailListCommand);
      addCommand(GET_MAILLIST_MEMBERS, GetMailListMembersCommand);
      
      addCommand(ADD_MAILLIST_MEMBER, AddMailListMemberCommand);
      addCommand(DELETE_MAILLIST_MEMBER, DeleteMailListMemberCommand);
      
      addCommand(ADD_LDAP, AddLdapCommand);
      
      addCommand(GET_SERVICES, GetServicesCommand);
      addCommand(GET_PROTOCOLS, GetProtocolsCommand);
      addCommand(GET_THREADPOOLS, GetThreadPoolsCommand);
      addCommand(GET_LOCALDOMAINS, GetLocalDomainsCommand);
      addCommand(GET_SSLDOMAINS, GetSSLDomainsCommand);
      addCommand(GET_USERREPOSITORIES, GetUserRepositoriesCommand);
      addCommand(GET_DOMAINGROUPS, GetDomainGroupsCommand);
      addCommand(GET_MAILLISTENERCHAINS, GetMailListenerChainsCommand);      
      addCommand(GET_MAILBOXMANAGERS, GetMailboxManagersCommand);
      addCommand(GET_MAILBODYMANAGERS, GetMailbodyManagersCommand);
      addCommand(GET_MAILLISTS, GetMailListsCommand);
    }

    public static const GET_USER:String = "getUser";
    public static const EDIT_USER:String = "editUser";
    public static const DELETE_USER:String = "deleteUser";
    public static const GET_USERS:String = "getUsers";
    public static const ADD_USER:String = "addUser";
    public static const LOGOUT_USER:String = "logoutUser";
    
    
    public static const ADD_SERVICE:String = "addService";
    public static const EDIT_SERVICE:String = "editService";
    public static const DELETE_SERVICE:String = "deleteService";
    //public static const GET_SERVICES:String = "getServices";
    
    public static const ADD_PROTOCOL:String = "addProtocol";
    public static const EDIT_PROTOCOL:String = "editProtocol";
    public static const DELETE_PROTOCOL:String = "deleteProtocol";
    
    public static const ADD_THREADPOOL:String = "addThreadPool";
    public static const EDIT_THREADPOOL:String = "editThreadPool";
    public static const DELETE_THREADPOOL:String = "deleteThreadPool";
    
    public static const ADD_MAILLIST:String = "addMailList";
    public static const EDIT_MAILLIST:String = "editMailList";
    public static const DELETE_MAILLIST:String = "deleteMailList";
    
    public static const ADD_MAILLIST_MEMBER:String = "addMailListMember";
    public static const DELETE_MAILLIST_MEMBER:String = "deleteMailListMember";

    public static const USER_ADDED:String = "userAdded";
    public static const USER_EDITED:String = "userEdited";
    public static const USER_DELETED:String = "userDeleted";
    public static const USER_DETAIL:String = "userDetail";
    
    
    public static const SERVICE_ADDED:String = "serviceAdded";
    public static const SERVICE_EDITED:String = "serviceEdited";
    public static const SERVICE_DELETED:String = "serviceDeleted";
    public static const SERVICE_DETAIL:String = "serviceDetail";
    
    public static const PROTOCOL_ADDED:String = "protocolAdded";
    public static const PROTOCOL_EDITED:String = "protocolEdited";
    public static const PROTOCOL_DELETED:String = "protocolDeleted";
    public static const PROTOCOL_DETAIL:String = "protocolDetail";
    
    public static const MAILLIST_ADDED:String = "mailListAdded";
    public static const MAILLIST_EDITED:String = "mailListEdited";
    public static const MAILLIST_DELETED:String = "mailListDeleted";
    public static const MAILLIST_DETAIL:String = "mailListDetail";
    public static const GOT_MAILLIST_MEMBERS:String = "gotMailListMembers";
    
    public static const MAILLIST_MEMBER_ADDED:String = "mailListMemberAdded";
    public static const MAILLIST_MEMBER_DELETED:String = "mailListMemberDeleted";
    
    public static const THREADPOOL_ADDED:String = "threadPoolAdded";
    public static const THREADPOOL_EDITED:String = "threadPoolEdited";
    public static const THREADPOOL_DELETED:String = "threadPoolDeleted";
    public static const GOT_THREADPOOLS:String = "GotThreadPools";
    public static const THREADPOOL_DETAIL:String = "threadPoolDetail";
    
    public static const SAVE_LOCALDOMAINS:String = "saveLocalDomains";

    public static const GET_SERVICES:String = "GetServices";
    public static const GET_PROTOCOLS:String = "GetProtocols";
    public static const GOT_PROTOCOLS:String = "GotProtocols";
    public static const GET_THREADPOOLS:String = "GetThreadPools";
    public static const GET_LOCALDOMAINS:String = "GetLocalDomains";
    public static const GET_SSLDOMAINS:String = "GetSSLDomains";
    public static const GET_USERREPOSITORIES:String = "GetUserRepositories";
    public static const GET_DOMAINGROUPS:String = "GetDomainGroups";
    public static const GET_MAILLISTENERCHAINS:String = "GetMailListenerChains";
    public static const GET_MAILBOXMANAGERS:String = "GetMailboxManagers";
    public static const GET_MAILBODYMANAGERS:String = "GetMailbodyManagers";
    public static const GET_MAILLISTS:String = "GetMailLists";
    public static const GET_MAILLIST_MEMBERS:String = "GetMailListMembers";
    public static const LOGIN:String = "login";
    
    public static const ADD_LDAP:String = "AddLDAP";
    public static const GET_ABMOUNTS:String = "GetSystemABMounts";
    
  }
}
