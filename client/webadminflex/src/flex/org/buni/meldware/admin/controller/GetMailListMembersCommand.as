package org.buni.meldware.admin.controller {

    import mx.rpc.events.ResultEvent;
    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.business.Responder;
    import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.commands.SequenceCommand;
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.adobe.cairngorm.control.CairngormEventDispatcher;
    import org.buni.meldware.admin.controller.ServiceDelegate;
    import org.buni.meldware.admin.model.ModelLocator;
    import org.buni.meldware.admin.model.MailListMemberVO;
  	import mx.utils.ObjectUtil;
    
    public class GetMailListMembersCommand extends SequenceCommand implements Command, Responder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        override public function execute( event : CairngormEvent ) : void {
            trace("in GetMailListMembersCommand execute");
            
            var delegate : MailListDelegate = new MailListDelegate( this );   
            model.selectedMailList = event.data.list;
            delegate.getMailListMembers( event.data.list, event.data.pattern );          
        }
           
        public function onResult( event : * = null ) : void {            
            var model:ModelLocator = ModelLocator.getInstance();
                  model.selectedMailList.members = new ArrayCollection();

                  var members:ArrayCollection;
                  trace(ObjectUtil.toString(event.result));

                  if (!event.result.hasOwnProperty("member")) {
                  	    members = null;
                  } else if (event.result.member is ArrayCollection) {
                        members = event.result.member;
                  } else if (event.result.member != null) {
                        members = new ArrayCollection();
                        members.addItem(new MailListMemberVO(event.result.member));
                  }

                  if (members != null) {
                      trace("members was not null");
                      trace("members length ");
                      trace(members.length);
                      for (var i:int = 0; i < members.length; i++) {
                          model.selectedMailList.members.addItem(new MailListMemberVO(members[i]));
                      }
                  } else {
                      trace("members was null");
                  }
              var e:CairngormEvent = new CairngormEvent(AdminController.GOT_MAILLIST_MEMBERS);
              CairngormEventDispatcher.getInstance().dispatchEvent(e); 
              executeNextCommand();
       }
                
       public function onFault( event : * = null ) : void {
            model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
