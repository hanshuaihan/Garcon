package com.example.paintm;
 
import android.os.Parcelable;
import android.util.Log;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
public class Line extends Shape
{ 
	private PointF begin;
    private PointF end; 
    
    public Line()
    {
     begin=new PointF();
     end=new PointF();
		strokeWidth = 10;
    }
	@Override
	public void PointDown(float x,float y,int parentw,int parenth)
	{ 
		begin.x=x;
		begin.y=y;
	}
	@Override
	public int PointMove(float x,float y,int parentw,int parenth)
	{
		end.x=x;
		end.y=y;
		tmppath.reset();
		tmppath.moveTo( begin.x,begin.y);
		tmppath.lineTo( end.x,end.y);
		tmppath.close();
		return 1;
	}
	@Override
	public void PointUp(float x,float y,int parentw,int parenth)
	{
		end.x=x;
		end.y=y;
		path.reset();
		path.moveTo( begin.x,begin.y);
		path.lineTo( end.x,end.y);
		path.close();
		Log.v("Line PointUp"," finished=t drawing=f Line("+ begin.x+"," + begin.y + ") to ("+ end.x+"," + end.y + ")");
		setFinished(true);
		setDrawing(false);
	} 

}