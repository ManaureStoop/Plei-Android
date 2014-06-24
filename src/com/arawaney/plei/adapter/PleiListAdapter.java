package com.arawaney.plei.adapter;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.arawaney.plei.MainActivity;
import com.arawaney.plei.R;
import com.arawaney.plei.activity.CategoryList;
import com.arawaney.plei.activity.TrackActivity;
import com.arawaney.plei.model.Pleilist;
import com.arawaney.plei.util.FileUtil;
import com.arawaney.plei.util.FontUtil;

public class PleiListAdapter extends BaseAdapter {
	private ArrayList<Pleilist> pleilists;
	Context contxt;
	private LayoutInflater l_Inflater;

	public PleiListAdapter(Context context, ArrayList<Pleilist> pleilists) {
		this.pleilists = pleilists;
		l_Inflater = LayoutInflater.from(context);
		contxt = context;

	}

	public int getCount() {
		return pleilists.size();
	}

	public Object getItem(int position) {
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

			holder.image_pleilist = (ImageView) convertView
					.findViewById(R.id.imageView_pleilist_item);
			holder.txt_pleilist_title = (TextView) convertView
					.findViewById(R.id.textView_pleilist_item_title);
			holder.pleilist_color_tag = (View) convertView
					.findViewById(R.id.view_color_tag);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder2) convertView.getTag();
		}

		setFonts(holder);
		final Pleilist pleilist = pleilists.get(position);

		holder.txt_pleilist_title.setText(pleilist.getName().toString());

		setTagColor(position + 1, holder.pleilist_color_tag, contxt);

		if (pleilist.getImage() != null) {
			if (FileUtil.imageExists(pleilist.getImage(), contxt)) {
				File filePath = contxt.getFileStreamPath(pleilist.getImage());
				holder.image_pleilist.setImageDrawable(Drawable
						.createFromPath(filePath.toString()));
			}
		}

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Log.d("test", "CLick on view" + pleilist.getName());
				Intent i = new Intent(contxt, TrackActivity.class);
				i.putExtra(MainActivity.TAG_CATEGORY_ID,
						pleilist.getCategoryId());
				i.putExtra(TrackActivity.TAG_CALL_MODE,
						TrackActivity.MODE_NEW_PLEILIST);
				i.putExtra(MainActivity.TAG_PLEILIST_ID, pleilist.getSystem_id());
				contxt.startActivity(i);

			}
		});

		return convertView;
	}

	private void setTagColor(int position, View pleilist_color_tag,
			Context context) {
		if (position % 4 == 0) {
			pleilist_color_tag.setBackgroundColor(context.getResources()
					.getColor(R.color.plei_blue));
		} else if (position % 3 == 0) {
			pleilist_color_tag.setBackgroundColor(context.getResources()
					.getColor(R.color.plei_green));

		} else if (position % 2 == 0) {
			pleilist_color_tag.setBackgroundColor(context.getResources()
					.getColor(R.color.plei_yellow));

		} else {
			pleilist_color_tag.setBackgroundColor(context.getResources()
					.getColor(R.color.plei_red));

		}

	}

	private void setFonts(ViewHolder2 holder) {
		holder.txt_pleilist_title.setTypeface(FontUtil.getTypeface(contxt,
				FontUtil.HELVETICA_NEUE_LIGHT));
	}

	static class ViewHolder2 {
		ImageView image_pleilist;
		TextView txt_pleilist_title;
		View pleilist_color_tag;
	}

	
}
