package org.buni.mail.webmail.model
{
      import com.adobe.cairngorm.vo.ValueObject;

      [Bindable]
      public class AttachmentVO implements ValueObject
      {
              public var path : String;
              public var size : Number;
              public var filename : String;
              public var url : String;
              public var id : String;


              public function AttachmentVO(o:*)
              {
              	trace("attachment vo constructed");
              	if (o != null) {
                      path = o.path;
                      size = o.size;
                      filename = o.filename;
                      url = o.url;
                      id = o.id;
              	}
                      trace("attachment url is "+url);
              }
      }
}
