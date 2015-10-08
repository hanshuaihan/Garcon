package com.example.paintm;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TestView extends View {
	private List<String> mShapeNames = Arrays.asList("Line", "Circle",
			"Rectangle", "FreeHand", "Oval");
	float L, T;
	private View thisView;
	private int brushWidth = 1;
	private Paint paint;
	private int W, H;
	private int bgcolor;
	private Paint mPaint = new Paint();
	private Class nowClass;
	private Shape nowShape;
	int fill_tolerance;
	int High_Tolerance = 0;
	int Low_Tolerance = 1;
	Bitmap fill_bitmap;
	private boolean isPainted;
	public boolean isFilling;
	private int colorNow, alphaNow;
	private float widthNow;
	private PathPackage[] op;
	private int oi, on, pon; /*
							 * oi: now pathpackage on: max pathpackage pon: sum
							 * of paths / pathpackage
							 */

	float x_down = 0;
	float y_down = 0;
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	float oldRotation = 0;
	Matrix matrix = new Matrix();
	Matrix matrix1 = new Matrix();
	Matrix savedMatrix = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	int mode = NONE;

	boolean matrixCheck = false;

	int widthScreen;
	int heightScreen;

	Bitmap gintama;

	boolean zoom;

	public void init() {/*
						 * 
						 * DisplayMetrics dm = new DisplayMetrics();
						 * getWindowManager
						 * ().getDefaultDisplay().getMetrics(dm); widthScreen =
						 * dm.widthPixels; heightScreen = dm.heightPixels;
						 */

		matrix = new Matrix();
	}

	public void initPaint() {
		paint.setDither(true);
		paint.setColor(Color.rgb(100, 100, 100));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(brushWidth);
	}

	public TestView(Context context) {
		super(context);
		isPainted = false;
		isFilling = false;
		fill_tolerance = High_Tolerance;
		thisView = this;
		paint = new Paint();
		int[] l = new int[2];
		initPaint();
		zoom = false;
		W = this.getWidth();
		H = this.getHeight();
		this.getLocationInWindow(l);
		bgcolor = Color.WHITE;
		colorNow = Color.BLACK;
		widthNow = 5.0f;
		alphaNow = 255;
		L = l[0];
		T = l[1];
		Log.v("TestView", "Init");
		on = 15;
		pon = 30;
		op = new PathPackage[on];

		benew();
		Log.v("TestView", "Inited");
	}

	public int getBGColor() {
		return bgcolor;
	}

	public int getColorNow() {
		return colorNow;
	}

	public int getAlphaNow() {
		return alphaNow;
	}

	public float getWidthNow() {
		return widthNow;
	}

	public boolean getIsPainted() {
		return isPainted;
	}

	/*
	 * public boolean getIsFilling(){ return isFilling; }
	 * 
	 * public void setIsFilling(boolean value) { isFilling = value; }
	 */

	public boolean getTolerance() {
		return isFilling;
	}

	public void setTolerance(int value) {
		fill_tolerance = value;
	}

	public void setBGColor(int v) {
		bgcolor = v;
	}

	public void setColorNow(int v) {
		colorNow = v;
	}

	public void setAlphaNow(int v) {
		alphaNow = v;
	}

	public void setWidthNow(float v) {
		widthNow = v;
	}

	public void setIsPainted(boolean b) {
		isPainted = b;
	}

	public void back() {
		op[oi].back();
		this.postInvalidate();
	}

	public void forward() {
		op[oi].forward();
		this.postInvalidate();
	}

	public void addZeroPath() {
		op[oi].addPath(new Path(), 0, 0, 0);// Add Zero
	}
	public void cM() {
		op[0].setNext(null);
		op[1].setPrev(null);
	}

	public void benew() {
		for (int i = 0; i < on; i++)
			op[i] = null;
		op[0] = new PathPackage(thisView, pon);
		op[0].addPath(new Path(), 0, 0, 0);// Add Zero
		op[0].setOnSwitchPackageListener(myspl);
		oi = 0;
		op[0].setOnPackageFullListener(mypfl);
		isPainted = false;
		this.postInvalidate();

	}

	@Override
	public void onSizeChanged(int w, int h, int ow, int oh) {
		W = w;
		H = h;
		int[] l = new int[2];
		this.getLocationInWindow(l);
		L = l[0];
		T = l[1];
		for (int i = 0; i < op.length; i++)
			if (op[i] != null)
				op[i].parentResize();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// boolean value = super.onTouchEvent(event);
		if (zoom) {
			gintama = getNowBitmap();
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				mode = DRAG;
				x_down = event.getX();
				y_down = event.getY();
				savedMatrix.set(matrix);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				mode = ZOOM;
				oldDist = spacing(event);
				oldRotation = rotation(event);
				savedMatrix.set(matrix);
				midPoint(mid, event);
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == ZOOM) {
					matrix1.set(savedMatrix);
					float rotation = rotation(event) - oldRotation;
					float newDist = spacing(event);
					float scale = newDist / oldDist;
					matrix1.postScale(scale, scale, mid.x, mid.y);// 縮放
					matrix1.postRotate(rotation, mid.x, mid.y);// 旋轉
					matrixCheck = matrixCheck();
					if (matrixCheck == false) {
						matrix.set(matrix1);
						invalidate();
					}
				} else if (mode == DRAG) {
					matrix1.set(savedMatrix);
					matrix1.postTranslate(event.getX() - x_down, event.getY()
							- y_down);// 平移
					matrixCheck = matrixCheck();
					matrixCheck = matrixCheck();
					if (matrixCheck == false) {
						matrix.set(matrix1);
						invalidate();
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				// zoom = !zoom;
				// gintama = getNowBitmap();
				// break;
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				break;
			}
			return true;
		} else {
			isPainted = true;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				if (nowShape == null || nowShape.isFinished())
					if (nowClass != null) {
						Log.v("down", "newShape");
						try {
							nowShape = (Shape) nowClass.newInstance();
							nowShape.setColor(colorNow);
							nowShape.setStrokeWidth(widthNow);
							nowShape.setAlpha(alphaNow);

							if (on > oi + 1)
								on = oi + 1;
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						nowShape.startDrawing();
					} else
						break;
				float x = event.getX();
				float y = event.getY();
				// x-=L;
				// y-=T;
				nowShape.PointDown(x, y, W, H);
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				if (nowShape != null && nowShape.isDrawing()) {
					float x = event.getX();
					float y = event.getY();
					// x-=L;
					// y-=T;
					nowShape.PointMove(x, y, W, H);
				}
				break;
			}
			case MotionEvent.ACTION_UP: {

				if (isFilling) {
					/*
					 * while (figures.size() > 0) {
					 * figuresToFlat.add(figures.remove(0)); }
					 */

					Bitmap bitmap = Bitmap.createBitmap(getWidth(),
							getHeight(), Bitmap.Config.ARGB_8888);
					draw(new Canvas(bitmap));
					// this.bitmap = bitmap;

					// figuresToFlat.clear();

					FloodFiller ff = new FloodFiller(bitmap, colorNow,
							fill_tolerance);
					ff.floodFill(new Point((int) event.getX(), (int) event
							.getY()));
					// fill_bitmap = ff.getBitmap();
					benew();
					setNowBitmap(ff.getBitmap());
					addZeroPath();
					cM();
					isFilling = false;
				} else {
					if (nowShape != null && nowShape.isDrawing()) {
						float x = event.getX();
						float y = event.getY();
						// x-=L;
						// y-=T;
						nowShape.PointUp(x, y, W, H);

						if (nowShape.isFinished())
							op[oi].addPath(nowShape.getPath(),
									nowShape.getColor(),
									nowShape.getStrokeWidth(),
									nowShape.getAlpha());

						Log.v("draw", "up at(" + x + "," + y + ") ");
					} else if (nowShape == null)
						Log.v("draw", "up no nowshape");
					else
						Log.v("draw", "up not nowshape.drawing");

					int[] l = new int[2];
					this.getLocationInWindow(l);
					L = l[0];
					T = l[1];
				}
			}
				break;
			default:
				return true;
			}
			this.postInvalidate();
			return true;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (zoom) {
			canvas.save();
			canvas.drawBitmap(gintama, matrix, null);
			canvas.restore();
		} else {
			// 清空画布
			if (isFilling && fill_bitmap != null) {
				canvas.drawBitmap(fill_bitmap, 0, 0, null);
				isFilling = false;
			} else {
				Paint p = new Paint(), mPaint = new Paint();
				;
				p.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
				canvas.drawPaint(p);
				p.setXfermode(new PorterDuffXfermode(Mode.SRC));
				// end 清空画布
				canvas.drawColor(bgcolor);
				Bitmap lastBuf = null;

				for (int j = 0; j <= oi; j++) {
					if (op[j].isBuffered())
						lastBuf = op[j].getBuffer();
					else {
						if (lastBuf != null) {
							canvas.drawBitmap(lastBuf, 0, 0, mPaint);
							lastBuf = null;
						}
						if (op[j].isMustBuffer() && op[j].isBuffered()) {
							canvas.drawBitmap(op[j].getBuffer(), 0, 0, mPaint);
							Log.v("ondraw", "MustBuffer");
						} else
							for (int i = 0; i < op[j].getI(); i++) {
								paint.setColor(op[j].getColor(i));
								paint.setAlpha(op[j].getAlpha(i));
								paint.setStrokeWidth(op[j].getWidth(i));
								canvas.drawPath(op[j].getPath(i), paint);
							}
					}
				}
				if (lastBuf != null)
					canvas.drawBitmap(lastBuf, 0, 0, mPaint);
				if (nowShape != null && !nowShape.isFinished()
						&& nowShape.isDrawing()) {
					paint.setColor(nowShape.getColor());
					paint.setStrokeWidth(nowShape.getStrokeWidth());
					canvas.drawPath(nowShape.getPath(), paint);// 这里getPath
																// 获得Shape.tmppath
				}
			}
		}
	}

	public Class getShapeClassByName(String name) {
		try {
			for (String t : mShapeNames)
				if (name.equals(t))
					return Class.forName(Shape.class.getPackage().getName()
							+ "." + t);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		return null;
	}

	public void setShape(Class cc) {
		nowClass = cc;
		if (nowShape != null)
			nowShape.endDrawing();
		nowShape = null;
	}

	public void setShapeByName(String name) {
		Class c = getShapeClassByName(name);
		if (c != null)
			setShape(c);
	}

	public Bitmap getNowBitmap() {
		Paint mpaint = new Paint();
		Paint p = new Paint();
		Bitmap lastBuf = null, b = Bitmap.createBitmap(this.getWidth(),
				this.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(b);

		p.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		canvas.drawPaint(p);
		p.setXfermode(new PorterDuffXfermode(Mode.SRC));
		canvas.drawColor(bgcolor);

		for (int j = 0; j <= oi; j++) {
			if (op[j].isBuffered())
				lastBuf = op[j].getBuffer();
			else {
				if (lastBuf != null) {
					canvas.drawBitmap(lastBuf, 0, 0, mPaint);
					lastBuf = null;
				}
				if (op[j].isMustBuffer() && op[j].isBuffered())
					canvas.drawBitmap(op[j].getBuffer(), 0, 0, mPaint);
				else
					for (int i = 0; i < op[j].getI(); i++) {
						paint.setColor(op[j].getColor(i));
						paint.setAlpha(op[j].getAlpha(i));
						paint.setStrokeWidth(op[j].getWidth(i));
						canvas.drawPath(op[j].getPath(i), paint);
					}
			}
		}
		if (lastBuf != null)
			canvas.drawBitmap(lastBuf, 0, 0, mPaint);
		return b;
	}

	public void setNowBitmap(Bitmap b) {
		if ((op[oi].getI() > 0 && oi > 0) || op[oi].getI() > 1) {
			op[oi].setFullSize(op[oi].getI());
			OnPackageFullListener f = op[oi].getOnPackageFullListener();
			OnSwitchPackageListener s = op[oi].getOnSwitchPackageListener();
			oi++;
			op[oi] = new BackGroundPathPackage(this, b);
			op[oi - 1].setNext(op[oi]);
			op[oi].setPrev(op[oi - 1]);
			op[oi].setOnPackageFullListener(f);
			op[oi].setOnSwitchPackageListener(s);
		} else {
			OnPackageFullListener f = op[oi].getOnPackageFullListener();
			OnSwitchPackageListener s = op[oi].getOnSwitchPackageListener();
			PathPackage n = op[oi].getNext(), p = op[oi].getPrev();
			op[oi] = new BackGroundPathPackage(this, b);
			op[oi].setOnPackageFullListener(f);
			op[oi].setOnSwitchPackageListener(s);
			op[oi].setNext(n);
			op[oi].setPrev(p);
			Log.v("setNowBmp", "2");
		}
		this.postInvalidate();
	}

	class MySPL implements OnSwitchPackageListener {
		public PathPackage onChagePackage(boolean next, PathPackage to) {

			if (next == true) {
				if (oi < on - 1) {
					oi++;
					return op[oi];
				}
			} else {
				if (oi > 0) {
					oi--;
					return op[oi];
				}
			}
			return null;
		}
	}

	MySPL myspl = new MySPL();

	class MyPFL implements OnPackageFullListener {
		@Override
		public PathPackage onPackageFull() {
			Log.v("PackageFull", "Add One");
			// TODO Auto-generated method stub
			oi++;
			op[oi] = new PathPackage(thisView, 100, op[oi - 1],
					op[oi - 1].isBuffered() ? op[oi - 1].getBuffer() : null);
			op[oi - 1].setNext(op[oi]);
			op[oi].setOnSwitchPackageListener(myspl);
			if (oi < on - 1)
				op[oi].setOnPackageFullListener(mypfl);
			else
				op[oi].setOnPackageFullListener(new OnPackageFullListener() {
					@Override
					public PathPackage onPackageFull() {
						Log.v("PackageFull", "TooMuch");
						return null;
					}
				});
			return op[oi];
		}
	}

	MyPFL mypfl = new MyPFL();

	private boolean matrixCheck() {
		float[] f = new float[9];
		matrix1.getValues(f);
		// 图片4个顶点的坐标
		float x1 = f[0] * 0 + f[1] * 0 + f[2];
		float y1 = f[3] * 0 + f[4] * 0 + f[5];
		float x2 = f[0] * gintama.getWidth() + f[1] * 0 + f[2];
		float y2 = f[3] * gintama.getWidth() + f[4] * 0 + f[5];
		float x3 = f[0] * 0 + f[1] * gintama.getHeight() + f[2];
		float y3 = f[3] * 0 + f[4] * gintama.getHeight() + f[5];
		float x4 = f[0] * gintama.getWidth() + f[1] * gintama.getHeight()
				+ f[2];
		float y4 = f[3] * gintama.getWidth() + f[4] * gintama.getHeight()
				+ f[5];
		// 图片现宽度
		double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
		// 缩放比率判断
		if (width < widthScreen / 3 || width > widthScreen * 3) {
			return true;
		}
		// 出界判断
		if ((x1 < widthScreen / 3 && x2 < widthScreen / 3
				&& x3 < widthScreen / 3 && x4 < widthScreen / 3)
				|| (x1 > widthScreen * 2 / 3 && x2 > widthScreen * 2 / 3
						&& x3 > widthScreen * 2 / 3 && x4 > widthScreen * 2 / 3)
				|| (y1 < heightScreen / 3 && y2 < heightScreen / 3
						&& y3 < heightScreen / 3 && y4 < heightScreen / 3)
				|| (y1 > heightScreen * 2 / 3 && y2 > heightScreen * 2 / 3
						&& y3 > heightScreen * 2 / 3 && y4 > heightScreen * 2 / 3)) {
			return true;
		}
		return false;
	}

	// 触碰两点间距离
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	// 取手势中心点
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// 取旋转角度
	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	// 将移动，缩放以及旋转后的图层保存为新图片
	// 本例中沒有用到該方法，需要保存圖片的可以參考
	public Bitmap CreatNewPhoto() {
		Bitmap bitmap = Bitmap.createBitmap(widthScreen, heightScreen,
				Config.ARGB_8888); // 背景图片
		Canvas canvas = new Canvas(bitmap); // 新建画布
		canvas.drawBitmap(gintama, matrix, null); // 画图片
		canvas.save(Canvas.ALL_SAVE_FLAG); // 保存画布
		canvas.restore();
		return bitmap;
	}
}
