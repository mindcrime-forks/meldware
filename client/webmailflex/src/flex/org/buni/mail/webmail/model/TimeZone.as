package org.buni.mail.webmail.model
{
  [Bindable]
  public class TimeZone
  {

    public var offset:Number;
    public var name:String;

    public function TimeZone(offset:Number, name:String)
    {
      this.offset = offset;
      this.name = name;
    }

    public function toString():String
    {
      return name;
    }

  }
}
