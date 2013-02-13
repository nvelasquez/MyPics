package edu.itla.imageswitcher;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class PresentationActivity extends Activity{

	private Cursor cur;
	private int columnIndex;
    ViewFlipper vf;
	
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        
        vf = (ViewFlipper) findViewById(R.id.flipper1);
        //Fields of the resources
        String[] projection = new String[]{
        		MediaStore.Images.Thumbnails._ID
        };
        
        
        //Get the uri to find resources        
        Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        //Creating a cursor with the resources.
        cur = managedQuery(uri, projection, null, null, MediaStore.Images.Thumbnails._ID);
        //getting a column index to look for the resource
        columnIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
        int imageID = 0;
        
        if(cur.getCount() > 0){
        	
        	cur.moveToFirst();
        	
        	for(int a =0; a < cur.getCount(); a++){
        		
        		imageID = cur.getInt(columnIndex);
        		
        		if(!(Integer.toString(imageID).equalsIgnoreCase(""))){
        			
        			//Create an imageView and set the bitmap decoding the uri.
	        		ImageView lastImg = new ImageView(this);
	        		
	        		/**********setting the image size************/
	        		try {
						lastImg.setImageBitmap(
								decodeUri(
										Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, ""+imageID))
								);
					} catch (FileNotFoundException e) {
					
						e.printStackTrace();
					}
	        		
	        		lastImg.setScaleType(ImageView.ScaleType.FIT_XY);
	        		
	        		vf.addView(lastImg);
	        		cur.moveToNext();
	        	}
        	}

        	vf.setAutoStart(true);        	
        }
    }
    
    public void previous(View v) {
    	vf.showPrevious();
    }
    
    public void next(View v){
    	vf.showNext();
    }
    
    public void pause(View v){
    	vf.stopFlipping();
    }
    
    public void play(View v){
    	vf.startFlipping();
    }
 
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        //Open an input stream from the content resolver and passing as parameter the uri with the image
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
               || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_presentation, menu);
        return true;
    }
  
}
