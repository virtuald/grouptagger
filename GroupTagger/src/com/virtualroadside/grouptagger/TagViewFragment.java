package com.virtualroadside.grouptagger;

import com.virtualroadside.grouptagger.MainActivity.HasTitle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TagViewFragment extends Fragment implements HasTitle
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_tag_view, container, false);
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
}
