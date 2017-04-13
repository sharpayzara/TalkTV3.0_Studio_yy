package com.sumavision.talktv.videoplayer.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.util.Log;

public class FileUtils
{
    private String SDPATH;

    public String getSDPATH()
    {
	return SDPATH;
    }

    public FileUtils()
    {
	SDPATH = Environment.getExternalStorageDirectory() + "/";
    }

    public File creatSDFile(String fileName) throws IOException
    {
	File file = new File(SDPATH + fileName);
	file.createNewFile();
	return file;
    }

    public File creatSDDir(String dirName)
    {
	File dir = new File(SDPATH + dirName);
	dir.mkdirs();
	return dir;
    }

    public boolean isFileExist(String fileName)
    {
	// File file = new File(SDPATH + fileName);
	File file = new File(fileName);
	return file.exists();
    }

    public File write2SDFromInput(String path, String fileName, InputStream input)
    {
	File file = null;
	OutputStream output = null;
	try
	{
	    // creatSDDir(path);
	    // file = creatSDFile(path + fileName);
	    file = new File(path + fileName);
	    file.createNewFile();
	    output = new FileOutputStream(file);
	    byte buffer[] = new byte[4 * 1024];
	    // Log.i("buffer length: ", ""+buffer.length);
	    int count;
	    while ((count = input.read(buffer)) != -1)
	    {
		Log.i("count: ", "" + count);
		// output.write(buffer);
		output.write(buffer, 0, count);
	    }
	    output.flush();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	finally
	{
	    try
	    {
		output.close();
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }
	}
	return file;
    }
}
