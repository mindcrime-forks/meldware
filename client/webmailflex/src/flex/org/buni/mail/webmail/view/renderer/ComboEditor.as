package org.buni.mail.webmail.view.renderer
{
	import mx.controls.ComboBox;
	import org.buni.mail.webmail.model.ModelLocator;
	import flash.events.KeyboardEvent;
	import flash.events.TextEvent;
	


  public class ComboEditor extends ComboBox
  {
	
    
    public function ComboEditor()
    {
      super();
      this.editable = true;
      this.dataProvider = ModelLocator.getInstance().suggestions;
      this.addEventListener(KeyboardEvent.KEY_UP,openCombo);
    }
    private function openCombo(event:KeyboardEvent) {
    	if (this.textInput.length > 0) {
    		this.dropdown.dataProvider = this.dataProvider;
    		this.open();
    	} else {
    		this.close()
    	}
    }

    
  }
}