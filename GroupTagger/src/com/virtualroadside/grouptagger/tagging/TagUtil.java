package com.virtualroadside.grouptagger.tagging;

import java.io.File;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import android.util.Log;

public class TagUtil 
{
	static final String TAG = "TagUtil";
	

	public static void loadFile(File file)
	{
		try 
		{
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();
			
			String groupingTag = tag.getFirst(FieldKey.GROUPING);
			
			Log.d(TAG, "Found grouping tag: " + groupingTag);
			
			
			// writing a tag:
			//tag.setField(FieldKey.GROUPING, groupingTag);
			//f.commit();
			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
