package com.parse.starter;

/**
 * Created by Keith
 */
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridViewAdapter extends BaseAdapter {
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    private List<ImageList> imagearraylist = null;
    private ArrayList<ImageList> arraylist;

    public GridViewAdapter(Context context, List<ImageList> imagearraylist) {
        this.context = context;
        this.imagearraylist = imagearraylist;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<ImageList>();
        this.arraylist.addAll(imagearraylist);
        imageLoader = new ImageLoader(context);
    }

    public class ViewHolder {
        ImageView image;
    }

    @Override
    public int getCount() {
        return imagearraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return imagearraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.singleitemview, null);
            // Locate the ImageView in gridview_item.xml
            holder.image = (ImageView) view.findViewById(R.id.image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Load image into GridView
        imageLoader.DisplayImage(imagearraylist.get(position).getImage(),
                holder.image);
        // Capture GridView item click
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Intent intent = new Intent(context, SingleItemView.class);
                // Pass all data image
                intent.putExtra("image", imagearraylist.get(position)
                        .getImage());
                context.startActivity(intent);
            }
        });
        return view;
    }
}
