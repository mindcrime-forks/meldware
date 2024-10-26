package org.buni.mail.webmail.view.renderer {
    import mx.controls.*;
    import mx.core.*;
    import mx.controls.dataGridClasses.DataGridListData;

    public class DGItemRenderer extends TextInput
    {
        // Define the constructor and set properties.
        public function DGItemRenderer() {
            super();
         //   height=30;
         //   width=40;
            setStyle("borderStyle", "none");
            editable=false;
        }

        // Override the set method for the data property.
        override public function set data(value:Object):void {
            super.text = "";
       
            if (value != null)
            {
                var val:Boolean = value[DataGridListData(listData).dataField];
                trace("custom renderer val is "+val);
                if(val)
                {
                	setStyle("backgroundColor", null);
                } else {
                    setStyle("backgroundColor", 0xFF0000);
                }
            }

            super.invalidateDisplayList();
        }
    }
}
