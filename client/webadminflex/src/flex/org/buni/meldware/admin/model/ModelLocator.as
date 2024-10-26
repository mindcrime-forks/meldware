package org.buni.meldware.admin.model {
  import mx.collections.ArrayCollection;
  import mx.collections.ListCollectionView;
  
  import com.adobe.cairngorm.model.ModelLocator;
  import mx.events.CollectionEvent;
  import flash.events.Event;
  
  [Bindable]
  public class ModelLocator implements com.adobe.cairngorm.model.ModelLocator {    

    private static var modelLocator:org.buni.meldware.admin.model.ModelLocator;
    public var mailLists:ArrayCollection;
    public var mailbodyManagers:ArrayCollection; 
    public var mailboxManagers:ArrayCollection;    
    public var mailListenerChains:ArrayCollection;
    public var domainGroups:ArrayCollection;
    public var localDomainGroups:ArrayCollection;
    public var userRepositories:ArrayCollection;
    public var ssldomains:ArrayCollection;
    public var postmaster:String;
    public var localdomains:ArrayCollection;
    public var protocols:ArrayCollection;
    public var threadpools:ArrayCollection;
    public var services:ArrayCollection;
    private var lastFailure:String;
    public var lastStatus:StatusVO;
    public var selectedUser:UserVO;
    public var selectedService:ServiceVO;
    public var selectedProtocol:ProtocolVO;
    public var selectedThreadPool:ThreadPoolVO;
    public var selectedMailList:MailListVO;
    private var _user:UserVO;
    private var _users:ArrayCollection;
    private var _systemABMounts:ArrayCollection;

    public static function getInstance():org.buni.meldware.admin.model.ModelLocator {
      if ( modelLocator == null )
        modelLocator = new org.buni.meldware.admin.model.ModelLocator();
        
      return modelLocator;
    }
     
    public function ModelLocator() {
      if ( org.buni.meldware.admin.model.ModelLocator.modelLocator != null )
        throw new Error( "Only one ModelLocator instance should be instantiated" );  

    }

    [Bindable(event="userChanged")]
    public function get user():UserVO {
      return _user; 
    }

    public function set user(_user:UserVO):void {
      this._user = _user;
      dispatchEvent(new Event("userChanged"));
    }

    public function get users():ArrayCollection {
    	return this._users;
    }
    
    public function set users(users:ArrayCollection):void {
    	this._users = users;
    }

    public function getServices():ArrayCollection {
      return this.services;
    }

    public function setServices(services:ArrayCollection):void {
          this.services = services;
    }

    public function setLastFailure(lastFailure:String):void {
      this.lastFailure = lastFailure;
    }

    public function getLastFailure():String {
      return this.lastFailure;
    }
    
    public function get systemABMounts():ArrayCollection {
    	return this._systemABMounts;
    }
    
    public function set systemABMounts(mounts:ArrayCollection):void {
    	this._systemABMounts = mounts;
    }
    
  }
  
}
