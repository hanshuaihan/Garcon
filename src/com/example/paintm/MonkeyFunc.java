package com.example.paintm;
/*
 *  "Monkey" Java Common Function Library By WWCMonkey
 * 		-- 2014.2
 * */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class MonkeyFunc
{
	public static String logTag="MonkeyFuncLib";

    public static String getSDPath(){ 
        File sdDir = null; 
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);   //�ж�sd���Ƿ���� 
        if(sdCardExist)   
        {                               
          sdDir = Environment.getExternalStorageDirectory();//��ȡ��Ŀ¼ 
        }else return null;   
        return sdDir.toString(); 
    } 
    public static void makeFolder(String folder)
    {
		makeFolder(folder,0);
	}
	public static void makeFolder(String folder,int start)
	{
		if(getSDPath() ==null || folder==null || folder==""){ return ;} 
		if(folder.charAt(folder.length()-1)!='/') folder=folder+"/";
		int a=folder.indexOf("/",start),b=-1;
		String f2;
		File dirFirstFile;
		while(a>-1){
			if(a-b>1){
				f2=folder.substring(0, a);
			 dirFirstFile=new File(f2);
			  
			 if(!dirFirstFile.exists()) { 
	             dirFirstFile.mkdir(); 
			 }
			}
			b=a;
			a=folder.indexOf("/", a+1);
		}
	} 
	public static void saveMyBitmap(String fileName,Bitmap mBitmap){
		  File f = new File(fileName);
		  try {
		   f.createNewFile();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   Log.v(logTag,"Error while saving bitmap \""+fileName+"\"");
		   return ;
		  }
		  FileOutputStream fOut = null;
		  try {
		   fOut = new FileOutputStream(f);
		  } catch (FileNotFoundException e) {
		   e.printStackTrace();
		  }
		  mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		  try {
		   fOut.flush();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		  try {
		   fOut.close();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
	 }
	public static String getDateTime(){
		return getDateTime("yyyy-MM-dd HH:mm:ss");
	}
	public static String getDateTime(String format)
	{
		SimpleDateFormat df=new SimpleDateFormat(format);
		return df.format(new Date()); 
	}
	public static void showToast(Context v,String text,int time)
	{
		Toast.makeText(v, text, time).show();
	}
	
	public static Bitmap shootPic(View windowDecorView,View target)
	{
		target.setDrawingCacheEnabled(true);
		//Bitmap b;//=img1.getDrawingCache();
		//Bitmap image = Bitmap.createBitmap(img1.getWidth(), img1.getHeight(), Bitmap.Config.RGB_565);
		
        View cv = windowDecorView;
        Bitmap b = Bitmap.createBitmap(cv.getWidth(), cv.getHeight(),
        		Bitmap.Config.RGB_565);
        cv.draw(new Canvas(b));
        target.setDrawingCacheEnabled(false);
        return b;
	}
	public static Bitmap getRoatedImage(Uri mImageCaptureUri,ContentResolver cr) {

		// ���������ջ���ѡ��ͼƬÿ��ͼƬ�����������д洢Ҳ�洢�ж�Ӧ��ת�Ƕ�orientationֵ
		// ����������ȡ��ͼƬ�ǰѽǶ�ֵȡ���Ա�����ȷ����ʾͼƬ,û����תʱ��Ч���ۿ�
 
		Cursor cursor = cr.query(mImageCaptureUri, null, null, null, null);// ����Uri�����ݿ�����
		if (cursor != null) {
			cursor.moveToFirst();// ���α��ƶ�����λ����Ϊ�����Uri�ǰ���ID��������Ψһ�Ĳ���Ҫѭ����ָ���һ��������
			String filePath = cursor.getString(cursor.getColumnIndex("_data"));// ��ȡͼƬ·
			String orientation = cursor.getString(cursor
					.getColumnIndex("orientation"));// ��ȡ��ת�ĽǶ�
			cursor.close();
			if (filePath != null) {
				//Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
				Bitmap bitmap = BitmapFactory.decodeFile(filePath);//����Path��ȡ��ԴͼƬ 
				int angle = 0;
				if (orientation != null && !"".equals(orientation)) {
					angle = Integer.parseInt(orientation);
				}
				if (angle != 0) {
					// ����ķ�����Ҫ�����ǰ�ͼƬתһ���Ƕȣ�Ҳ���ԷŴ���С��
					Matrix m = new Matrix();
					int width = bitmap.getWidth();
					int height = bitmap.getHeight();
					m.setRotate(angle); // ��תangle��
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
							m, true);// ��������ͼƬ
					
				}
				return bitmap;
			}
		}
		return null;
	}
}