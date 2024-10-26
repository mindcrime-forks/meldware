package org.buni.mail.webmail.controller {

    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.commands.ICommand;        
    import com.adobe.cairngorm.commands.SequenceCommand;
    import com.adobe.cairngorm.control.CairngormEvent;
    import mx.rpc.IResponder;
    
    import org.buni.mail.webmail.model.ModelLocator;
    import org.buni.mail.webmail.model.AddressBookEntryVO;
    
    public class SearchABCommand extends SequenceCommand implements ICommand, IResponder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public override function execute(event:CairngormEvent):void {
            trace("in SearchABCommand execute");
           // var edata:Object = event.data as Object;
            var delegate : AddressBookDelegate = new AddressBookDelegate( this ); 
            delegate.searchAddressBook( event.data.name, event.data.email );          
            trace("post delegate.searchAddressBook()");
        }
           
        public function result(event:Object) : void {    
        	trace("got searchab result");
        	
        	var addresses:ArrayCollection;
        	if(!event.result.hasOwnProperty("address")) {
        		addresses = new ArrayCollection();
        	} else if (event.result.address is ArrayCollection) {
        		addresses = event.result.address as ArrayCollection;
        	} else if (event.result.address != null ) {
        		addresses = new ArrayCollection();
        		addresses.addItem(event.result.address);
        	} 
        	
        	model.searched.removeAll();
        	
        	for(var i:int = 0; addresses != null && i < addresses.length; i++) {
        		var entry:AddressBookEntryVO = new AddressBookEntryVO(addresses.getItemAt(i));
        		model.searched.addItem(entry);
        	}
        	          
            executeNextCommand();
       }
                
       public function fault(event:Object) : void {
           trace('test');
           // model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
