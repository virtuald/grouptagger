package com.virtualroadside.grouptagger;

import java.util.ArrayList;

import com.example.android.musicplayer.MusicRetriever.Item;
import com.example.android.musicplayer.MusicService;
import com.example.android.musicplayer.MusicService.MusicEventNotification;
import com.example.android.musicplayer.MusicService.MusicServiceBinder;
import com.example.android.musicplayer.MusicService.State;
import com.example.android.musicplayer.PrepareMusicRetrieverTask;
import com.example.android.musicplayer.PrepareMusicRetrieverTask.MusicRetrieverPreparedListener;
import com.virtualroadside.grouptagger.MainActivity.HasTitle;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileViewFragment extends ListFragment implements HasTitle
{
	static final String TAG = "FileViewFragment";
	static final String FILE_LIST = "FILE_LIST";
	
	// TODO: add a refresh button! Or menu item. Hmm.. 
	
	ArrayList<Item> mItems = null;

	protected MusicService mService;
	
	//
	// Android lifecycle functions
	//
	
	
	
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
		setListAdapter(mAdapter);
	}
	
	BaseAdapter mAdapter = new BaseAdapter()
	{
		@Override
		public int getCount() 
		{
			return mItems.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return mItems.get(position);
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
			
			Item musicItem = mItems.get(position);
			
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
	};
}
