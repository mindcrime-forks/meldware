package org.buni.mail.webmail.util
{

public class EmailFormatter
{
  
  /**
   * formats an string by indenting the lines to create a typical "reply to" email
   * @param body
   * @return 
   * 
   */
  static public function getEmailReplyText(body:String):String
  {
    var reply:String = new String();

    if (body == null)
    {
        return "";
    }

    var a:Array = body.split(/\r/);

    for (var j:int = 0; j < a.length; j++)
    {

      var lines:Array = new Array();

      if (a[j].length > 78)
      {

        var ss:Array = a[j].split(" ");
        var currentLine:int = 1;

        while (ss.length > 0)
        {
          // create the new line if needed
          if (currentLine > lines.length)
          {
            lines.push(new String());
          }

          // new line for strings over 78 chars
          if (ss[0].length > 78)
          {
            if (lines[currentLine - 1].length > 0)
            {
              currentLine++;
              lines.push(new String());
            }
            lines[currentLine - 1] = ss.shift();
            currentLine++;
            continue;
          }

          if ((lines[currentLine - 1].length + ss[0].length) < 78)
          {
            lines[currentLine - 1] += ss.shift() + " ";
          }
          else
          {
            currentLine++;
          }
        }
      }
      else
      {
        lines.push(a[j]);
      }

      for each (var l:String in lines)
      {
        reply += "\n> " + l;
      }

    }

    return reply;
  }
}
}
