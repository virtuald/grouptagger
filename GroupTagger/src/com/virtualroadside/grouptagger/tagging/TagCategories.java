package com.virtualroadside.grouptagger.tagging;

import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.virtualroadside.grouptagger.Util;

import android.util.JsonReader;

public class TagCategories 
{
	public static class Tag
	{
		public String name;
		public boolean selected = false;
		
		public Tag(String name)
		{
			this.name = name;
		}
		
		// copy constructor
		public Tag(Tag other)
		{
			this.name = other.name;
			this.selected = other.selected;
		}
	}
	
	public static class TagCategory
	{
		public String name;
		public boolean expanded = true;
		public ArrayList<Tag> tags = new ArrayList<Tag>();
		
		public TagCategory()
		{
			// empty
		}
		
		// copy constructor
		public TagCategory(TagCategory other)
		{
			this.name = other.name;
			this.expanded = other.expanded;
			
			for (Tag tag: other.tags)
				this.tags.add(new Tag(tag));
		}
	}
	
	// categories
	ArrayList<TagCategory> categories = new ArrayList<TagCategories.TagCategory>();
	
	
	public TagCategories()
	{
		// empty
	}
	
	// copy constructor
	public TagCategories(TagCategories other)
	{
		for (TagCategory category: other.categories)
			categories.add(new TagCategory(category));
	}
	
	
	//
	// ExpandableListViewAdapter related API
	//
	
	public TagCategory getCategory(int catPosition)
	{
		return categories.get(catPosition);
	}
	
	public int getCategoryCount()
	{
		return categories.size();
	}
	
	public Tag getTag(int catPosition, int tagPosition)
	{
		return categories.get(catPosition).tags.get(tagPosition);
	}
	
	public int getTagCount(int catPosition)
	{
		return categories.get(catPosition).tags.size();
	}
	
	
	// todo: combinatorial API for actual tags
	
	
	/*
	 * JSON Format:
	 * 
	 * 	{"category name": [expanded, [tag1, tag2... tagN]], ... }
	 * 
	 */
	
	// load a tagcategories instance from a JSON file
	public static TagCategories loadJson(InputStream is) throws JSONException
	{
		TagCategories categories = new TagCategories();
		
		// load the string from file
		String jsonText = Util.readStreamAsString(is);
		
		JSONObject base = (JSONObject)new JSONTokener(jsonText).nextValue();
		Iterator<?> keyIter = base.keys();
		
		while (keyIter.hasNext())
		{
			TagCategory category = new TagCategory();
			
			// key is the category name
			category.name = (String)keyIter.next();
			
			// value is [enclosed, [tag.. ]]
			// each category has an array of strings
			JSONArray value = base.getJSONArray(category.name);
			category.expanded = value.getBoolean(0);
			
			JSONArray tags = value.getJSONArray(1);
			
			for (int i = 0; i < tags.length(); ++i)
				category.tags.add(new Tag(tags.getString(i)));
			
			// store the data 
			categories.categories.add(category);
		}
		
		return categories;
	}
	
	// load a tagcategories instance from a string.. 
	

}
