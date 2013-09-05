package com.virtualroadside.grouptagger.tagging;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.virtualroadside.grouptagger.Util;

import android.os.Parcel;
import android.os.Parcelable;

public class TagCategories implements Parcelable
{
	public static class Tag implements Parcelable, Comparable<Tag>
	{
		public String name;
		public boolean selected = false;
		
		public Tag(String name)
		{
			this(name, false);
		}
		
		public Tag(String name, boolean selected)
		{
			this.name = name;
			this.selected = selected;
		}
		
		// copy constructor
		public Tag(Tag other)
		{
			this(other.name, other.selected);
		}
		
		//
        // Parcelable interface
        //

        public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() 
        {
		    public Tag createFromParcel(Parcel in) {
		        return new Tag(in);
		    }
		
		    public Tag[] newArray(int size) {
		        return new Tag[size];
		    }
		};
		
		private Tag(Parcel in)
		{
			name = in.readString();
			selected = in.readInt() == 0 ? false : true;
		}
        
		@Override
		public int describeContents() 
		{
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) 
		{
			dest.writeString(name);
			dest.writeInt(selected ? 1 : 0);
		}

		@Override
		public int compareTo(Tag another) 
		{
			return this.name.compareTo(another.name);
		}
	}
	
	public static class TagCategory implements Parcelable, Comparable<TagCategory>
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
		
		//
        // Parcelable interface
        //

        public static final Parcelable.Creator<TagCategory> CREATOR = new Parcelable.Creator<TagCategory>() 
        {
		    public TagCategory createFromParcel(Parcel in) {
		        return new TagCategory(in);
		    }
		
		    public TagCategory[] newArray(int size) {
		        return new TagCategory[size];
		    }
		};
		
		private TagCategory(Parcel in)
		{
			name = in.readString();
			expanded = in.readInt() == 0 ? false : true;
			in.readTypedList(tags, Tag.CREATOR);
		}
        
		@Override
		public int describeContents() 
		{
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) 
		{
			dest.writeString(name);
			dest.writeInt(expanded ? 1 : 0);
			dest.writeTypedList(tags);
		}

		@Override
		public int compareTo(TagCategory another) 
		{
			return this.name.compareTo(another.name);
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
	
	public ArrayList<TagCategory> getCategories()
	{
		return categories;
	}
	
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
	
	// set the number of selected from a tag string
	public void setSelectedFromString(String tagsString)
	{
		TagCategory uncategorized = null;
		String [] tags = tagsString.split(" ");
		
		for (String tagString: tags)
		{
			boolean found = false;
			
			// TODO: deal with underscore correctly
			tagString = tagString.replace("_", " ");
			
			// linear search for now.. for small N this is good enough
			for (TagCategory category: categories)
			{
				// cache this for later.. 
				if (uncategorized == null && category.name.equals("uncategorized"))
					uncategorized = category;
				
				for (Tag tag: category.tags)
				{
					if (tag.name.equals(tagString))
					{
						tag.selected = true;
						found = true;
						break;
					}
				}
				
				if (found)
					break;
			}
			
			if (!found)
			{
				// add a new tag for it under uncategorized
				if (uncategorized == null)
				{
					uncategorized = new TagCategory();
					uncategorized.name = "uncategorized";
					uncategorized.expanded = true;
					
					categories.add(uncategorized);
				}
				
				uncategorized.tags.add(new Tag(tagString));
			}
		}
	}
	
	public String getSelectedAsString()
	{
		ArrayList<String> selected = new ArrayList<String>();
		
		for (TagCategory category: categories)
		{
			for (Tag tag: category.tags)
			{
				if (tag.selected)
					selected.add(tag.name.replace(" ", "_"));
			}
		}
		
		// sort the list by name
		Collections.sort(selected);
		
		// concatenate them together
		StringBuilder builder = new StringBuilder();
		
		for (String s: selected)
		{
			builder.append(s);
			builder.append(" ");
		}
		
		return builder.toString().trim();
	}
	
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
			
			// sort the data
			Collections.sort(category.tags);
			
			// store the data 
			categories.categories.add(category);
		}
		
		Collections.sort(categories.categories);
		
		return categories;
	}
	
	public void writeJson()
	{
		
	}
	
	
	//
    // Parcelable interface
    //

    public static final Parcelable.Creator<TagCategories> CREATOR = new Parcelable.Creator<TagCategories>() 
    {
	    public TagCategories createFromParcel(Parcel in) {
	        return new TagCategories(in);
	    }
	
	    public TagCategories[] newArray(int size) {
	        return new TagCategories[size];
	    }
	};
	
	private TagCategories(Parcel in)
	{
		in.readTypedList(categories, TagCategory.CREATOR);
	}
    
	@Override
	public int describeContents() 
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeTypedList(categories);
	}

}
