package com.example.paintm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements SensorEventListener {
	final static String logTag = "mainActivity";
	int colorNow, bgColorNow, alphaNow;
	float widthNow;
	int High_Tolerance = 0;
	int Low_Tolerance = 1;
	private Activity thisactivity;
	boolean isSaved,isIn;
	boolean isTrans;
	Bitmap bitmap_trans;
	SeekBar sb;
	ToggleButton tbtn1;
	Button btn_brush, btn_back, btn_forward, btn_save, btn_new, btn_rubber,
			btn_open, btn_color, btn_zoom, btn_fill,btn_share,btn_wp, btn_trans, btn_trans_stop;
	RelativeLayout tools;
	ViewGroup pt;
	TestView tv;
	ListView listView1;

	Animation anim_in, anim_out, anim_fout, anim_hide, anim_fin;
	boolean rubbering;
	Bitmap lb;

	SensorManager sensorManager = null;
	//Vibrator vibrator = null;
	int shake_times;

	public MainActivity() {
		colorNow = Color.BLACK;
		bgColorNow = Color.WHITE;
		widthNow = 5.0f;
		alphaNow = 255;
		rubbering = false;
		lb = null;
		isIn=false;
		bitmap_trans = null;
	}

	private void fillpaint() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(getResources().getText(R.string.fill));

		String[] items = new String[] {
				getResources().getText(R.string.high_tolerance).toString(),
				getResources().getText(R.string.low_tolerance).toString() };

		alertDialogBuilder.setItems(items,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) {
							tv.setTolerance(High_Tolerance);
						} else {
							tv.setTolerance(Low_Tolerance);
						}/*
						 * 
						 * paintView.setFigure(Figures.FILL);
						 * paintView.setColor(lastColor);
						 * paintView.setSize(lastSize);
						 * button_color.setEnabled(true);
						 * 
						 * doFocus(button_fill);
						 */
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitymain/* new TestView(this) */);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		//vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		shake_times = 0;
		thisactivity = this;

		pt = (ViewGroup) findViewById(R.id.prant);

		tv = new TestView(this);// (TestView)findViewById(R.id.testview);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		tv.widthScreen = dm.widthPixels;
		tv.heightScreen = dm.heightPixels;

		pt.addView(tv);
		isSaved = false;
		isTrans = false;
		
		sb = (SeekBar)findViewById(R.id.adj);
		// 设置拖动条改变监听器  
        OnSeekBarChangeListener osbcl = new  OnSeekBarChangeListener() { 
 
            @Override  
            public   void  onProgressChanged(SeekBar seekBar,  int  progress, 
                    boolean  fromUser) { 
                //tv1.setText("当前进度："  + sb.getProgress()); 
            } 
 
            @Override  
            public   void  onStartTrackingTouch(SeekBar seekBar) { 
                Toast.makeText(getApplicationContext(), "onStartTrackingTouch" , 
                        Toast.LENGTH_SHORT).show(); 
            } 
 
            @Override  
            public   void  onStopTrackingTouch(SeekBar seekBar) {
            	if(bitmap_trans != null)
            		trans(bitmap_trans, sb.getProgress());
            } 
 
        }; 
        // 为拖动条绑定监听器  
        sb.setOnSeekBarChangeListener(osbcl);

		tbtn1 = (ToggleButton) findViewById(R.id.toggleButton2);
		tools = (RelativeLayout) findViewById(R.id.tools);
		btn_color = (Button) findViewById(R.id.button_color);
		btn_rubber = (Button) findViewById(R.id.button_rubber);
		btn_new = (Button) findViewById(R.id.button_new);
		btn_save = (Button) findViewById(R.id.button_save);
		btn_brush = (Button) findViewById(R.id.button_brush);
		btn_back = (Button) findViewById(R.id.button_back);
		btn_forward = (Button) findViewById(R.id.button_forward);
		btn_share=(Button)findViewById(R.id.button_share);
		btn_open = (Button) findViewById(R.id.button_open);
		btn_fill = (Button) findViewById(R.id.button_fill);
		btn_wp = (Button) findViewById(R.id.button_wallpaper);
		btn_trans = (Button) findViewById(R.id.button_trans);
		anim_in = AnimationUtils.loadAnimation(this, R.anim.gradually_in);
		anim_out = AnimationUtils.loadAnimation(this, R.anim.gradually_out);
		anim_fout = AnimationUtils.loadAnimation(this, R.anim.fast_out);
		anim_fin = AnimationUtils.loadAnimation(this, R.anim.fast_in);
		anim_hide = AnimationUtils.loadAnimation(this, R.anim.hide);
		listView1 = (ListView) findViewById(R.id.listView1);
		tools.startAnimation(anim_hide);

		
		btn_zoom = (Button) findViewById(R.id.button_zoom);
		btn_zoom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				tv.zoom = !tv.zoom;
			}
		});

		btn_fill.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				tv.isFilling = !tv.isFilling;
				if (tv.isFilling) {
					fillpaint();
				}
			}
		});
