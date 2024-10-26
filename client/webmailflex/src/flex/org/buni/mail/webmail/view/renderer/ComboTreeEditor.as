package org.buni.mail.webmail.view.renderer
{
  import mx.controls.Button;
  import flash.events.MouseEvent;
  import mx.managers.PopUpManager;
  import mx.containers.Box;
  import mx.controls.Tree;
  import flash.geom.Point;
  import mx.events.FlexMouseEvent;
  import flash.events.Event;
  import mx.events.ListEvent;
  import mx.skins.halo.ComboBoxArrowSkin;
  import mx.states.SetStyle;
  import org.buni.mail.webmail.model.ModelLocator;

  public class ComboTreeEditor extends Box
  {
    private var _button:Button;
    private var _tree:Tree;
    
    private var _text:String;
    private var _data:Object;
    
    public function ComboTreeEditor()
    {
      super();
      horizontalScrollPolicy = "off";
      verticalScrollPolicy = "off";
    }
    
    private function closePopUp(event:MouseEvent=null):void
    {
      PopUpManager.removePopUp(_tree);
      _tree = null;
    }

    private function handleArrowClick(event:Event):void
    {
      _tree = new Tree();
      _tree.selectedItem = _text;
      _tree.labelField = "folderName";
      _tree.setStyle("backgroundColor", 0xe7e7e7);
      _tree.minWidth = _button.width + 50;
      _tree.showRoot = false;
      _tree.dataProvider = ModelLocator.getInstance().folders;
      _tree.openItems = _tree.dataProvider;
      _tree.horizontalScrollPolicy = "on";
      _tree.maxHorizontalScrollPosition = 10;
      _tree.addEventListener(ListEvent.CHANGE, itemSelected);
      _tree.addEventListener(FlexMouseEvent.MOUSE_DOWN_OUTSIDE, closePopUp);
      _tree.addEventListener(FlexMouseEvent.MOUSE_WHEEL_OUTSIDE, closePopUp);
  
      PopUpManager.addPopUp(_tree, this);
      var p:Point = localToGlobal(new Point());
      _tree.y = p.y + this.height;
      _tree.x = p.x;
    }
    
    private function itemSelected(event:ListEvent):void
    {
      text = _tree.selectedItem.toString();
      if (text != null && this._data != null) {
          this._data.folderName = text;
      }
      closePopUp();
    }
    
    public function get text():String
    {
      return _text;
    }
    
    public function set text(text:String):void
    {
      _text = text;
      _button.label = text;
    }

    public override function set data(data:Object):void
    {
      if (data != null) {
        this._data = data;
        text = data.folderName;
      }
    }
    
    public function get selectedItem():Object {
        return _tree.selectedItem;
    }
    
    public function set selectedItem(item:Object):void {
        _tree.selectedItem = item;
    }
    
    public override function get data():Object
    {
      if (_tree == null) {
          return null;
      }
      return _tree.selectedItem;
    }
    
    protected override function createChildren():void
    {
      if (_button == null)
      {
        _button = new Button();
        _button.label = text;
        _button.width = this.width;
        _button.setStyle("disabledSkin", ComboBoxArrowSkin);
        _button.setStyle("editableUpSkin", ComboBoxArrowSkin);
        _button.setStyle("editableDownSkin", ComboBoxArrowSkin);
        _button.setStyle("editableOverSkin", ComboBoxArrowSkin);
        _button.setStyle("editableDisabledSkin", ComboBoxArrowSkin);
        _button.setStyle("downSkin", ComboBoxArrowSkin);
        _button.setStyle("upSkin", ComboBoxArrowSkin);
        _button.setStyle("overSkin", ComboBoxArrowSkin);
        _button.setStyle("paddingRight", 20);
        _button.setStyle("textAlign", "left");
        _button.addEventListener(MouseEvent.CLICK, handleArrowClick);
        addChild(_button);
      }
    }
    
    protected override function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
    {
      super.updateDisplayList(unscaledWidth, unscaledHeight);
      
      _button.width = unscaledWidth - 5;
    }
  }
}