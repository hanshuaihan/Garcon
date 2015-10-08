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
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在 
        if(sdCardExist)   
        {                               
          sdDir = Environment.getExternalStorageDirectory();//获取跟目录 
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

		// 不管是拍照还是选择图片每张图片都有在数据中存储也存储有对应旋转角度orientation值
		// 所以我们在取出图片是把角度值取出以便能正确的显示图片,没有旋转时的效果观看
 
		Cursor cursor = cr.query(mImageCaptureUri, null, null, null, null);// 根据Uri从数据库中找
		if (cursor != null) {
			cursor.moveToFirst();// 把游标移动到首位，因为这里的Uri是包含ID的所以是唯一的不需要循环找指向第一个就是了
			String filePath = cursor.getString(cursor.getColumnIndex("_data"));// 获取图片路
			String orientation = cursor.getString(cursor
					.getColumnIndex("orientation"));// 获取旋转的角度
			cursor.close();
			if (filePath != null) {
				//Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
				Bitmap bitmap = BitmapFactory.decodeFile(filePath);//根据Path读取资源图片 
				int angle = 0;
				if (orientation != null && !"".equals(orientation)) {
					angle = Integer.parseInt(orientation);
				}
				if (angle != 0) {
					// 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
					Matrix m = new Matrix();
					int width = bitmap.getWidth();
					int height = bitmap.getHeight();
					m.setRotate(angle); // 旋转angle度
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
							m, true);// 从新生成图片
					
				}
				return bitmap;
			}
		}
		return null;
	}
}