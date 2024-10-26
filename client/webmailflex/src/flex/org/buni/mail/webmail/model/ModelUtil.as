package org.buni.mail.webmail.model
{
	import mx.utils.ObjectUtil;
	
	public class ModelUtil {
		public static function nvl(o:Object, prop:String) {
			if (o.hasOwnProperty(prop)) {
				return o[prop];
			} else {
				return null;
			}
		}
	}
}