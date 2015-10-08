package com.example.paintm;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;
 

interface OnSwitchPackageListener{   
	public PathPackage onChagePackage(boolean next, PathPackage to);     
} 
interface OnPackageFullListener{   
	public  PathPackage onPackageFull();     
}
public class PathPackage{ 
	protected PathPackage prev,next,thisPathPackage;
		protected OnSwitchPackageListener spl;
		protected OnPackageFullListener pfl;
		protected boolean buffered ;// background=Lock Buffer && Can't Change 
		protected Bitmap buf;// 
		protected  View par;
		protected Path []ps ;
		protected int[]cs,as;
		protected float [] ws;
		protected int i,size,n;
		protected void initpen(Paint paint)
		{ 
			paint.setDither(true); 
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND); 
		}
		protected void init(int sizep)
		{
			thisPathPackage=this;
			size=sizep;  
			/*par.addOnLayoutChangeListener(new OnLayoutChangeListener() {
				
				@Override
				public void onLayoutChange(View arg0, int arg1, int arg2, int arg3,
						int arg4, int arg5, int arg6, int arg7, int arg8) {
					// TODO Auto-generated method stub
					thisPathPackage.parentResize();
				}
			});*/
			if(buf==null)buf= Bitmap.createBitmap(par.getWidth()>0?par.getWidth():1,par.getHeight()>0?par.getHeight():1,Bitmap.Config.ARGB_8888);
			ps=new Path[size];
			cs=new int[size];
			as=new int [size];
			ws=new float [size];
			i=0;
			buffered=false;
			n=0;
		} 
		public PathPackage( View c,int sizep){
			par=c;
			init(sizep);
		}
		public PathPackage( View c,int sizep,PathPackage previous,Bitmap buffer){
			par=c;
			if(buffer!=null)buf=Bitmap.createBitmap(buffer);
			init(sizep);
			prev=previous; 
		}
		public PathPackage( View c,int sizep,PathPackage previous,PathPackage nextt,Bitmap buffer){
			par=c;
			if(buffer!=null)buf=Bitmap.createBitmap(buffer);
			init(sizep);
			prev=previous;
			next=nextt;
		}

		public void clear()
		{
			if(size<=0)return;
			init(size);
		}
		public int getI()
		{
			return i;
		}
		public int getFullSize()
		{
			return size;
		}
		public Path getPath(int ii)
		{
			if(ii>=0 && ii<i)
				return ps[ii];
			return null;
		}
		public int getColor(int ii)
		{	if(ii>=0 && ii<i)
				return cs[ii];
			return 0;
		}
		public int getAlpha(int ii)
		{	if(ii>=0 && ii<i)
				return as[ii];
			return 0;
		}
		public float getWidth(int ii)
		{
			if(ii>=0 && ii<i)
				return ws[ii];
			return 0;
		}
		public void setFullSize(int siz)
		{
			size=siz;
		}
		public void setPrev(PathPackage p)
		{
			prev=p;
		}
		public PathPackage getPrev()
		{
			return prev;
		}
		public void setNext(PathPackage p)
		{
			next=p;
		}
		public PathPackage getNext()
		{
			return next;
		}
		public void addPath(Path p,int c,float w,int alpha )
		{
			if(i>=size){
				PathPackage pp=null;
				if(pfl!=null){
					Paint pen=new Paint();
					initpen(pen);
					Canvas cv=new Canvas(buf);
					buffered=true;
					for(int j=0;j<size;j++)
					{
						pen.setColor(cs[j]);
						pen.setAlpha(as[j]);
						pen.setStrokeWidth(ws[j]);
						cv.drawPath(ps[j], pen);
					}
					pp=pfl.onPackageFull();
				}
				if(pp!=null)
					pp.addPath(p, c, w,alpha);
				return;
			}
			ps[i]=p;
			cs[i]=c;
			ws[i]=w;
			as[i]=alpha;
			i++;
			n=i;
		}
		public void parentResize()
		{
			buf=Bitmap.createBitmap(par.getWidth()>0?par.getWidth():1,par.getHeight()>0?par.getHeight():1,Bitmap.Config.ARGB_8888);
			buffered=false;
		}
		public boolean isFull()
		{
			return n>=size;
		}
		public boolean isBuffered()
		{
			return buffered;
		}
		public boolean isMustBuffer()
		{
			return false;
		}
		public Bitmap getBuffer()
		{
			return buf;
		}
		public boolean canBack()
		{
			if(i<=1 && (prev==null||spl==null))return false;
			return true; 
		}
		public boolean canForward()
		{
			if(i>=n && (next==null||spl==null))return false;
			return true; 
		}
		public boolean back()
		{
			if(!canBack()){ return false;}
			if(i<=1){
				PathPackage pp=null;
				if(spl!=null){
					pp=spl.onChagePackage(false, prev); 
					if(pp!=null){i=0;/*pp.back();*/return true;}  
				} 
				return false;
			} 
			i--;
			buffered=false;
			return true;
		}
		
		public boolean forward()
		{
			if(!canForward())return false; 
			if(i>=n){
				PathPackage pp=null; 
				if(spl!=null){ 
					pp=spl.onChagePackage(true, next) ; 
					if(pp!=null){pp.forward();return true;}
				} 
				return false;
			}
			i++;
			buffered=false;
			return true;
		}
		 public void setOnSwitchPackageListener(OnSwitchPackageListener sl)
		 {
			 spl=sl; 
		 }
		 public void setOnPackageFullListener(OnPackageFullListener pl)
		 {
			 pfl=pl;
		 }
		 public OnSwitchPackageListener getOnSwitchPackageListener( )
		 {
			 return spl ; 
		 }
		 public OnPackageFullListener getOnPackageFullListener( )
		 {
			 return  pfl ;
		 }
	}











class BackGroundPathPackage extends PathPackage{
	public BackGroundPathPackage( View c,Bitmap buffer){
		super(c, 1);
		buf=buffer;
		buffered=true;
	}
	@Override
	public void clear()
	{
		
	}
	@Override
	public boolean isMustBuffer()
	{
		return true;
	}
	@Override
	public int getI()
	{
		return 0;
	}  
	@Override
	public void addPath(Path p,int c,float w,int a)
	{
			PathPackage pp=null;
			if(pfl!=null){
				pp=pfl.onPackageFull();
			}
			if(pp!=null)
				pp.addPath(p, c, w,a);
			return;
	}
	@Override
	public void parentResize()
	{
		 
	} 
	@Override
	public boolean canBack()
	{
		if( (prev==null||spl==null))return false;
		return true; 
	}
	@Override
	public boolean canForward()
	{
		if( (next==null||spl==null))return false;
		return true; 
	}
	@Override
	public boolean back()
	{
		if(!canBack())return false; 
			PathPackage pp=null;
			if(spl!=null){
				pp=spl.onChagePackage(false, prev) ;
				if(pp!=null){pp.back();return true;}
			}
			return false; 
	}

	@Override
	public boolean forward()
	{
		if(!canForward())return false; 
			PathPackage pp=null;
			if(spl!=null){
				pp=spl.onChagePackage(true, next) ;
				if(pp!=null){pp.forward();return true;}
			}
			return false;  
	} 
}