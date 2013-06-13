package com.virtualroadside.grouptagger;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;

public class MainActivity extends FragmentActivity 
{
	// child fragments have to implement this
	public static interface HasTitle
	{
		public String getTitle();
	}
	
	List<Fragment> fragments = new ArrayList<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// setup fragments
		fragments.add(new FileViewFragment());
		fragments.add(new TagViewFragment());
		
		// setup the ViewPager
		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
		
		
		viewPager.setOnPageChangeListener(onPageChanged);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	OnPageChangeListener onPageChanged = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int pageNum) 
		{
			setTitle(((HasTitle)fragments.get(pageNum)).getTitle());
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) 
		{
					
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) 
		{
					
		}
	};
	
	
	// pager adapter boilerplate
	class PageAdapter extends FragmentPagerAdapter
	{
		public PageAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int index) 
		{
			return fragments.get(index);
		}

		@Override
		public int getCount() 
		{
			return fragments.size();
		}
	}
}
