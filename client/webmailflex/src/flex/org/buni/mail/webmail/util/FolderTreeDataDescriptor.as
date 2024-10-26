////////////////////////////////////////////////////////////////////////////////
//
//  Copyright (C) 2003-2006 Adobe Macromedia Software LLC and its licensors.
//  All Rights Reserved. The following is Source Code and is subject to all
//  restrictions on such code as contained in the End User License Agreement
//  accompanying this product. If you have received this file from a source
//  other than Adobe, then your use, modification, or distribution of this file
//  requires the prior written permission of Adobe.
//
////////////////////////////////////////////////////////////////////////////////

package org.buni.mail.webmail.util
{

import mx.collections.ArrayCollection;
import mx.collections.CursorBookmark;
import mx.collections.ICollectionView;
import mx.collections.IViewCursor;
import mx.collections.XMLListCollection;
import mx.controls.menuClasses.IMenuDataDescriptor;
import mx.events.CollectionEvent;
import mx.controls.treeClasses.ITreeDataDescriptor;
import mx.collections.IList;
import mx.events.CollectionEventKind;

public class FolderTreeDataDescriptor implements ITreeDataDescriptor, IMenuDataDescriptor
{

	public function FolderTreeDataDescriptor()
	{
		super();
	}

	public function getChildren(node:Object, model:Object=null):ICollectionView
	{
		//null items never have children
		if (node == null)
			return null;
			
		var children:*;
		//first get the children based on the type of node. 
		if (node is XML)
		{
			children = node.*;
		}
		else if (node is ICollectionView ||
				 node is IList ||
				 node is Array)
		{
			//until we guarantee that containers wont be passed to this class
			// we'll just assume that these collections are already a collection of children
			children = node;
		}
		else if (node is Object)
		{
			//we'll try the default children property
			try
			{
				children = node.children;
			}
			catch (e:Error)
			{
			}
		}
		//then wrap children in ICollectionView if necessary 
		if (children is ICollectionView)
		{
			return ICollectionView(children);
		}
		else if (children is Array)
		{
			return new ArrayCollection(children);
		}
		else if (children is XMLList)
		{
			return new XMLListCollection(children);
		}
		else
		{
			var childArray:Array = new Array(children);
			if (childArray != null)
			{
				return new ArrayCollection(childArray);
			}
		}
		return null;
	}
	
	public function hasChildren(node:Object, model:Object=null):Boolean
	{
		if (node == null)
			return false;
			
		//is it a branch?
		if (isBranch(node, model))
		{
			//get children
			var children:ICollectionView = getChildren(node, model);
			try 
			{
				if (children.length > 0)
					return true;
			}
			catch (e:Error)
			{
			}
		}
		return false;
	}

	public function isBranch(node:Object, model:Object=null):Boolean
	{
		if (node == null)
			return false;
			
		var branch:Boolean;
			
		if (node is XML)
		{
			var childList:XMLList = node.children();
			//accessing non-required e4x attributes is quirky
			//but we know we'll at least get an XMLList
			var branchFlag:XMLList = node.@isBranch;
			//check to see if a flag has been set
			if (branchFlag.length() == 1)
			{
				//check flag and return (this flag overrides termination status)
				if (branchFlag[0] == "true")
			    	branch = true;
			}
			//since no flags, we'll check to see if there are children
			else if (childList.length() != 0)
			{
				branch = true;
			}
		}
		else if (node is ICollectionView ||
				 node is IList ||
				 node is Array)
		{
			//in the special case of a collection of one, 
			//we'll go ahead and get that items children
			if (node.length == 1)
			{
				branch = isBranch(node[0], model);
			}
			//in all other cases we'll just assume that the collection is essentially 
			//already a collection of children
			else
			{
				//isBranch is fairly meaningless to data containers with all other lengths
				branch = false; 
			}
		}
		else if (node is Object)
		{
			try
			{
				//TODO verify this works with all object proxies
				if (node.children != undefined)
				{
					return true;
				}
			}
			catch (e:Error)
			{
				branch = false;
			}
		}
		return branch;
	}

	public function getData(node:Object, model:Object=null):Object
	{
		return Object(node);
	}

	public function addChildAt(node:Object, child:Object, index:int, model:Object=null):Boolean
	{
		// trace("addChildAt", node, child, index);

		var event:CollectionEvent = new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
        event.kind = CollectionEventKind.ADD;
        event.items = [child];
        event.location = index;

		if (!node)
		{
			var iterator:IViewCursor = model.createCursor();
			iterator.seek(CursorBookmark.FIRST, index);
			iterator.insert(child);
			if (model)
				model.dispatchEvent(event);
			return true;
		}
		else if (node is XML)
		{
			var childList:XMLList = node.children();
			if (childList.length() == 0 || childList.length() == index)
			{
				//trace("[Descriptor] childList[index] == child", childList[index] == child);
				if (node != child)
				{
					node.insertChildBefore(null, child);
					if (model)
						model.dispatchEvent(event);
					return true;
				}
			}
			else
			{
				var sibling:XML = childList[index];
				try
				{
					if (node != child)
					{
						node.insertChildBefore(sibling, child);
						if (model)
							model.dispatchEvent(event);
						return true;
					}
				}
				catch (e:Error)
				{
				}
			}
		}
		else if (node is Object)
		{
			if (node.children != undefined)
			{
				node.children.splice(index, 0, child);
				if (model)
					model.dispatchEvent(event);
				return true;
			}
		}
		return false;
	}

	public function removeChildAt(node:Object, child:Object, index:int, model:Object=null):Boolean
	{
		//trace("removeChildAt", node, index);

		var event:CollectionEvent = new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
        event.kind = CollectionEventKind.REMOVE;
        event.items = [child];
        event.location = index;

		//handle top level where there is no parent
		if (!node)
		{
			var iterator:IViewCursor = model.createCursor();
			iterator.seek(CursorBookmark.FIRST, index);
			iterator.remove();
			if (model)
				model.dispatchEvent(event);
			return true;
		}
		else if (node is XML)
		{
			var childList:XMLList = node.children();
			if (childList.length() != 0)
			{
				//dont delete ourself if we got dropped on our head
				if (node != child)
				{
					delete childList[index];
					if (model)
						model.dispatchEvent(event);
					return true;
				}
			}
		}
		else if (node is Object)
		{
			if (node.children != undefined)
			{
				node.children.splice(index, 1);
				if (model)
				model.dispatchEvent(event);
					return true;
			}
		}
		return false;
	}

	public function getType(node:Object):String
	{
		if (node is XML)
		{
			return String(node.@type);
		}
		else if (node is Object)
		{
			try
			{
				return String(node.type);
			}
			catch (e:TypeError)
			{
			}
		}
		return "";
	}

	public function isEnabled(node:Object):Boolean
	{
		var enabled:*;
		if (node is XML)
		{
			enabled = node.@enabled;
			if (enabled[0] == false)
				return false;
		}
		else if (node is Object)
		{
			try
			{
				if ("false" == String(node.enabled))
					return false;
			}
			catch (e:TypeError)
			{
			}
		}
		return true;
	}

	public function setEnabled(node:Object, value:Boolean):void
	{
		if (node is XML)
		{
			node.@enabled = value;
		}
		else if (node is Object)
		{
			try
			{
				node.enabled = value;
			}
			catch (e:TypeError)
			{
			}
		}
	}

	public function isToggled(node:Object):Boolean
	{
		if (node is XML)
		{
			var toggled:* = node.@toggled;
			if (toggled[0] == true)
				return true;
		}
		else if (node is Object)
		{
			try
			{
				return Boolean(node.toggled);
			}
			catch (e:TypeError)
			{
			}
		}
		return false;
	}

	public function setToggled(node:Object, value:Boolean):void
	{
		if (node is XML)
		{
			node.@toggled = value;
		}
		else if (node is Object)
		{
			try
			{
				node.toggled = value;
			}
			catch (e:TypeError)
			{
			}
		}
	}

	public function getGroupName(node:Object):String
	{
		if (node is XML)
		{
			return node.@groupName;
		}
		else if (node is Object)
		{
			try
			{
				return node.groupName;
			}
			catch (e:TypeError)
			{
			}
		}
		return "";
	}

}

}
