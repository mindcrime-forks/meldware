package org.buni.mail.webmail.view
{
	import mx.core.IContainer;

	public interface ITabView extends IContainer
	{
		
		function set data(data:Object):void;
		
		function get data():Object;
		
	}
}