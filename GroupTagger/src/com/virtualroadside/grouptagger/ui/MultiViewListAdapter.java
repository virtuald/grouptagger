package com.virtualroadside.grouptagger.ui;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Adapter that allows us to use multiple row view types inside a single list 
 * view, without too much fuss. 
 */
public class MultiViewListAdapter extends BaseAdapter 
{
	public static interface Row
	{
		public View getView(View convertView);
		public int getViewType();
	}
	
	ArrayList<Row> rows;
	int typeCount;
	
	/**
	 * @param rows			List of rows to display
	 * @param typeCount		Number of view types implemented
	 */
	public MultiViewListAdapter(ArrayList<Row> rows, int typeCount)
	{
		this.rows = rows;
		this.typeCount = typeCount;
	}
	
	@Override
	public int getCount() 
	{
		return rows.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return rows.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		return rows.get(position).getView(convertView);
	}
	
	@Override
	public int getItemViewType(int position)
	{
		return rows.get(position).getViewType();
	}

	@Override 
	public int getViewTypeCount()
	{
		return typeCount;
	}
}
