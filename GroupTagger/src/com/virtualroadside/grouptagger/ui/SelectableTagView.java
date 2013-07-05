package com.virtualroadside.grouptagger.ui;

import com.virtualroadside.grouptagger.tagging.TagCategories.Tag;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;


public class SelectableTagView extends TextView 
{
	Tag mTag;
	
	
	public SelectableTagView(Context context) 
	{
		super(context);
		
		setTextAppearance(context, android.R.style.TextAppearance_Small);
		setOnClickListener(onClick);
	}
	
	public void setTag(Tag tag)
	{
		mTag = tag;
		setText("[" + tag.name + "] ");
		update();
	}

	void update()
	{
		if (mTag.selected)
			setTextColor(Color.GREEN);
		else
			setTextColor(Color.GRAY);
	}
	
	OnClickListener onClick = new OnClickListener() 
	{	
		@Override
		public void onClick(View v) 
		{
			mTag.selected = !mTag.selected;
			update();
		}
	};
	
}
