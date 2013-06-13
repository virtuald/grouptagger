package com.virtualroadside.grouptagger;

import java.util.ArrayList;

import com.example.android.musicplayer.MusicRetriever.Item;
import com.example.android.musicplayer.PrepareMusicRetrieverTask;
import com.example.android.musicplayer.PrepareMusicRetrieverTask.MusicRetrieverPreparedListener;
import com.virtualroadside.grouptagger.MainActivity.HasTitle;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileViewFragment extends ListFragment implements HasTitle
{
	static final String FILE_LIST = "FILE_LIST";
	
	ArrayList<Item> musicFiles;
	
	//
	// Android lifecycle functions
	//
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		if (savedInstanceState != null && savedInstanceState.containsKey(FILE_LIST))
		{
			musicFiles = savedInstanceState.getParcelableArrayList(FILE_LIST);
			setupAdapter();
		}
		else
		{
			loadFiles();
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		
		// save the file list here
		outState.putParcelableArrayList(FILE_LIST, musicFiles);
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
	// Implementation
	//
	
	
	/**
	 * Loads the files asynchronously
	 * 
	 * TODO: What about a cursor adapter instead? But then we have to 
	 * figure out how to deal with the service too... 
	 */
	void loadFiles()
	{
		(new PrepareMusicRetrieverTask(getActivity().getContentResolver(), onMusicPrepared)).execute();
	}
	
	MusicRetrieverPreparedListener onMusicPrepared = new MusicRetrieverPreparedListener() 
	{
		@Override
		public void onMusicRetrieverPrepared(ArrayList<Item> items) 
		{
			musicFiles = items;
			setupAdapter();
		}
	};
	
	void setupAdapter()
	{
		setListAdapter(mAdapter);
	}
	
	BaseAdapter mAdapter = new BaseAdapter()
	{
		@Override
		public int getCount() 
		{
			return musicFiles.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return musicFiles.get(position);
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
			
			Item musicItem = musicFiles.get(position);
			
			title.setText(musicItem.getTitle());
			artist.setText(musicItem.getArtist());

			return layoutView;
		}
	};
}
