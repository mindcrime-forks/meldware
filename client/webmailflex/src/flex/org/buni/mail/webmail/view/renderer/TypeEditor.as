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
  
  /**
  * This big hairy hack deals with the inherent suckiness of the ugly beast ComboBox + DataGrid in Flex.
  * The problem is that if you want to use the ComboBox as an editor and you want to use say an array or something
  * as a dataprovider and you're only editing one field...you need to specify both the data provider datafield and the
  * datafield for the edited.  The silly combobox and the datagrid's deficient error reporting of type mismatches between
  * edited and editor means you always get 0 for our To/cc boxes.  Moreover without the actual datafield being passed from
  * the datagrid (which seems not to happen) the combo is at a bit of a loss.
  * 
  * example:
  *
  * public static var types: Array = [ {label:"To:", data:0}, {label:"Cc:", data:1}, {label:"Bcc:", data:2} ];
  * &lt;mx:DataGridColumn width="80" editable="true" rendererIsEditor="true"  dataField="type" &gt;
  * &lt;mx:itemRenderer&gt;
  * &lt;mx:Component&gt;
  * &lt;r:TypeEditor labelField="label" dataProvider="{EmailComposeView.types}" dataField="type" dataProviderDataField="data"/&gt;
  * &lt;/mx:Component&gt;
  * &lt;/mx:itemRenderer&gt;
  * &lt;/mx:DataGridColumn&gt;
  * 
  * @author Andrew C. Oliver &lt;acoliver ot buni dat org&gt;
  */
  public class TypeEditor extends Box implements IFactory, IFocusManagerComponent
  {
	[Inspectable(category="Data")]  	
  	
  	private var _comboBox:ComboBox;
    
    private var _data:Object;
    
    private var _labelField:String;
    private var _suggestionDataProvider:Object;
    
    private var down:Boolean;

    private var ignoreFocusOut:Boolean = false;

    private var _dataProvider:Object;
    
    private var _dataField:String;
    
    private var _dataProviderDataField:String;
    
    public function TypeEditor()
    {
      super();
      horizontalScrollPolicy = "off";
      verticalScrollPolicy = "off";
    }
    
    public function newInstance():* {
    	var te:TypeEditor = new TypeEditor();
    	te.dataProvider = this.dataProvider;
    	te.labelField = this.labelField;
    	te.enabled = true;
    	return te;
    }
   
    public override function set width(value:Number):void {
    	super.width = value;
    	if(_comboBox != null) {
    		_comboBox.width = value;
    	}
    }
    
    public function set dataProvider(object:Object):void {
    		this._dataProvider=object;
    		if (_comboBox != null) {
    			_comboBox.dataProvider = _dataProvider;
    		}
    }
    
    public function get dataProvider():Object {
    	return this._dataProvider;
    }
    
    public function get dataProviderDataField():String {
    	return _dataProviderDataField;
    }
    
    public function set dataProviderDataField(field:String):void {
    	this._dataProviderDataField = field;
    }
    
    public function set labelField(name:String):void {
    	this._labelField = name;
    	if (_comboBox != null ) {
    		_comboBox.labelField = name;
    	}
    }
    
    public function get labelField():String {
    	return this._labelField;
    }
    
    public function set dataField(name:String):void {
    	this._dataField = name;
    }
    
    public function get dataField():String {
    	return this._dataField;
    }

    public override function set data(d:Object):void
    {
    	trace("set data");
      if (d != null) {
      	trace("data !	= null");
        this._data = d;
        _comboBox.selectedItem = d[_dataField];
      }
    }
    
    public function get text():String {
    	trace("get text "+_comboBox.text);
    	if (this._dataProviderDataField != null ) {
    		return _comboBox.selectedItem[_dataProviderDataField];
    	}
    	return _comboBox.text;
    }
    
    public function set text(text:String) {
    	trace("set text "+text);
    	this._comboBox.text = text;
    }
    
    public function get selectedItem():Object {
    	trace("get selectedItem");
        return _comboBox.selectedItem;
    }
    
    public function set selectedItem(item:Object):void {
    	trace("setSelectedItem");
        _comboBox.selectedItem = item;
    }
    
    public override function get data():Object
    {
    	trace("get data "+_data);
      return _data;
    }
    
    protected override function createChildren():void
    {
    	trace("createChildren");
      if (_comboBox == null)
      {
      	_comboBox = new ComboBox();
      	_comboBox.dataProvider = _dataProvider;
      	if (this.width != 0) {
      		_comboBox.width = width;
      	}
      	_comboBox.addEventListener(ListEvent.CHANGE, handleChange);
      	_comboBox.addEventListener(ListEvent.ITEM_CLICK, handleChange);
      	_comboBox.labelField = _labelField;
        _comboBox.enabled = true;
        addChild(_comboBox);
       // _comboBox.setFocus();
      }
    }
    
    public function handleChange(event:Event) {
    	this._data[_dataField] = _dataProviderDataField != null ? _comboBox.selectedItem[_dataProviderDataField] : _comboBox.selectedItem;
    }
    
    override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
    {
      super.updateDisplayList(unscaledWidth, unscaledHeight);
      
    }

    override public function setFocus():void
    {
      _comboBox.setFocus();
    }

  }
}