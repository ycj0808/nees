package com.neusoft.nees.signName;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.os.Environment;

public class FileUtil
{
     private static final String path=Environment.getExternalStorageDirectory().getAbsolutePath();
	 public static void  newFolder(String  folderPath)  {  
	       try  {  
	           String  filePath  = path+folderPath;  
	           filePath  =  filePath.toString();  
	           java.io.File  myFilePath  =  new  java.io.File(filePath);  
	           if  (!myFilePath.exists())  {  
	               myFilePath.mkdir();  
	           }  
	       }  
	       catch  (Exception  e)  {  
	           System.out.println("�½�Ŀ¼��������");  
	           e.printStackTrace();  
	       }  
	   }  
	 static char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F' };

	public static String getbyte2str(byte[] bytes) {
		int len = bytes.length;
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < len; i++) {
			byte byte0 = bytes[i];
			result.append(hex[byte0 >>> 4 & 0xf]);
			result.append(hex[byte0 & 0xf]);
		}
		return result.toString();
	}

	public static String getMd5(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");// ����ʹ��MD5�㷨
		md5.update(bytes);
		return getbyte2str(md5.digest());
	}
	//
	public static byte[] getPdfByte(String path) throws IOException {

		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		int fileLength = (int) file.length();
		byte[] fileBytes = new byte[fileLength];
		fis.read(fileBytes);
		fis.close();
		return fileBytes;
	}
	//md5  У�鷵��ֵ
	public static String getMd5Value(String path) throws Exception, IOException
	{
		
	      return getMd5(getPdfByte(path));
	}
	
	public static String[] getData(String path) throws Exception
	{
		BufferedReader reader= new BufferedReader(new FileReader(path)); 
		StringBuffer str =new StringBuffer("");
		 String temp="";
		 while( (temp=reader.readLine())!=null){   
			 str.append(temp).append(",");       
			            }   
           if(!"".equals(temp))
           {
		 temp=str.toString();
		 return temp.split(",");
           } else {
			return null;
		}
		 
	}
	
}
