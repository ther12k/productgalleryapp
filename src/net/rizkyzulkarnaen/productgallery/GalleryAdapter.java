package net.rizkyzulkarnaen.productgallery;

import java.util.ArrayList;
import java.util.List;

import net.rizkyzulkarnaen.productgallery.entity.Product;
import net.rizkyzulkarnaen.productgallery.sql.ProductSource;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryAdapter extends BaseAdapter {
	    private List<Product> items = new ArrayList<Product>();
	    private LayoutInflater inflater;
	    private Point laySize = null;
	    private ProductSource productSource;
		private LruCache<String, Bitmap> mMemoryCache;
	    
	    public GalleryAdapter(Context context,Point laySize,LruCache<String, Bitmap> mMemoryCache) {
	        inflater = LayoutInflater.from(context);
	        this.laySize = laySize;
	        this.mMemoryCache = mMemoryCache;
			productSource = new ProductSource(context);
			productSource.open();
			items = (ArrayList<Product>) productSource.getAll();
			productSource.close();
	    }

		
		public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		    if (getBitmapFromMemCache(key) == null) {
		        mMemoryCache.put(key, bitmap);
		    }
		}

		public Bitmap getBitmapFromMemCache(String key) {
		    return mMemoryCache.get(key);
		}
	    @Override
	    public int getCount() {
	        return items.size();
	    }
	    
	    @Override
	    public Product getItem(int i) {
	        return items.get(i);
	    }

	    public Product getItemWImage(int i) {
			productSource.open();
			Product product = productSource.get(items.get(i).getId());
			productSource.close();
	        return product;
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

	        Product item = (Product)getItem(i);
	        name.setText(item.getNo());
	        Bitmap bitmap = getBitmapFromMemCache(String.valueOf(item.getId()));
	        if(bitmap!=null){
	        	picture.setImageBitmap(bitmap);
	        }else{
	        	item = (Product)getItemWImage(i);
	        	if(item.getImageBitmap()!=null){
		        	Bitmap photo = item.getImageBitmap();
		        	int width = photo.getWidth();
		            int height = photo.getHeight();
		        	bitmap = Bitmap.createScaledBitmap(photo,
								width/2,
								height/2, true);
		        	addBitmapToMemoryCache(String.valueOf(item.getId()),bitmap);
		        	picture.setImageBitmap(getBitmapFromMemCache(String.valueOf(item.getId())));
		        	photo.recycle();
		        	//bitmap.recycle();
	        	}else{
	        		picture.setImageResource(R.drawable.ic_launcher);
	        	}
	        }

	        return v;
	    }
}
