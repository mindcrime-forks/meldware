package org.buni.mail.webmail.view.renderer
{
  import com.adobe.cairngorm.control.CairngormEvent;
  
  import flash.events.Event;
  import flash.events.FocusEvent;
  import flash.events.KeyboardEvent;
  import flash.events.MouseEvent;
  import flash.events.TextEvent;
  import flash.geom.Point;
  import flash.ui.Keyboard;
  
  import mx.collections.ArrayCollection;
  import mx.containers.Box;
  import mx.controls.Button;
  import mx.controls.ComboBox;
  import mx.controls.List;
  import mx.controls.TextInput;
  import mx.core.IFactory;
  import mx.events.FlexMouseEvent;
  import mx.events.ListEvent;
  import mx.managers.IFocusManagerComponent;
  import mx.managers.PopUpManager;
  import mx.skins.halo.ComboBoxArrowSkin;
  import mx.states.SetStyle;
  
  import org.buni.mail.webmail.model.ModelLocator;

[Event(name="typedTextChange", type="flash.events.Event")]

  public class AutoCompleteEditor extends Box implements IFactory, IFocusManagerComponent
  {
	/**
	 *  @private
	 */
	private var typedTextChanged:Boolean;

	[Bindable("typedTextChanged")]
	[Inspectable(category="Data")]  	
  	
  	private var _textInput:TextInput;
    private var _list:List;
    
    private var _data:Object;
    
    private var _labelField:String;
    private var _suggestionDataProvider:Object;
    
    private var down:Boolean;

    private var ignoreFocusOut:Boolean = false;
    
    public function AutoCompleteEditor()
    {
      super();
      horizontalScrollPolicy = "off";
      verticalScrollPolicy = "off";
      this._suggestionDataProvider = ModelLocator.getInstance().suggestions;
    }
    
    public function newInstance():* {
    	var ace:AutoCompleteEditor = new AutoCompleteEditor();
    	ace.suggestionDataProvider = this.suggestionDataProvider;
    	ace.labelField = this.labelField;
    	ace.enabled = true;
    	return ace;
    }
    
    public function set suggestionDataProvider(object:Object):void {
    	this._suggestionDataProvider = object;
    }
    
    public function get suggestionDataProvider():Object {
    	return this._suggestionDataProvider;
    }
    
    public function set labelField(name:String):void {
    	this._labelField = name;
    }
    
    public function get labelField():String {
    	return this._labelField;
    }
    
    private function closePopUp(event:MouseEvent=null):void
    {
      PopUpManager.removePopUp(_list);
      _list = null;
    }

    private function handleTextInput(event:Event):void
    { 	
      if (_list != null) {
      	closePopUp();
      }
      this._suggestionDataProvider = ModelLocator.getInstance().suggestions;

      _list = new List();
      _list.selectedItem = _textInput.text;
      _list.labelField = this.labelField;
      _list.setStyle("backgroundColor", 0xe7e7e7);
      _list.minWidth = _textInput.width + 50;
      _list.dataProvider = this.suggestionDataProvider;
      
      _list.addEventListener(ListEvent.ITEM_CLICK, itemSelected);
      _list.addEventListener(FlexMouseEvent.MOUSE_DOWN_OUTSIDE, closePopUp);
      _list.addEventListener(FlexMouseEvent.MOUSE_WHEEL_OUTSIDE, closePopUp);
      _list.addEventListener(KeyboardEvent.KEY_UP, handleKeyEventsForList);
  
      PopUpManager.addPopUp(_list, this);
      var p:Point = localToGlobal(new Point());
      _list.y = p.y + this.height;
      _list.x = p.x;
      
      	var e:CairngormEvent = new CairngormEvent("typedTextChanged");
		e.data = _textInput.text;
		dispatchEvent(e);
    }
    
    private function itemSelected(event:ListEvent):void
    {
    	trace("itemSelected");
    	if (down == false) {
	      _textInput.text = _list.selectedItem.toString();
	      if (_textInput.text != null && this._data != null) {
	          _data.fullAddress = _textInput.text;
	          _textInput.text = _data.fullAddress;
	      }
	          _textInput.setSelection(_textInput.text.length,_textInput.text.length);
	      closePopUp();
        } else {
        	down = false;
        }
    }
    
    public function get text():String
    {
    	trace("get text");
      return _textInput.text;
    }
    
    public function set text(text:String):void
    {
    	
    	trace("set text");
      _textInput.text = text;
    }

    public override function set data(d:Object):void
    {
    	trace("set data");
      if (d != null) {
      	trace("data !	= null");
        this._data = d;
        _textInput.text = d.fullAddress as String;

      }
    }
    
    public function get selectedItem():Object {
    	trace("get selectedItem");
        return _list.selectedItem;
    }
    
    public function set selectedItem(item:Object):void {
    	trace("setSelectedItem");
        _list.selectedItem = item;
        if (down == false) {
        	_textInput.text = item.toString();
     
            _data.fullAddress = _textInput.text;
        }
    }
    
    public override function get data():Object
    {
      return _data;
    }
    
    protected override function createChildren():void
    {
    	trace("createChildren");
      if (_textInput == null)
      {
        _textInput = new TextInput();
        _textInput.minWidth = 300;
        _textInput.width=300;
        _textInput.editable = true;
        _textInput.enabled = true;
   /*     _textInput.setStyle("disabledSkin", ComboBoxArrowSkin);
        _textInput.setStyle("editableUpSkin", ComboBoxArrowSkin);
        _textInput.setStyle("editableDownSkin", ComboBoxArrowSkin);
        _textInput.setStyle("editableOverSkin", ComboBoxArrowSkin);
        _textInput.setStyle("editableDisabledSkin", ComboBoxArrowSkin);
        _textInput.setStyle("downSkin", ComboBoxArrowSkin);
        _textInput.setStyle("upSkin", ComboBoxArrowSkin);
        _textInput.setStyle("overSkin", ComboBoxArrowSkin);*/
        _textInput.setStyle("paddingRight", 20);
        _textInput.setStyle("textAlign", "left");
        _textInput.addEventListener(TextEvent.TEXT_INPUT, handleTextInput);
        _textInput.addEventListener(KeyboardEvent.KEY_DOWN, handleKeyEvents);
        addChild(_textInput);
        _textInput.setFocus();
      }
    }
    
    protected function handleKeyEventsForList(event:KeyboardEvent):void {
    	if(event.keyCode == Keyboard.ENTER && _list != null) {
    		down = false;
    		trace("list key enter");
    		this.itemSelected(null);  		
	        _textInput.setFocus();
    	} else if (event.keyCode == Keyboard.ESCAPE && _list != null) {
    		trace("list key escape");
    		_list.enabled = false;
	        //_textInput.setFocus();
    		closePopUp();
    	}

        /*
        if ( ((event.keyCode == Keyboard.ENTER) || (event.keyCode == Keyboard.BACKSPACE) || (event.keyCode == Keyboard.ESCAPE) || (event.keyCode == Keyboard.DOWN) || (event.keyCode == Keyboard.UP) || (event.keyCode == Keyboard.TAB)) && (this._data != null)) {
    		this._data.fullAddress = _textInput.text;
    		this._textInput.text = this._data.fullAddress;
    	}
        */
    }
    
    protected function handleKeyEvents(event:KeyboardEvent):void {
    	if(event.keyCode == Keyboard.DOWN && _list != null && _list.selectedIndex <0) {
    		trace("key down");
            ignoreFocusOut = true;
    		this._list.setFocus();
    		if (suggestionDataProvider.length > 0){
				_list.selectedIndex = 0;
    		}
            event.stopImmediatePropagation();
            event.preventDefault();
    	} else if(event.keyCode == Keyboard.ENTER ) {
    		trace("key enter");
    		if (_list != null) {
    			_list.enabled = false;
    		}
    		this.down = true;
    		if (_list != null) {
    			closePopUp();
    		}
    		this.down = false;
    	} else if (event.keyCode == Keyboard.ESCAPE && _list != null) {
    		trace("key escape");
    		_list.enabled = false;
    		closePopUp();
            _textInput.setFocus();
            event.stopImmediatePropagation();
    	}

    }
    
    protected function handleMouseEvents(event:MouseEvent):void {
    	this.down = false;
    }
    
    override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
    {
      super.updateDisplayList(unscaledWidth, unscaledHeight);
      
    }

    // if you get focus, select the text
    override public function setFocus():void
    {
      _textInput.setSelection(0, _textInput.text.length);
      _textInput.setFocus();
    }

    // make sure the drop down goes away if focus leaves
    override protected function focusOutHandler(event:FocusEvent):void
    {
      if (!ignoreFocusOut && data != null && _textInput != null)
      {
        _data.fullAddress = _textInput.text;

        super.focusOutHandler(event);

        if (_list != null)
        {
          _list.enabled = false;
          closePopUp();
        }

      }
      else
      {
        ignoreFocusOut = false;
      }

    }
  }
}
