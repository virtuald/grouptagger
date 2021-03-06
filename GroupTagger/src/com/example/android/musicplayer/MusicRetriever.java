/*   
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.musicplayer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;


/**
 * Retrieves and organizes media to play. Before being used, you must call {@link #prepare()},
 * which will retrieve all of the music on the user's device (by performing a query on a content
 * resolver). After that, it's ready to retrieve a random song, with its title and URI, upon
 * request.
 */
public class MusicRetriever {
    final String TAG = "MusicRetriever";

    ContentResolver mContentResolver;
    Context mContext;

    // the items (songs) we have queried
    ArrayList<Item> mItems = new ArrayList<Item>();

    public MusicRetriever(Context context, ContentResolver cr) {
    	mContext = context;
        mContentResolver = cr;
    }

    /**
     * Loads music data. This method may take long, so be sure to call it asynchronously without
     * blocking the main thread.
     */
    public void prepare() {
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Log.i(TAG, "Querying media...");
        Log.i(TAG, "URI: " + uri.toString());

        // Perform a query on the content resolver. The URI we're passing specifies that we
        // want to query for all audio media on external storage (e.g. SD card)
        Cursor cur = mContentResolver.query(uri, null,
                MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
        Log.i(TAG, "Query finished. " + (cur == null ? "Returned NULL." : "Returned a cursor."));

        if (cur == null) {
            // Query failed...
            Log.e(TAG, "Failed to retrieve music: cursor is null :-(");
            return;
        }
        if (!cur.moveToFirst()) {
            // Nothing to query. There is no music on the device. How boring.
            Log.e(TAG, "Failed to move cursor to first row (no query results).");
            return;
        }

        Log.i(TAG, "Listing...");

        // retrieve the indices of the columns where the ID, title, etc. of the song are
        int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);

        Log.i(TAG, "Title column index: " + String.valueOf(titleColumn));
        Log.i(TAG, "ID column index: " + String.valueOf(titleColumn));

        // add each song to mItems
        do {
            Log.i(TAG, "ID: " + cur.getString(idColumn) + " Title: " + cur.getString(titleColumn));
            Item item = new Item(cur.getLong(idColumn),
				                 cur.getString(artistColumn),
				                 cur.getString(titleColumn),
				                 cur.getString(albumColumn),
				                 cur.getLong(durationColumn));
            
            /*
             *  TODO: This is incredibly slow because JAudioTagger keeps
             *  opening up the file over and over again... 
            try
            {
            	File tagsFile = Util.getFileFromUri(mContext, item.getURI());
    			item.hasGrouping = TagUtil.hasGroupingTag(tagsFile);
            }
            catch (Exception e)
            {
            	// ignore this
            }
            */
            
            mItems.add(item);
            
        } while (cur.moveToNext());

        Log.i(TAG, "Done querying media. MusicRetriever is ready.");
    }

    public ContentResolver getContentResolver() {
        return mContentResolver;
    }
    
    public ArrayList<Item> getItems()
    {
    	return mItems;
    }

    public static class Item implements Parcelable 
    {
        long id;
        String artist;
        String title;
        String album;
        long duration;
        boolean hasGrouping = false;

        public Item(long id, String artist, String title, String album, long duration) {
            this.id = id;
            this.artist = artist;
            this.title = title;
            this.album = album;
            this.duration = duration;
        }

        public long getId() {
            return id;
        }

        public String getArtist() {
            return artist;
        }

        public String getTitle() {
            return title;
        }

        public String getAlbum() {
            return album;
        }

        public long getDuration() {
            return duration;
        }

        public Uri getURI() {
            return ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        }
        
        public boolean getHasGrouping()
        {
        	return hasGrouping;
        }
        
        //
        // Parcelable interface
        //

        public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() 
        {
		    public Item createFromParcel(Parcel in) {
		        return new Item(in);
		    }
		
		    public Item[] newArray(int size) {
		        return new Item[size];
		    }
		};
		
		private Item(Parcel in)
		{
			id = in.readLong();
			artist = in.readString();
			title = in.readString();
			album = in.readString();
			duration = in.readLong();
		}
        
		@Override
		public int describeContents() 
		{
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) 
		{
			dest.writeLong(id);
			dest.writeString(artist);
			dest.writeString(title);
			dest.writeString(album);
			dest.writeLong(duration);
		}
    }
}
