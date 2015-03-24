package com.parse.starter;
/**
 * Created by Keith
 */
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import com.parse.ParseAnalytics;

public class ParseStarterProjectActivity extends Activity {
        // Declare Variables
        GridView gridview;
        List<ParseObject> ob;
        ProgressDialog mProgressDialog;
        GridViewAdapter adapter;
        private List<ImageList> imageArrayList = null;

        /** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ParseAnalytics.trackAppOpenedInBackground(getIntent());
        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();
	}

    //Inflate Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_image, menu);
        return true;
    }

    /*
	 * Posting images and refreshing the list will be controlled from the Action
	 * Bar.
	 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_refresh: {
                //updateImageList();
                break;
            }

            case R.id.action_favorites: {
                showFavorites();
                break;
            }

            case R.id.action_new: {
                newImage();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

/*    private void updateImageList() {
        mainAdapter.loadObjects();
        setListAdapter(mainAdapter);
    }*/

    private void showFavorites() {
        /*favoritesAdapter.loadObjects();
        setListAdapter(favoritesAdapter);*/
    }

    private void newImage() {
        Intent i = new Intent(this, NewImageActivity.class);
        startActivityForResult(i, 0);
    }

    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(ParseStarterProjectActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Parse.com GridView Tutorial");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            imageArrayList = new ArrayList<ImageList>();
            try {
                // Locate the class table named "Images" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "Images");
                // Locate the column named "position" in Parse.com and order list
                // by ascending
                query.orderByAscending("position");
                ob = query.find();
                for (ParseObject country : ob) {
                    ParseFile image = (ParseFile) country.get("images");
                    ImageList map = new ImageList();
                    map.setImage(image.getUrl());
                    imageArrayList.add(map);
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the gridview in gridview_main.xml
            gridview = (GridView) findViewById(R.id.gridview);
            // Pass the results into ListViewAdapter.java
            adapter = new GridViewAdapter(ParseStarterProjectActivity.this,
                    imageArrayList);
            // Binds the Adapter to the ListView
            gridview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }
}
