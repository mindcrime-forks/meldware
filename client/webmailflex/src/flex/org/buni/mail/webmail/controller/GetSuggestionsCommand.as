package org.buni.mail.webmail.controller {

    import mx.rpc.IResponder;
    import mx.rpc.events.ResultEvent;

    import mx.collections.ArrayCollection;
    import com.adobe.cairngorm.commands.ICommand;        
    import com.adobe.cairngorm.commands.SequenceCommand;
    import com.adobe.cairngorm.control.CairngormEventDispatcher;
    import com.adobe.cairngorm.control.CairngormEvent;
    import org.buni.mail.webmail.controller.AddressBookDelegate;
    import org.buni.mail.webmail.model.ModelLocator;
    import org.buni.mail.webmail.model.SystemABMountVO;
    
    public class GetSuggestionsCommand extends SequenceCommand implements ICommand, IResponder {

        private var model : ModelLocator = ModelLocator.getInstance();
       
        public override function execute(event:CairngormEvent):void {
            trace("in GetSuggestionsCommand execute");
            
            var delegate : AddressBookDelegate = new AddressBookDelegate( this );   
            if (event.hasOwnProperty("data")) {
            	trace("actually getting suggestions");
            	delegate.getSuggestions( event.data as String );    
            }      
            trace("post delegate.getSuggestions()");
        }
           
        public function result(event:Object):void {    
        	trace("GetSuggestions onResult");         
            var model:ModelLocator = ModelLocator.getInstance();
                 

                  var suggestions:ArrayCollection;
                  if (!event.result.hasOwnProperty("suggestion")) {
                  	    suggestions = new ArrayCollection();
                  } else if (event.result.suggestion is ArrayCollection) {
                  	    trace("suggestions is Arraycollection");
                        suggestions = event.result.suggestion;
                  } else if (event.result.suggestion != null) {
                  	    trace("Suggestions is not array collection but is not null");
                        suggestions = new ArrayCollection();
                        suggestions.addItem(event.result.suggestion);
                  }

                  if (suggestions != null && suggestions.length > 0) {
                      trace("suggestions was not null");
                      trace("suggestions length ");
                      trace(suggestions.length);
                      model.suggestions.removeAll();
                      for (var i:int = 0; i < suggestions.length; i++) {
                          var suggestion:String = suggestions[i];
                          model.suggestions.addItem(suggestion);
                      }
                  } else {
                      trace("suggestions was null");
                  }
              executeNextCommand();
                  

       }
                
       public function fault(event:Object):void {
           // model.setLastFailure("The server did not like your tone or some error occurred.");
        }
    }
}
