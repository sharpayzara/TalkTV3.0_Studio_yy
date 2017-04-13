package com.sumavision.encrypt;

public class EncryptUtil
{
    static
    {
	System.loadLibrary("tvfan_encrypt");
    }

    public native String encrypt(String content);
}
