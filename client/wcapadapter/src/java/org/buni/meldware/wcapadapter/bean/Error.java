package org.buni.meldware.wcapadapter.bean;

public class Error {
	public static final Error LOGOUT = new Error(-1);//Logout successful.
	public static final Error LOGIN_FAILED = new Error(1); // Login failed, session ID timed out. Invalid session ID 
	public static final Error LOGIN_OK_DEFAULT_CALENDAR_NOT_FOUND = new Error(2); //login.wcap was successful, but the default calendar for this user was not found. A new default calendar set to the userid was created.
	public static final Error DELETE_EVENTS_BY_ID_FAILED = new Error(6); 
	public static final Error SETCALPROPS_FAILED = new Error(8); 
	public static final Error FETCH_EVENTS_BY_ID_FAILED = new Error(9); 
	public static final Error CREATECALENDAR_FAILED = new Error(10); 
	public static final Error DELETECALENDAR_FAILED = new Error(11); 
	public static final Error ADDLINK_FAILED = new Error(12); 
	public static final Error FETCHBYDATERANGE_FAILED = new Error(13); 
	public static final Error STOREEVENTS_FAILED = new Error(14); 
	public static final Error STORETODOS_FAILED = new Error(15); 
	public static final Error DELETE_TODOS_BY_ID_FAILED = new Error(16);  
	public static final Error FETCH_TODOS_BY_ID_FAILED = new Error(17);  
	public static final Error FETCHCOMPONENTS_FAILED_BAD_TZID = new Error(18); // Command failed to find correct tzid. Applies to fetchcomponents_by_range, fetchevents_by_id, fetchtodos_by_id.
	public static final Error SEARCH_CALPROPS_FAILED = new Error(19);  
	public static final Error GET_CALPROPS_FAILED = new Error(20); 
	public static final Error DELETECOMPONENTS_BY_RANGE_FAILED = new Error(21);  
	public static final Error DELETEEVENTS_BY_RANGE_FAILED = new Error(22); 
	public static final Error DELETETODOS_BY_RANGE_FAILED = new Error(23);  
	public static final Error GET_ALL_TIMEZONES_FAILED = new Error(24);  
	public static final Error CREATECALENDAR_ALREADY_EXISTS_FAILED = new Error(25); // The command createcalendar.wcap failed. A calendar with that name already exists in the database.
	public static final Error SET_USERPREFS_FAILED = new Error(26); 
	public static final Error CHANGE_PASSWORD_FAILED = new Error(27);  
	public static final Error ACCESS_DENIED_TO_CALENDAR = new Error(28);  // The user is denied access to a calendar. 
	public static final Error CALENDAR_DOES_NOT_EXIST = new Error(29);  // The requested calendar does not exist in the database. 
	public static final Error ILLEGAL_CALID_NAME = new Error(30); //	createcalendar.wcap failed. Invalid calid passed in.
	public static final Error CANNOT_MODIFY_LINKED_EVENTS = new Error(31); // storeevents.wcap failed. The event to modify was a linked event.
	public static final Error CANNOT_MODIFY_LINKED_TODOS = new Error(32); // storetodos.wcap failed. The todo to modify was a linked todo
	public static final Error CANNOT_SENT_EMAIL = new Error(33);  // Email notification failed. Usually caused by the server not being properly configured to send email. This can occur in storeevents, storetodos, deleteevents_by_id, deletetodos_by_id.
	public static final Error CALENDAR_DISABLED = new Error(34);  // The calendar is disabled in the database. 
	public static final Error WRITE_IMPORT_FAILED = new Error(35); // Import failed when writing files to the server. 
	public static final Error FETCH_BY_LAST_MODIFIED_FAILED = new Error(36);  
	public static final Error CAPI_NOT_SUPPORTED = new Error(37); // Failed trying to read from unsupported format calendar data.
	public static final Error CALID_NOT_SPECIFIED = new Error(38); // Calendar ID was not specified. 
	public static final Error GET_FREEBUSY_FAILED = new Error(39);  
	public static final Error STORE_FAILED_DOUBLE_BOOKED = new Error(40); // If double booking is not allowed in this calendar, storeevents fails with this error when attempting to store an event in a time slot that was already filled.
	public static final Error FETCH_BY_ALARM_RANGE_FAILED = new Error(41); 
	public static final Error FETCH_BY_ATTENDEE_ERROR_FAILED = new Error(42);
	public static final Error ATTENDEE_GROUP_EXPANSION_CLIPPED = new Error(43);	// An LDAP group being expanded was too large and exceeded the maximum number allowed in an expansion. The expansion stopped at the specified maximum limit. 
	public static final Error USERPREFS_ACCESS_DENIED = new Error(44); // Either the server does not allow this administrator access to get or modify user preferences, or the requester is not an administrator. 
	public static final Error NOT_ALLOWED_TO_REQUEST_PUBLISH = new Error(45); //	The requester was not an organizer of the event, and, therefore, is not allowed to edit the component using the PUBLISH or REQUEST method.
	public static final Error INSUFFICIENT_PARAMETERS = new Error(46); // The caller tried to invoke verifyevents_by_ids, or verifytodos_by_ids with insufficient arguments (mismatched number of uid's and rid's).
	public static final Error MUSTBEOWNER_OPERATION = new Error(47); // The user needs to be an owner or co-owner of the calendar in questions to complete this operation. (Probably related to private or confidential component.) 
	public static final Error DWP_CONNECTION_FAILED = new Error(48); // GSE scheduling engine failed to make connection to DWP. 
	public static final Error DWP_MAX_CONNECTION_REACHED = new Error(49); // Reached the maximum number of connections. When some of the connections are freed, users can successfully connect. Same as error 11001.
	public static final Error DWP_CANNOT_RESOLVE_CALENDAR = new Error(50); // Front end can’t resolve to a particular back end. Same as error11002.
	public static final Error DWP_BAD_DATA = new Error(51); // Generic response. Check all DWP servers. One might be down. Same as error 11003.
	public static final Error BAD_COMMAND = new Error(52); // The command sent in was not recognized. This is an Errorernal only error code. It should not appear in the error logs. 
	public static final Error NOT_FOUND = new Error(53); // Returned for all errors from a write to the Berkeley DB. This is an Errorernal only error code. It should not appear in the error logs. 
	public static final Error WRITE_IMPORT_CANT_EXPAND_CALID = new Error(54); // Can’t expand calid when importing file.
	public static final Error GETTIME_FAILED = new Error(55); // Get server time failed. 
	public static final Error FETCH_DELETEDCOMPONENTS_FAILED = new Error(56); // fetch_deletedcomponents failed.
	public static final Error FETCH_DELETEDCOMPONENTS_PARTIAL_RESULT = new Error(57); // Success but partial result. 
	public static final Error WCAP_NO_SUCH_FORMAT = new Error(58); // Returned in any of the commands when supplied fmt-out is not a supported format.
	public static final Error COMPONENT_NOT_FOUND = new Error(59); // Returned when a fetch or delete is attempted that does not exist. 
	public static final Error BAD_ARGUMENTS = new Error(60); // Currently used when attendee or organizer specified does not have a valid email address. 
	public static final Error GET_USERPREFS_FAILED = new Error(61); // get_userprefs.wcap failed. The following error conditions returns error code 61: LDAP access denied| no results found| LDAP limit exceeded| LDAP connection failed
	public static final Error WCAP_MODIFY_NO_EVENT = new Error(62); // storeevents.wcap issued with storeytype set to 2 (WCAP_STORE_TYPE_MODIFY) and the event doesn’t exist.
	public static final Error WCAP_CREATE_EXISTS = new Error(63); // storeevents.wcap issued with storetype set to 1 (WCAP_STORE_TYPE_CREATE) and the event already exists.
	public static final Error WCAP_MODIFY_CANT_MAKE_COPY = new Error(64); //	storevents.wcap issued and copy of event failed during processing.
	public static final Error STORE_FAILED_RECUR_SKIP = new Error(65); // One instance of a recurring event skips over another 
	public static final Error STORE_FAILED_RECUR_SAMEDAY = new Error(66); //	Two instances of a recurring event can’t occur on the same day 
	public static final Error BAD_ORG_ARGUMENTS =	new Error(67); // Bad organizer arguments. orgCalid or orgEmail must be passed if any other org” parameter is sent. That is, orgUID can’t be sent alone on a storeevents or a storetodos command if it is trying about to "create" the event or task. Note, if no “org” information is passed, the organizer defaults to the calid being passed with the command.
	public static final Error STORE_FAILED_RECUR_PRIVACY = new Error(68); // Error returned if you try to change the privacy or transparency of a single instance in a recurring series. 
	public static final Error LDAP_ERROR = new Error(69); // For get_calprops, when there is an error is getting LDAP derived token values (X-S1CS-CALPROPS-FB-INCLUDE, X-S1CS-CALPROPS-COMMON-NAME).
	public static final Error GET_INVITE_COUNT_FAILED = new Error(70); // Error in getting invite count (for get_calprops.wcap and fetchcomponents_by_range.wcap commands)
	public static final Error LIST_FAILED = new Error(71); // list.wcap failed
	public static final Error LIST_SUBSCRIBED_FAILED = new Error(72); // list_subscribed.wcap failed
	public static final Error SUBSCRIBE_FAILED = new Error(73); // subscribe.wcap failed
	public static final Error UNSUBSCRIBE_FAILED = new Error(74); // unsubscribe.wcap failed
	public static final Error ANONYMOUS_NOT_ALLOWED = new Error(75); // Command cannot be executed as anonymous. Used only for list.wcap, list_subscribed.wcap, subscribe.wcap, and unsubscribe.wcap commands.
	public static final Error ACCESS_DENIED = new Error(76); // Generated if a non-administrator user tries to read or set the calendar-owned list or the calendar-subscribed list of some other user, or if the option is not turned on in the server 
	public static final Error BAD_IMPORT_ARGUMENTS = new Error(77); // Incorrect parameter received by import.wcap
	public static final Error READONLY_DATABASE = new Error(78); // Database is in read-only mode. (returned for all attempts to write to the database) 
	public static final Error ATTENDEE_NOT_ALLOWED_TO_REQUEST_ON_MODIFY = new Error(79); // Attendee is not allowed to modify an event with method=request.
	public static final Error TRANSP_RESOURCE_NOT_ALLOWED = new Error(80); // Resources do not permit the transparency parameter.
	public static final Error RECURRING_COMPONENT_NOT_FOUND = new Error(81); 
	
	private int errorCode = 0;
	
	private Error(int errorCode)
	{
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}
}
