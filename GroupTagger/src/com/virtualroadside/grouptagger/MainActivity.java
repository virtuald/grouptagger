package com.virtualroadside.grouptagger;

import java.util.ArrayList;
import java.util.List;

import com.virtualroadside.grouptagger.ui.SearchableFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;

public class MainActivity extends FragmentActivity 
{
	// child fragments have to implement this
	public static interface HasTitle
	{
		public String getTitle();
	}
	
	List<Fragment> fragments = new ArrayList<Fragment>();
	
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// setup fragments
		fragments.add(new FileViewFragment());
		fragments.add(new TagViewFragment());
		
		// setup the ViewPager
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
		
		
		mViewPager.setOnPageChangeListener(onPageChanged);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//
	// Android events
	//
	
	@Override
    public boolean onKeyDown(int keycode, KeyEvent e) 
	{
        switch(keycode) 
        {
            case KeyEvent.KEYCODE_SEARCH:
            	
            	Fragment f = fragments.get(mViewPager.getCurrentItem());
            	if (f instanceof SearchableFragment)
            		((SearchableFragment)f).onSearch();
            	
            	break;
            	
            default:
        }
        
        return super.onKeyDown(keycode, e);
	}
	
	//
	// Pager adapter stuff
	//
	
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
