package com.example.paintm;
 
import android.os.Parcelable;
import android.util.Log;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
public class Rectangle extends Shape
{ 
	private PointF begin;
    private PointF end; 

	RectF rect = new RectF();
    public Rectangle()
    {
     begin=new PointF();
     end=new PointF();
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
		rect.set(begin.x, begin.y, end.x, end.y);
		tmppath.reset();
		tmppath.addRect(rect, Path.Direction.CW);
		tmppath.close();
		return 1;
	}
	@Override
	public void PointUp(float x,float y,int parentw,int parenth)
	{
		end.x=x;
		end.y=y;
		rect.set(begin.x, begin.y, end.x, end.y);
		path.reset();
		path.addRect(rect, Path.Direction.CW);
		path.close();
		Log.v("Rect PointUp"," finished=t drawing=f Line("+ begin.x+"," + begin.y + ") to ("+ end.x+"," + end.y + ")");
		setFinished(true);
		setDrawing(false);
	} 

}