package com.virtualroadside.grouptagger;

import java.io.InputStream;

public class Util 
{
	
	// from http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
	public static String readStreamAsString(InputStream is)
	{
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

}
