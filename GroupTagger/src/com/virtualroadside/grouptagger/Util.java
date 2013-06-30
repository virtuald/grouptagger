package com.virtualroadside.grouptagger;

import java.io.File;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Util 
{
	
	// from http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
	public static String readStreamAsString(InputStream is)
	{
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

	
	// from http://stackoverflow.com/questions/5657411/android-getting-a-file-uri-from-a-content-uri
	public static File getFileFromUri(Context context, Uri uri)
	{
		String filePath;
		
		if (uri.getScheme().equals("content"))
		{
			Cursor cursor = context.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
			cursor.moveToFirst();   
			filePath = cursor.getString(0);
			cursor.close();
		}
		else
		{
			filePath = uri.getPath();
		}
        
        return new File(filePath);
	}
}
