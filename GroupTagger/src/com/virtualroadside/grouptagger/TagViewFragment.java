package com.virtualroadside.grouptagger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.json.JSONException;

import com.example.android.musicplayer.MusicRetriever.Item;
import com.virtualroadside.grouptagger.MainActivity.HasTitle;
import com.virtualroadside.grouptagger.tagging.TagCategories;
import com.virtualroadside.grouptagger.tagging.TagCategories.Tag;
import com.virtualroadside.grouptagger.tagging.TagCategories.TagCategory;
import com.virtualroadside.grouptagger.ui.MultiViewListAdapter;
import com.virtualroadside.grouptagger.ui.MultiViewListAdapter.Row;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TagViewFragment extends Fragment implements HasTitle
{
	
	TagCategories defaultCategories = null;
	TagCategories currentTags = new TagCategories();
	
	ExpandableListView listView;
	TagViewAdapter mAdapter;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// load the default tags
		new TagLoader().execute();
		
		View view = inflater.inflate(R.layout.fragment_tag_view, container, false);
		
		listView = (ExpandableListView)view.findViewById(R.id.tagListView);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		mAdapter = new TagViewAdapter();
		listView.setAdapter(mAdapter);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		
	}

	@Override
	public String getTitle() 
	{
		return "GroupTagger";
	}
	
	public void showTags()
	{
		// load cars
		
		// 
		if (defaultCategories != null)
		{
			currentTags = new TagCategories(defaultCategories);
		}
		
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
	}
	
	
	// load all tags
	class TagLoader extends AsyncTask<Void, Void, TagCategories>
	{
		String errString = null;

		@Override
		protected TagCategories doInBackground(Void... params) 
		{
			// todo: improve.. 
			String tagPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/tags.json";
			FileInputStream fStream;
			try 
			{
				fStream = new FileInputStream(tagPath);
				return TagCategories.loadJson(fStream);
			} 
			catch (FileNotFoundException e1) 
			{
				errString = tagPath + " not found!";
			} 
			catch (JSONException e) 
			{
				errString = "Error reading tags.json!";
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(TagCategories result) 
		{
			if (result == null)
			{
				Toast.makeText(getActivity(), errString, Toast.LENGTH_LONG).show();
			}
			
			defaultCategories = result;
			showTags();
		}
		
	}
	
	// events.. 
	
	// on new track started!
	
	// connect buttons to service
	
	// other crap.. 
	
	// load tags from track
	
	// interpolate tags into array...
	
	// notify list view that things changed
	
	class TagViewAdapter extends BaseExpandableListAdapter
	{

		@Override
		public int getGroupCount() 
		{
			return currentTags.getCategoryCount();
		}

		@Override
		public int getChildrenCount(int groupPosition) 
		{
			return currentTags.getTagCount(groupPosition);
		}

		@Override
		public Object getGroup(int groupPosition) 
		{
			return currentTags.getCategory(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) 
		{
			return currentTags.getTag(groupPosition, childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) 
		{
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) 
		{
			return childPosition;
		}

		@Override
		public boolean hasStableIds() 
		{
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) 
		{
			TextView textView;
			
			if (convertView == null)
			{
				textView = new TextView(getActivity());
				textView.setTypeface(null, Typeface.BOLD);
			}
			else
			{
				textView = (TextView)convertView;
			}
			
			TagCategory category = (TagCategory)getGroup(groupPosition);
			
			textView.setText(category.name);
			return textView;

		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) 
		{
			CheckBox checkBox;
			
			if (convertView == null)
			{
				checkBox = new CheckBox(getActivity());
			}
			else
			{
				checkBox = (CheckBox)convertView;
			}
			
			Tag tag = (Tag)getChild(groupPosition, childPosition);
			
			checkBox.setText(tag.name);
			checkBox.setChecked(tag.selected);
			return checkBox;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) 
		{
			return false;
		}
		
	}
	
}
