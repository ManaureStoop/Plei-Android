package com.arawaney.plei.activity;

import java.util.ArrayList;

import com.arawaney.plei.MainActivity;
import com.arawaney.plei.R;
import com.arawaney.plei.adapter.AnimationLeftToRightAdapter;
import com.arawaney.plei.adapter.PleiListAdapter;
import com.arawaney.plei.db.provider.CategoryProvider;
import com.arawaney.plei.db.provider.PleilistProvider;
import com.arawaney.plei.db.provider.TrackProvider;
import com.arawaney.plei.model.Category;
import com.arawaney.plei.model.Pleilist;
import com.arawaney.plei.model.Track;
import com.arawaney.plei.util.FontUtil;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path.FillType;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CategoryList extends Activity {

	private final String LOG_TAG = "Pleilist-PleiListActivity";

	ListView list;
	PleiListAdapter adapter;
	ArrayList<Pleilist> pleiLists;
	AlphaInAnimationAdapter swingAnimationAdapter;
	Category category;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plei_list);

		getCategory();

		setActionBar();

		loadList();

		loadViews();

		refreshList();
	}

	private void setActionBar() {
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.actionbar_category_view);

		ImageView backButton = (ImageView) findViewById(R.id.imageView_actionBar_back);
		ImageView playButton = (ImageView) findViewById(R.id.imageView_actionBar_play);
		TextView categoryTitle = (TextView) findViewById(R.id.textView_actionBar_title);
		
		categoryTitle.setTypeface(FontUtil.getTypeface(this, FontUtil.HELVETICA_NEUE_LIGHT));

		categoryTitle.setText(category.getName().toString());

		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

	}

	private void getCategory() {
		String categoryId = getIntent().getStringExtra(
				MainActivity.TAG_CATEGORY_ID).toString();
		if (categoryId != null) {
			category = CategoryProvider.readCategory(this, categoryId);
		}
	}

	private void refreshList() {
		adapter = new PleiListAdapter(this, pleiLists);
		swingAnimationAdapter = new AlphaInAnimationAdapter(adapter);
		swingAnimationAdapter.setAbsListView(list);
		list.setAdapter(swingAnimationAdapter);
	}

	private void loadViews() {
		list = (ListView) findViewById(R.id.listView_plei_list);
	}

	private void loadList() {

		String categoryId = getIntent().getStringExtra(
				MainActivity.TAG_CATEGORY_ID).toString();
		if (categoryId != null) {
			pleiLists = PleilistProvider.readPleilistsByCategory(this,
					categoryId);
		}

	}

}
