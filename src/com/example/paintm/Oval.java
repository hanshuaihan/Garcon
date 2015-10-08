package com.example.paintm;
 
import android.os.Parcelable;
import android.util.Log;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
public class Oval extends Shape
{ 
	private PointF begin;
    private PointF end; 

	RectF rect = new RectF();
    public Oval()
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
		tmppath.addOval(rect, Path.Direction.CW);
		tmppath.moveTo(begin.x, begin.y); 
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
		path.addOval(rect, Path.Direction.CW);
		path.moveTo(begin.x, begin.y);
		Log.v("Rect PointUp"," finished=t drawing=f Line("+ begin.x+"," + begin.y + ") to ("+ end.x+"," + end.y + ")");
		setFinished(true);
		setDrawing(false);
	} 

}