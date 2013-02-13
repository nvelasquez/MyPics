package edu.itla.imageswitcher;

import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class ImageSwitcherActivity extends Activity implements ViewFactory{


	private Cursor cur;
	private ImageSwitcher ims;
	private int columnIndex;
	private int imageID;
	private final Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
	private int actionU = 0;
	private int actionD = 0;
	private ImageView imageView;
	private Gallery g;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_switcher);
		System.out.println("this is on create");
		
		
		loadImages();
		columnIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
		
		//Setting tha image switcher		
		ims = (ImageSwitcher) findViewById(R.id.switcher1);
		ims.setFactory(this);
		ims.setInAnimation(AnimationUtils.loadAnimation(ImageSwitcherActivity.this, android.R.anim.fade_in));
		ims.setOutAnimation(AnimationUtils.loadAnimation(ImageSwitcherActivity.this, android.R.anim.fade_out));
		
		//Setting the gallery object.
		final ImageAdapter imgA = new ImageAdapter(this);
		g = (Gallery) findViewById(R.id.gallery1);
		g.setSpacing(1);
		g.setAdapter(imgA);
		g.setBackgroundColor(0xFF000000);	
		
		ims.setOnTouchListener(new OnTouchListener() {
			
			
			@SuppressWarnings("finally")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				System.out.println("this is on ims ontouch listener");
				MotionEvent e1 = MotionEvent.obtain(
					    	SystemClock.uptimeMillis(), 
					    	SystemClock.uptimeMillis(),  
					    	MotionEvent.ACTION_DOWN, 89.333336f, 265.33334f, 0);
					    	
				MotionEvent e2 = MotionEvent.obtain(
					    	SystemClock.uptimeMillis(), 
					    	SystemClock.uptimeMillis(), 
					    	MotionEvent.ACTION_UP, 500.0f, 438.00003f, 0);
				
				boolean move = false;
				
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					actionD = (int) event.getX();
					return true;
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					actionU = (int)event.getX();
					try{
						
						if(actionU - actionD > 100 ){
							g.onFling(e1, e2, 800, 0);
							cur.moveToPosition(g.getSelectedItemPosition()-1);
							move = true;
						}else if(actionD - actionU > 100){						
							g.onFling(e1, e2, -800, 0);
							cur.moveToPosition(g.getSelectedItemPosition()+1);
							move = true;
						}
						if (move){
							ims.setImageURI(Uri.withAppendedPath(uri, ""+cur.getInt(columnIndex)));
						}					
						
					}catch(CursorIndexOutOfBoundsException e){
						Toast.makeText(ImageSwitcherActivity.this, "No existen mas imagenes hacia ese lado", Toast.LENGTH_SHORT).show();
						cur.moveToFirst();						
					}finally{						
						return true;
					}					
				}
				return false;
			}
		});
		
		g.setOnItemClickListener(new OnItemClickListener() {
		
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {	
				System.out.println("this is on gallery on clicklistener");
				cur.moveToPosition(position);
				ims.setInAnimation(AnimationUtils.loadAnimation(ImageSwitcherActivity.this, android.R.anim.slide_in_left));
				ims.setOutAnimation(AnimationUtils.loadAnimation(ImageSwitcherActivity.this, android.R.anim.fade_out));
				ims.setImageURI(Uri.withAppendedPath(uri, ""+cur.getInt(columnIndex)));				
			}
		});
		
		cur.moveToFirst();
		ims.setImageURI(Uri.withAppendedPath(uri, ""+cur.getInt(columnIndex)));
		
	}

	public void play(View v){
		Intent i = new Intent(getApplicationContext(), PresentationActivity.class);
		startActivity(i);
		
	}
	
	private void loadImages(){

		String[] columns = new String[]{
				MediaStore.Images.Thumbnails._ID
		};
		
		cur = managedQuery(uri, columns, null, null, null);		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_image_switcher, menu);
		return true;
	}

	@Override
	public View makeView() {
		ImageView imageView = new ImageView(this);
		imageView.setBackgroundColor(0xFF000000);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT
			));
		//System.out.println("MakeView");
		return imageView;
	}
	
	public class ImageAdapter extends BaseAdapter{
		private Context context;
		private int itemBackground;
		public ImageAdapter(Context c){
			context = c;
			//---setting the style---
			TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
			itemBackground = a.getResourceId(
			R.styleable.Gallery1_android_galleryItemBackground, 0);
			a.recycle();
		}
		//---returns the number of images---
		public int getCount(){
			return cur.getCount();
		}
		//---returns the item---
		public Object getItem(int position){
			return position;
		}
		//---returns the ID of an item---
		public long getItemId(int position){
			return imageID;
		}
		//---returns an ImageView view---
		public Uri getUri(){
			return uri;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			imageView = new ImageView(context);

			cur.moveToPosition(position);			
			Uri uri2 = (Uri.withAppendedPath(uri, ""+cur.getInt(columnIndex)));
			imageView.setImageURI(uri2);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setLayoutParams(new Gallery.LayoutParams(150, 120));
			imageView.setBackgroundResource(itemBackground);

			return imageView;
		}
	}

}
