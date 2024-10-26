package org.buni.meldware.admin.model
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