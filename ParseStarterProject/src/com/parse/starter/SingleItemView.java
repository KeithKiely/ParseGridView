package com.parse.starter;

/**
 * Created by Keith on 21/03/2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class SingleItemView extends Activity {

    String image;
    ImageLoader imageLoader = new ImageLoader(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.singleitemview);

        Intent i = getIntent();
        // Get the intent from ListViewAdapter
        image = i.getStringExtra("image");

        // Locate the ImageView in singleitemview.xml
        ImageView img = (ImageView) findViewById(R.id.image);

        // Load image into the ImageView
        imageLoader.DisplayImage(image, img);
    }
}