btn_trans.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				isTrans = !isTrans;
				if(isTrans)
					adjust();
				else {
					bitmap_trans = null;
					sb.setVisibility(View.INVISIBLE);
				}
			}
		});
		btn_share.setOnClickListener(new OnClickListener() { 
			public void onClick(View arg0) {
				  Intent intent=new Intent(Intent.ACTION_SEND);  
				  Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), tv.getNowBitmap(), null,null));
			     
			      intent.setType("image/*");  
			      intent.putExtra(Intent.EXTRA_STREAM,uri);
			      startActivity(Intent.createChooser(intent, getTitle()));  	
			}
		});

		listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				HashMap<String, Object> map = (HashMap<String, Object>) listView
						.getItemAtPosition(position);
				if (rubbering) {
					tv.setColorNow(colorNow);
					tv.setWidthNow(widthNow);
					rubbering = false;
				}
				tv.setShapeByName((String) map.get("name"));
			}
		});
		refData();
		anim_fout.setAnimationListener(myal);
		anim_fin.setAnimationListener(myal); 
		// Set up the user interaction to manually show or hide the system UI.
		tbtn1.setOnClickListener(new View.OnClickListener() { 
			@Override
			public void onClick(View view) {
				if (tbtn1.isChecked()) {
					tools.startAnimation(anim_fin); 
				} else {  
					tools.startAnimation(anim_fout);
				}
			}
		});
		btn_brush.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (rubbering) {
					tv.setColorNow(colorNow);
					tv.setWidthNow(widthNow);
					rubbering = false;
				}
				tv.setShapeByName("FreeHand");
			}
		});
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				tv.back();
			}
		});
		btn_forward.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				tv.forward();
			}
		});
		btn_new.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!isSaved && tv.getIsPainted()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setMessage("新建之前保证当前图片？")
							.setCancelable(false)
							.setPositiveButton("保存",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											getAndSavePic();
										}
									})
							.setNegativeButton("不保存",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											tv.benew();
											tv.addZeroPath();
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				} else {
					isSaved = false;
				}
				tv.benew();
				Toast.makeText(thisactivity, "新建成功！", 4).show();
			}
		});
		btn_rubber.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				tv.setWidthNow(35.0f);
				tv.setColorNow(bgColorNow);
				rubbering = true;
				tv.setShapeByName("FreeHand");
			}
		});
		btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getAndSavePic();
				isSaved = true;
			}
		});
		btn_open.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openPic();
				Toast.makeText(thisactivity, "打开成功！", 4).show();
			}
		});
		btn_color.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// Toast.makeText(thisactivity, "Add Function Here", 4).show();

				ColorPickerDialog dialog = new ColorPickerDialog(tv
						.getColorNow(), tv.getAlphaNow(), tv.getWidthNow(),
						MainActivity.this, tv.getColorNow(), getResources()
								.getString(R.string.btn_color_picker),
						new ColorPickerDialog.OnColorChangedListener() {

							@Override
							public void colorChanged(int ncolor) {
								tv.setColorNow(ncolor);
							}

							@Override
							public void alphaChanged(int nalpha) {
								tv.setAlphaNow(255 - nalpha);
							}

							@Override
							public void sizeChanged(int nsize) {
								tv.setWidthNow(nsize);
							}
						});
				dialog.show();
			}
		});
		btn_wp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				WallpaperManager wallpaperManager = WallpaperManager.getInstance(thisactivity);   
			 Log.v("onclick","button wallpaper");
				      
				try {
					wallpaperManager.setBitmap(tv.getNowBitmap());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   
				 Toast.makeText(thisactivity, "Success!", 4000).show();
			}
		});
		tv.setShapeByName("FreeHand");

		return;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// 当传感器精度改变时回调该方法，Do nothing.
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Log.d("sensor","sensor changed");
		int sensorType = event.sensor.getType();
		// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
		float[] values = event.values;
		//Log.d("sensor","sensor changed  out "+shake_times);
		if (sensorType == Sensor.TYPE_ACCELEROMETER 
				&& (Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math.abs(values[2]) > 17)) {/*
			Log.d("sensor","sensor changed in " + shake_times);
			shake_times ++;
			if(shake_times == 1)
				Toast.makeText(thisactivity, "请再次摇晃手机以确认清除画面", 5).show();
			else if(shake_times >= 2) {
				tv.benew();
				tv.addZeroPath();
				Toast.makeText(thisactivity, "清除画面成功", 5).show();
				shake_times = 0;
			}*/
			shake_times ++;
			if(shake_times == 1) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setMessage(getResources().getText(R.string.shake))
						.setCancelable(false)
						.setPositiveButton(
								getResources().getText(R.string.shake_yes),
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog, int id) {
										tv.benew();
										tv.addZeroPath();
										shake_times = 0;
									}
								})
						.setNegativeButton(getResources().getText(R.string.shake_no),
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog, int id) {
										shake_times = 0;
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
					alert.show();
					// 摇动手机后，再伴随震动提示~~
					//vibrator.vibrate(500);
			}
		}
	}

	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		 super.onPrepareOptionsMenu(menu);
			menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.trans_stop:
			isTrans = false;
			bitmap_trans = null;
			sb.setVisibility(View.INVISIBLE);
			break;
		case R.id.trans:
	        adjust();
	        break;
		}
		return super.onOptionsItemSelected(item);
	}*/

	public void trans(Bitmap img1, int adj) {
		long current = System.currentTimeMillis();
        //Bitmap img1 = tv.getNowBitmap();
        int w = img1.getWidth(), h = img1.getHeight();
        int[] pix = new int[w * h];
        img1.getPixels(pix, 0, w, 0, 0, w, h);
        int[] resultInt = LibImgFun.ImgFun(pix, w, h, adj);
        Bitmap resultImg = Bitmap.createBitmap(w, h, Config.RGB_565);
        resultImg.setPixels(resultInt, 0, w, 0, 0, w, h);
        long performance = System.currentTimeMillis() - current;
        tv.benew();
        tv.setNowBitmap(resultImg);
        tv.addZeroPath();
        tv.cM();
        /*//imgView.setImageBitmap(resultImg);
        MainActivity.this.setTitle("w:" + String.valueOf(img1.getWidth())
            + ",h:" + String.valueOf(img1.getHeight()) + "NDK耗时"
            + String.valueOf(performance) + " 毫秒");*/
	}
	public void adjust() {
		bitmap_trans = tv.getNowBitmap();
		sb.setVisibility(View.VISIBLE);
	}
	public void getAndSavePic() {
		//
		// Bitmap b=tv.getNowBitmap(); 没用需调试
		//
		tv.setIsPainted(false);
		Bitmap b = null;

		/*
		 * {// 截屏 tbtn1.setVisibility(View.INVISIBLE);
		 * tools.setVisibility(View.INVISIBLE); tools.startAnimation(anim_hide);
		 * b=MonkeyFunc.shootPic(getWindow().getDecorView(),(View)tv);
		 * if(tbtn1.isChecked() ) { tools.setVisibility(View.VISIBLE);
		 * tools.startAnimation(anim_fin); } tbtn1.setVisibility(View.VISIBLE);
		 * }
		 */

		b = tv.getNowBitmap();
		String sdPath = MonkeyFunc.getSDPath(), fn = "";
		if (sdPath != null && b != null) {
			fn = sdPath + "/PaintM/";
			MonkeyFunc.makeFolder(fn);
			fn = fn + "save_" + MonkeyFunc.getDateTime("yy-MM-dd_HH-mm-ss")
					+ ".png";
			MonkeyFunc.saveMyBitmap(fn, b);
		}
		Toast.makeText(thisactivity, "成功保存到" + fn, 5).show();
	}

	private final static int RESULT_TAKE_PHOTO = 1, RESULT_LOAD_IMAGE = 2;

	protected void takePic() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, RESULT_TAKE_PHOTO);
	}

	protected void openPic() {

		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_OK == resultCode) {
			switch (requestCode) {
			case RESULT_TAKE_PHOTO:// 暂无调用photo的功能。
				try {
					super.onActivityResult(requestCode, resultCode, data);
					Bundle extras = data.getExtras();
					Bitmap b = null;
					if (extras != null) {
						b = (Bitmap) extras.get("data");
						if (b != null) {
							// img1.setImageBitmap(b);
							// lb = b;
							lb = zoomImg(b, tv.getWidth(), tv.getHeight());
						}
					}
				} catch (Exception e) {
					Log.v(logTag, e.getMessage());
				}
				break;
			case RESULT_LOAD_IMAGE:
				try {
					Uri selectedImage = data.getData();
					Bitmap b = MonkeyFunc.getRoatedImage(selectedImage,
							this.getContentResolver());
					// img1.setImageBitmap(b);
					lb = zoomImg(b, tv.getWidth(), tv.getHeight());
					// lb = b;
					Log.v("ImageLoaded", selectedImage.toString());

					tv.benew();
					tv.setNowBitmap(lb);
					tv.addZeroPath();
					tv.cM();
				} catch (Exception e) {
					// TODO: handle exception
					Log.v(logTag, e.getMessage());
				}

				break;
			default:
			}

		}
	}

	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}

	public void refData() {
		SimpleAdapter adapter = new SimpleAdapter(this, getData(),
				R.layout.listviewitem, new String[] { "img" },
				new int[] { R.id.img });
		listView1.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("img", R.drawable.line);
		map.put("name", "Line");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("img", R.drawable.rectangle);
		map.put("name", "Rectangle");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("img", R.drawable.circle);
		map.put("name", "Circle");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("img", R.drawable.oval);
		map.put("name", "Oval");
		list.add(map);
		return list;

	}

	
	class Myal implements AnimationListener  {

		@Override
		public void onAnimationStart(Animation arg0) {
			// TODO Auto-generated method stub
			if(tbtn1.isChecked()){
				tools.setVisibility(View.VISIBLE);
			}else{ 
			}
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
			// TODO Auto-generated method stub
			;
		}

		@Override
		public void onAnimationEnd(Animation arg0) {
			// TODO Auto-generated method stub
			if(tbtn1.isChecked()){
				tools.clearAnimation();
				tools.setVisibility(View.VISIBLE);
			}else{
				tools.clearAnimation();
				tools.setVisibility(View.GONE);
			}
		}
	}
	Myal myal=new Myal();
}
