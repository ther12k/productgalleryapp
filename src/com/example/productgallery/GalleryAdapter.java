package com.example.productgallery;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryAdapter extends BaseAdapter {
	    private List<Item> items = new ArrayList<Item>();
	    private LayoutInflater inflater;

	    public GalleryAdapter(Context context) {
	        inflater = LayoutInflater.from(context);
	        items.add(new Item("Product A",R.drawable.a));
	        items.add(new Item("Product B",R.drawable.b));
	        items.add(new Item("Product C",R.drawable.c));
	        items.add(new Item("Product D",R.drawable.d));
	        items.add(new Item("Product E",R.drawable.e));
	        items.add(new Item("Product F",R.drawable.f));
	        items.add(new Item("Product G",R.drawable.g));
	        items.add(new Item("Product H",R.drawable.h));
	    }
	    @Override
	    public int getCount() {
	        return items.size();
	    }

	    @Override
	    public Object getItem(int i) {
	        return items.get(i);
	    }

	    @Override
	    public long getItemId(int i) {
	        return items.get(i).hashCode();
	    }

		@Override
	    public View getView(int i, View view, ViewGroup viewGroup) {
	        View v = view;
	        ImageView picture;
	        TextView name;

	        if(v == null) {
	            v = inflater.inflate(R.layout.grid_item, viewGroup, false);
	            v.setTag(R.id.picture, v.findViewById(R.id.picture));
	            v.setTag(R.id.text, v.findViewById(R.id.text));
	        }

	        picture = (ImageView)v.getTag(R.id.picture);
	        name = (TextView)v.getTag(R.id.text);

	        final Item item = (Item)getItem(i);
	        name.setText(item.name);
	        picture.setImageResource(item.drawable);

	        return v;
	    }
}
