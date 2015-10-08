package com.example.paintm;
 
import android.os.Parcelable;
import android.util.Log;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
public class Circle extends Shape
{ 
	private PointF begin;
    private PointF end; 
    
    public Circle()
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
		float radius=new PointF(begin.x-end.x,begin.y-end.y).length();
		tmppath.reset();
		tmppath.addCircle(begin.x, begin.y,radius , Path.Direction.CW) ;
		tmppath.moveTo(begin.x, begin.y);
		tmppath.lineTo( end.x,end.y);
		tmppath.close();
		return 1;
	}
	@Override
	public void PointUp(float x,float y,int parentw,int parenth)
	{
		end.x=x;
		end.y=y;
		float radius=new PointF(begin.x-end.x,begin.y-end.y).length();
		path.reset();
		path.addCircle(begin.x, begin.y,radius , Path.Direction.CW) ;
		path.close();
		Log.v("Circle PointUp"," finished=t drawing=f Line("+ begin.x+"," + begin.y + ") to ("+ end.x+"," + end.y + ")");
		setFinished(true);
		setDrawing(false);
	} 

}