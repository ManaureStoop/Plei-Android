package com.arawaney.plei.adapter;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arawaney.plei.R;
import com.arawaney.plei.model.Pleilist;
import com.arawaney.plei.util.FontUtil;

public class AnimationLeftToRightAdapter extends com.nhaarman.listviewanimations.ArrayAdapter<Pleilist> {
	private  ArrayList<Pleilist> pleilists;
	Context contxt;
	private LayoutInflater l_Inflater;
	

	public AnimationLeftToRightAdapter(Context context, ArrayList<Pleilist> pleilists) {
		this.pleilists = pleilists;
		l_Inflater = LayoutInflater.from(context);
		contxt = context;

	}

	public int getCount() {
		return pleilists.size();
	}

	public Pleilist getItem(int position) {
		return pleilists.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder2 holder;
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.pleilist_item_view, null);
			holder = new ViewHolder2();
			
			holder.image_pleilist = (ImageView) convertView.findViewById(R.id.imageView_pleilist_item);
			holder.txt_pleilist_title = (TextView) convertView.findViewById(R.id.textView_pleilist_item_title);
			holder.pleilist_color_tag = (View) convertView.findViewById(R.id.view_color_tag);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder2) convertView.getTag();
		}
	
		
//		setFonts(holder);
		Pleilist pleilist = pleilists.get(position);
		
//		holder.image_pleilist.setVisibility(View.GONE);
	    
	    holder.txt_pleilist_title.setText(pleilist.getName().toString());
		

	    if (pleilist.getImage() != null) {
			if (imageExists(pleilist.getImage(), contxt)) {
				File filePath = contxt.getFileStreamPath(
						pleilist.getImage());
				holder.image_pleilist.setImageDrawable(Drawable
						.createFromPath(filePath.toString()));
			}
		}
	    
		
		return convertView;
	}

	private void setFonts(ViewHolder2 holder) {
		holder.txt_pleilist_title.setTypeface(FontUtil.getTypeface(contxt, FontUtil.HELVETICA_NEUE_LIGHT));
	}
	
	
	static class ViewHolder2 {
		ImageView image_pleilist;
		TextView txt_pleilist_title;
		View pleilist_color_tag;
	}
	
	protected static boolean imageExists(String imageName, Context context) {
		File file = new File(context.getFilesDir(), imageName);
		if (file.exists())
			return true;
		else
			return false;
	}
}
