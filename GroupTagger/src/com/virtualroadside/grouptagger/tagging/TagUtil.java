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
	

	public static String getTagsFromFile(File file)
	{
		try 
		{
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();
			
			// TODO: support multiple field types
			
			return tag.getFirst(FieldKey.GROUPING);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setTagsOnFileIfChanged(File file, String tagString)
	{
		try 
		{
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();
			
			String oldTags = tag.getFirst(FieldKey.GROUPING);
			
			// only save the tags if they've changed
			if (oldTags == null || !oldTags.equals(tagString))
			{
				if (tagString.isEmpty())
				{
					Log.i(TAG, "Tags deleted for " + file.getAbsolutePath());
					
					tag.deleteField(FieldKey.GROUPING);
				}
				else
				{
					Log.i(TAG, "Tags changed from \"" + oldTags + "\" to \"" + tagString + "\" for "+ file.getAbsolutePath());
					
					tag.setField(FieldKey.GROUPING, tagString);
					f.commit();
				}
			}
			else
			{
				Log.i(TAG, "Tags not changed for " + file.getAbsolutePath());
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "Error saving tags for " + file.getAbsolutePath());
			e.printStackTrace();
		}
	}
	
}
