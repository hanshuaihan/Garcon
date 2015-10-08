package com.example.paintm;
 
import android.os.Parcelable;
import android.util.Log;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
public class FreeHand extends Shape
{ 
	 boolean down;
    public FreeHand()
    { 
    	down=false;
    }
	@Override
	public void PointDown(float x,float y,int parentw,int parenth)
	{ 
		down=true;
		tmppath.reset();
		tmppath.moveTo(x, y);
	}
	@Override
	public int PointMove(float x,float y,int parentw,int parenth)
	{
		if(down)
		{
			tmppath.lineTo(x, y) ;
		}
		return 1;
	}
	@Override
	public void PointUp(float x,float y,int parentw,int parenth)
	{  
		path.reset();
		path.addPath(tmppath); 
		Log.v("FreeHand PointUp"," finished=t drawing=f");
		setFinished(true);
		setDrawing(false);
	} 

}