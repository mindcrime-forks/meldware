package org.buni.mail.webmail.util
{
import mx.collections.ArrayCollection;
import mx.formatters.DateFormatter;

public class LabelUtil
{
        
    /*
     * 
     */
    /**
     * 
     * @param o Takes either a single email or an array of emails
     * @return String listing the emails
     * 
     */
    static public function getEmail(o:*):String
    {
        if (o is ArrayCollection)
        {
            var s:String = "";
            for (var i:int = 0; i < o.length; i++)
            {
                s += getEmail(o[i]) + ", ";
            }
            return s.substr(0,s.length - 2);
        }
        else if (o is Object)
        {
            var ss:String = "";
            if (o.name != null)
            {
                ss = o.name + " <" + o.address + ">";
            }
            else
            {
                ss = o.address;
            }
             
            return ss;
        }
        else
        {
            return "";
        }
    }
        
    /**
     * This is our global date Formating mechanism
     * @param d date to format
     * @return formatted date string
     * 
     */
    static public function getTimeString(d:Date):String
    {
        var s:String = new String();
        if (d.getHours() == 0)
        {
            s += "12";
        }
        else if (d.getHours() > 12)
        {
            s += d.getHours() - 12;
        }
        else
        {
            s += d.getHours();
        }
        s += ":";
        if (d.getMinutes() < 10)
        {
            s += "0" + d.getMinutes();
        }
        else
        {
            s += d.getMinutes();
        }
        if (d.getHours() < 12)
        {
            s += " am";
        }
        else
        {
            s += " pm";
        }
        return s;
    }

    /*
     * 
     */
    static public function getAttachment(o:*):String
    {
        if (o is ArrayCollection)
        {
            var s:String = "";
            for (var i:int = 0; i < o.length; i++)
            {
                s += getAttachment(o[i]) + "  ";
            }
            return s.substr(0,s.length - 2) ;
        }
        else if (o is Object)
        {
            return o.filename;
        }
        else
        {
            return "";
        }
    }

    static public function getFormattedDate(d:Date):String
    {
        var formatter:DateFormatter = new DateFormatter();

        var today:Date = new Date();

        if (d.getDate() == today.getDate() && d.getFullYear() == today.getFullYear() && d.getMonth() == today.getMonth())
        {
            formatter.formatString = "L:NN A";
            return formatter.format(d);
        }
            
        var yesterday:Date = new Date(today.getTime() - (24 * 60 * 60 * 1000));
        if (d.getDate() == yesterday.getDate() && d.getFullYear() == yesterday.getFullYear() && d.getMonth() == yesterday.getMonth())
        {
            formatter.formatString = "L:NN A";
            return "Yesterday " + formatter.format(d);
        }

        var weekAgo:Date = new Date(today.getTime() - (7 * 24 * 60 * 60 * 1000));
        weekAgo.setHours(0);
        weekAgo.setMinutes(0);
        weekAgo.setSeconds(0);
        weekAgo.setMilliseconds(0);
        if (d.getTime() > weekAgo.getTime())
        {
            formatter.formatString = "EEE L:NN A";
            return formatter.format(d);
        }

        var yearStart:Date = new Date();
        yearStart.setMonth(1);
        yearStart.setDate(1);
        yearStart.setHours(0);
        yearStart.setMinutes(0);
        yearStart.setSeconds(0);
        yearStart.setMilliseconds(0);
        if (d.getTime() > yearStart.getTime())
        {
            formatter.formatString = "MMM D YYYY";
            return formatter.format(d);
        }

        return d.toString();
    }
}
}
