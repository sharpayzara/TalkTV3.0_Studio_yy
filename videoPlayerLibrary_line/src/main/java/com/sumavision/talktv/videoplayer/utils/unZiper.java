package com.sumavision.talktv.videoplayer.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class unZiper
{

    public static void Unzip(String zipFile, String targetDir)
    {
	int BUFFER = 4096;
	String strEntry;

	try
	{
	    BufferedOutputStream dest = null;
	    FileInputStream fis = new FileInputStream(zipFile);
	    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	    ZipEntry entry;

	    while ((entry = zis.getNextEntry()) != null)
	    {

		try
		{
		    Log.i("Unzip: ", "=" + entry);
		    int count;
		    byte data[] = new byte[BUFFER];
		    strEntry = entry.getName();

		    File entryFile = new File(targetDir + strEntry);
		    File entryDir = new File(entryFile.getParent());
		    if (!entryDir.exists())
		    {
			entryDir.mkdirs();
		    }

		    FileOutputStream fos = new FileOutputStream(entryFile);
		    dest = new BufferedOutputStream(fos, BUFFER);
		    while ((count = zis.read(data, 0, BUFFER)) != -1)
		    {
			dest.write(data, 0, count);
		    }
		    dest.flush();
		    dest.close();
		}
		catch (Exception ex)
		{
		    ex.printStackTrace();
		}
	    }
	    zis.close();
	}
	catch (Exception cwj)
	{
	    cwj.printStackTrace();
	}
    }

    public static void unZip(String unZipfileName, String mDestPath)
    {
	if (!mDestPath.endsWith("/"))
	{
	    mDestPath = mDestPath + "/";
	    Log.e("unzip", mDestPath);
	}
	FileOutputStream fileOut = null;
	ZipInputStream zipIn = null;
	ZipEntry zipEntry = null;
	File file = null;
	int readedBytes = 0;
	byte buf[] = new byte[4096];

	// int i = 0;

	try
	{
	    zipIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(unZipfileName)));
	    while ((zipEntry = zipIn.getNextEntry()) != null)
	    {
		Log.i("get a entry", zipEntry.getName());
		file = new File(mDestPath + zipEntry.getName());
		// file = new
		// File("/data/data/com.baidu.bvideoviewsample2/files/1.so");
		if (zipEntry.isDirectory())
		{
		    file.mkdirs();
		}
		else
		{
		    // ���ָ���ļ���Ŀ¼������?�򴴽�֮.
		    File parent = file.getParentFile();
		    if (!parent.exists())
		    {
			parent.mkdirs();
		    }
		    // file.createNewFile();
		    Log.i("creatnewfile", "creatnewfile");
		    fileOut = new FileOutputStream(file);
		    while ((readedBytes = zipIn.read(buf)) > 0)
		    {
			fileOut.write(buf, 0, readedBytes);
		    }
		    fileOut.close();
		}
		zipIn.closeEntry();
	    }
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace();
	}
    }
}
