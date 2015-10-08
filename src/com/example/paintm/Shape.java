package com.example.paintm;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;

public class Shape extends Object implements Parcelable{ 
			//private Paint paint;
			protected Path path,tmppath;
			protected float strokeWidth;
			private int color;
			private int alpha;
			private boolean finished,drawing;
	    	
			public Shape(){
	    		//paint = new Paint();
				finished=false;
	    		path = new Path();
	    		tmppath=new Path();
	    		strokeWidth = 5;
	    		color = Color.BLACK;
	    		alpha = 255;
	    	}   
	    	public void PointDown(float x,float y,int parentw,int parenth)
	    	{
	    		
	    	}
	    	
	    	public int PointMove(float x,float y,int parentw,int parenth)
	    	{
	    		return 0;
	    	}
	    	public void PointUp(float x,float y,int parentw,int parenth)
	    	{
	    		
	    	}
	    	public void endDrawing()
	    	{ 
	    		setDrawing(false); 
	    	}
	    	public void startDrawing()
	    	{
	    		clear();
	    		finished=false; 
	    		setDrawing(true); 
	    	}
	    	public boolean isDrawing()
	    	{
	    		return drawing;
	    	}
	    	public void setDrawing(boolean d)
	    	{
	    		drawing=d;
	    	}
	    	public void setFinished(boolean d)
	    	{
	    		finished=d;
	    	}
	    	public boolean isFinished()
	    	{
	    		return finished;
	    	}
	    	public void clear()
	    	{ 	

	    	}
	    	public Path getPath()
	    	{
	    		if(isDrawing())
	    			return tmppath;
	    		else 
	    			return path;
	    	}
	    	
	    	public void setPath(Path p){
	    		path = p;
	    	}
	    	
	    	public int getAlpha() {
	    		return alpha;
	    	}
	    	
	    	public void setAlpha(int n_alpha) {
	    		alpha = n_alpha;
	    	}
	    	public int getColor() {
	    		return color;
	    	}
	    	
	    	public void setColor(int setedColor) {
	    		color = setedColor;
	    	}
	    	
	    	public void setStrokeWidth(float w){
	    		strokeWidth = w;
	    	}
	    	
	    	public float getStrokeWidth(){
	    		return strokeWidth;
	    	}


			@Override
			public int describeContents() {
				// TODO Auto-generated method stub
				return 0;
			}


			@Override
			public void writeToParcel(Parcel out, int flag) {
				// TODO Auto-generated method stub
				out.writeValue(path);
			}
}