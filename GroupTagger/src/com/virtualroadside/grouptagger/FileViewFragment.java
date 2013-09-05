package com.virtualroadside.grouptagger;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.example.android.musicplayer.MusicRetriever.Item;
import com.example.android.musicplayer.MusicService;
import com.example.android.musicplayer.MusicService.MusicEventNotification;
import com.example.android.musicplayer.MusicService.MusicServiceBinder;
import com.example.android.musicplayer.MusicService.State;
import com.virtualroadside.grouptagger.MainActivity.HasTitle;
import com.virtualroadside.grouptagger.ui.SearchableFragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FileViewFragment extends ListFragment implements HasTitle, SearchableFragment
{
	static final String TAG = "FileViewFragment";
	static final String FILE_LIST = "FILE_LIST";
	
	// TODO: add a refresh button! Or menu item. Hmm.. 
	
	ArrayList<Item> mItems = null;

	protected MusicService mService;
	
	private RelativeLayout mInteriorView;
	private EditText mFilterText;
	
	//
	// Android lifecycle functions
	//
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// we can't use a custom layout, so create the normal layout, and 
		// insert it into our custom layout instead. :) 
		
		// http://stackoverflow.com/a/16687602
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		ViewGroup parent = (ViewGroup)inflater.inflate(R.layout.fragment_file_view, container, false);
		parent.addView(view);
		
		// fix up the location
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
		layoutParams.addRule(RelativeLayout.BELOW, R.id.interiorView);
		
		// grab the views we need to interact with
		mInteriorView = (RelativeLayout)parent.findViewById(R.id.interiorView);
		mFilterText = (EditText)parent.findViewById(R.id.filterText);
		
		mFilterText.addTextChangedListener(mFilterTextWatcher);
		
		return parent;
	}
	
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState)
	{
		Log.i(TAG, "onActivityCreated");
		
		super.onActivityCreated(savedInstanceState);
	
		setEmptyText("No audio files found.");
		
		this.getListView().setOnItemLongClickListener(onItemLongClick);
		
		if (savedInstanceState != null && savedInstanceState.containsKey(FILE_LIST))
		{
			if (mItems == null)
			{
				mItems = savedInstanceState.getParcelableArrayList(FILE_LIST);
				if (mItems != null)
					setupAdapter();
			}
		}
		
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		Log.i(TAG, "onStart");
		
		// connect to the service
		Intent intent = new Intent(getActivity(), MusicService.class);
		getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		
		Log.i(TAG, "onStop");
		
		if (mService != null)
		{
			Log.i(TAG, "unbind");
			mService.unsubscribeForNotifications(mEventNotification);
			getActivity().unbindService(mConnection);
			mService = null;
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		
		// save the file list here
		outState.putParcelableArrayList(FILE_LIST, mItems);
	}
	
	
	
	//
	// HasTitle interface
	//
	
	@Override
	public String getTitle() 
	{
		return "Audio Files";
	}
	
	//
	// SearchableFragment interface
	//
	
	@Override
	public void onSearch() 
	{
		if (mInteriorView.getVisibility() == View.GONE)
		{
			mInteriorView.setVisibility(View.VISIBLE);
			
			mFilterText.setText("");
			mFilterText.requestFocus();
		}
		else
		{
			mInteriorView.setVisibility(View.GONE);
			mAdapter.resetFilter();
		}
	}
	
	
	//
	// Service interface
	//
	
	ServiceConnection mConnection = new ServiceConnection() 
	{	
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) 
		{
			Log.i(TAG, "onServiceConnect");
			
			MusicServiceBinder binder = (MusicServiceBinder)service;
			mService = binder.getService();
			
			mService.subscribeForNotifications(mEventNotification);
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) 
		{
			Log.i(TAG, "onServiceDisconnect");
			
			// reconnect?
			mService = null;
		}
	};
	
	MusicEventNotification mEventNotification = new MusicEventNotification() 
	{
		@Override
		public void onStateChange(State newState, Item newItem) 
		{
			Log.i(TAG, "onStateChange");
			
			if (mItems == null)
			{
				mItems = mService.getAudioItems();
				if (mItems != null)
					setupAdapter();
			}
		}
	};
	
	
	//
	// Implementation
	//
	
	
	OnItemLongClickListener onItemLongClick = new OnItemLongClickListener() 
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) 
		{
			Intent playIntent = new Intent(MusicService.ACTION_PLAY);
			playIntent.putExtra(MusicService.PLAY_IDX, position);
			getActivity().startService(playIntent);
			return false;
		}
	};
	
	void setupAdapter()
	{
		Log.i(TAG, "setupAdapter");
		
		mAdapter.resetFilter();
		setListAdapter(mAdapter);
	}
			
	class FileViewAdapter extends BaseAdapter implements Filterable
	{
		ArrayList<Item> adapterItems = null;
		
		@Override
		public int getCount() 
		{
			return adapterItems.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return adapterItems.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup container) 
		{
			LinearLayout layoutView = null;
			
			if (view == null)
			{
				LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				layoutView = (LinearLayout)inflater.inflate(R.layout.file_list_view, null);
			}
			else
			{
				layoutView = (LinearLayout)view;
			}
			
			TextView title = (TextView)layoutView.findViewById(R.id.file_list_view_title);
			TextView artist = (TextView)layoutView.findViewById(R.id.file_list_view_artist);
			
			Item musicItem = adapterItems.get(position);
			
			/* When I fix jAudioTagger, should implement this... 
			 * 
			String indicator = "";
			if (!musicItem.getHasGrouping())
				indicator = "*";
			*/
			
			title.setText(musicItem.getTitle());
			artist.setText(musicItem.getArtist());

			return layoutView;
		}

		@Override
		public Filter getFilter() 
		{
			return new Filter()
			{
				@Override
				protected FilterResults performFiltering(CharSequence constraint) 
				{
					FilterResults results = new FilterResults();
					
					if (constraint.length() == 0)
					{
						// shortcut
						results.values = mItems;
					}
					else
					{
						ArrayList<Item> filteredItems = new ArrayList<Item>();
						Pattern pattern = Pattern.compile(Pattern.quote(constraint.toString()), Pattern.CASE_INSENSITIVE);
						
						for (Item item: mItems)
						{
							if (pattern.matcher(item.getArtist()).find() || pattern.matcher(item.getTitle()).find())
								filteredItems.add(item);
						}
						
						results.values = filteredItems;
					}
					
					return results;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) 
				{
					adapterItems = (ArrayList<Item>)results.values;
					notifyDataSetChanged();
				}
			};
		}
		
		public void resetFilter()
		{
			adapterItems = mItems;
		}
	};
	
	FileViewAdapter mAdapter = new FileViewAdapter();
	
	//
	// Filter editing
	//
	
	TextWatcher mFilterTextWatcher = new TextWatcher()
	{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) 
		{
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) 
		{
			mAdapter.getFilter().filter(s);
		}

		@Override
		public void afterTextChanged(Editable s) 
		{
			
		}
		
	};
}
