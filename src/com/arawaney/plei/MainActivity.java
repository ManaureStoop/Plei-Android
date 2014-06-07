package com.arawaney.plei;

import java.io.File;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.nfc.cardemulation.OffHostApduService;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arawaney.plei.activity.CategoryList;
import com.arawaney.plei.activity.TrackActivity;
import com.arawaney.plei.db.provider.CategoryProvider;
import com.arawaney.plei.db.provider.CoverProvider;
import com.arawaney.plei.db.provider.PleilistProvider;
import com.arawaney.plei.db.provider.TrackProvider;
import com.arawaney.plei.listener.ParseListener;
import com.arawaney.plei.model.Category;
import com.arawaney.plei.model.Cover;
import com.arawaney.plei.model.Pleilist;
import com.arawaney.plei.model.Track;
import com.arawaney.plei.parse.DataUpdater;
import com.arawaney.plei.parse.ParseProvider;
import com.arawaney.plei.util.FontUtil;
import com.parse.ParseUser;

public class MainActivity extends Activity implements ParseListener {

	public final static String TYPE_PLEI_LIST = "Pleilist";
	public final static String TYPE_CATEGORY = "Category";

	public final static String TAG_CATEGORY_ID = "categoryId";
	public final static String TAG_PLEILIST_ID = "pleilistId";

	private final String LOG_TAG = "Pleilist-MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ParseProvider.initializeParse(this);

		if (ParseUser.getCurrentUser() == null) {
			ParseProvider.logIn("manaurestoop@gmail.com", "manaure.stoop!",
					this, this);
		}

		setActionBar();

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		DataUpdater.UpdateAllData(this, this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements
			ParseListener {

		LayoutInflater inflater;

		LinearLayout generosList;
		LinearLayout planesList;
		LinearLayout destacadosList;
		LinearLayout favoritosList;

		ArrayList<Cover> coversDestacados;
		ArrayList<Cover> coversPlanes;
		ArrayList<Cover> coversGeneros;
		ArrayList<Cover> coversFavoritos;

		private final int SECTION_DESTACADOS = 0;
		private final int SECTION_PLANES = 1;
		private final int SECTION_GENEROS = 2;

		// private final int SECTION_FAVORITOS = 4;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			this.inflater = inflater;
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			loadViews(rootView);
			loadCovers();
			refreshScrollViews(inflater);
			return rootView;
		}
		
		@Override
		public void onResume() {
		loadCovers();
		refreshScrollViews(inflater);
			super.onResume();
		}

		private void refreshScrollViews(LayoutInflater inflater) {
			refreshDestacadosScrollView();
			refreshPlanesScrollView();
			refreshGenerosScrollView();
			refreshFavoritosScrollView();
		}

		private void refreshFavoritosScrollView() {
			if (coversFavoritos != null) {
				favoritosList.removeAllViews();
				for (Cover cover : coversFavoritos) {
					View coverView = fillCoverItemInfo(inflater, cover);
					favoritosList.addView(coverView);
				}
			}

		}

		private void refreshPlanesScrollView() {
			if (coversPlanes != null) {
				planesList.removeAllViews();
				for (Cover cover : coversPlanes) {
					View coverView = fillCoverItemInfo(inflater, cover);
					planesList.addView(coverView);
				}

			}
		}

		private void refreshGenerosScrollView() {
			if (coversGeneros != null) {
				generosList.removeAllViews();
				for (Cover cover : coversGeneros) {	
					View coverView = fillCoverItemInfo(inflater, cover);
					generosList.addView(coverView);
				}
			}
		}

		private void refreshDestacadosScrollView() {
			if (coversDestacados != null) {
				destacadosList.removeAllViews();
				for (Cover cover : coversDestacados) {
					View coverView = fillCoverItemInfo(inflater, cover);
					destacadosList.addView(coverView);

				}
			}
		}

		private View fillCoverItemInfo(LayoutInflater inflater,
				final Cover cover) {
			View coverView = setCoverViews(inflater, cover);
			coverView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onCoverCLicked(cover);
				}
			});
			return coverView;
		}

		private View setCoverViews(LayoutInflater inflater, final Cover cover) {
			View coverView = inflater.inflate(R.layout.cover_item_view, null);
			ImageView coverImage = (ImageView) coverView
					.findViewById(R.id.imageView_cover_item);
			TextView coverTitle = (TextView) coverView
					.findViewById(R.id.textView_cover_itemt_title);
			coverTitle.setTypeface(FontUtil.getTypeface(getActivity(), FontUtil.HELVETICA_NEUE_LIGHT));
			if (cover.getName() != null) {
				coverTitle.setText(cover.getName().toString());
			}
			if (cover.getImageFile() != null) {
				if (imageExists(cover.getImageFile(), getActivity())) {
					File filePath = getActivity().getFileStreamPath(
							cover.getImageFile());
					coverImage.setImageDrawable(Drawable
							.createFromPath(filePath.toString()));
				}
			}
			return coverView;
		}
		
		private void onCoverCLicked(final Cover cover) {
			Log.d("test", "CLick on view" + cover.getName());
			if (cover.getType().equals(TYPE_CATEGORY)) {
				Intent i = new Intent(getActivity(), CategoryList.class);
				i.putExtra(TAG_CATEGORY_ID, cover.getCategoryId());
				startActivity(i);
			}else if (cover.getType().equals(TYPE_PLEI_LIST)){
				Intent i = new Intent(getActivity(), TrackActivity.class);
				i.putExtra(MainActivity.TAG_PLEILIST_ID, cover.getPleilistId());
				startActivity(i);
			}
		}

		private void loadCovers() {
			loadDestacadosCovers();
			loadPlanesCovers();
			loadGenerosCovers();
			loadFavoritosCovers();

		}

		private void loadFavoritosCovers() {
			ArrayList<Pleilist> pleilistFavoritos = PleilistProvider
					.getFavoritesPleiLists(getActivity());
			if (pleilistFavoritos != null) {
				coversFavoritos = transforPleilistToCover(pleilistFavoritos);
			}

		}

		private ArrayList<Cover> transforPleilistToCover(
				ArrayList<Pleilist> pleilistFavoritos) {
			ArrayList<Cover> covers = new ArrayList<Cover>();
			for (Pleilist pleilist : pleilistFavoritos) {
				Cover cover = new Cover();
				cover.setSystem_id(pleilist.getSystem_id());
				cover.setName(pleilist.getName());
				cover.setImageFile(pleilist.getImage());
				cover.setPleilistId(pleilist.getSystem_id());
				cover.setType(TYPE_PLEI_LIST);

				covers.add(cover);

			}
			return covers;
		}

		private void loadPlanesCovers() {
			coversPlanes = CoverProvider.readCoversBySection(getActivity(),
					SECTION_PLANES);

		}

		private void loadGenerosCovers() {
			coversGeneros = CoverProvider.readCoversBySection(getActivity(),
					SECTION_GENEROS);

		}

		private void loadDestacadosCovers() {
			coversDestacados = CoverProvider.readCoversBySection(getActivity(),
					SECTION_DESTACADOS);

		}

		private void loadViews(View rootView) {
			generosList = (LinearLayout) rootView
					.findViewById(R.id.listView_main_generos);
			planesList = (LinearLayout) rootView
					.findViewById(R.id.listView_main_planes);
			destacadosList = (LinearLayout) rootView
					.findViewById(R.id.listView_main_destacados);
			favoritosList = (LinearLayout) rootView
					.findViewById(R.id.listView_main_favoritos);
			if (destacadosList == null) {
				Log.d("test", "null destacados");
			}
			if (planesList == null) {
				Log.d("test", "null planes");
			}
			if (generosList == null) {
				Log.d("test", "null generos");
			}

		}

		@Override
		public void OnLoginResponse(boolean succes) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAllCategoriesFinished(ArrayList<Category> categories) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAllCoversFinished(ArrayList<Cover> covers) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAllPleilistsFinished(ArrayList<Pleilist> pleilists) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAllTracksFinished(ArrayList<Track> tracks) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSavedFAvoriteDone(boolean succes, Pleilist pleilist) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFavoritesUpdated(boolean b) {
			refreshFavoritosScrollView();
		}

		@Override
		public void onFavoritedRemoved(boolean b, Pleilist pleilist) {
			// TODO Auto-generated method stub
			
		}

	}

	@Override
	public void OnLoginResponse(boolean succes) {
		Toast toast = new Toast(this);
		toast.setDuration(Toast.LENGTH_LONG);
		if (succes) {
			Toast.makeText(this, "Login sucessfull", Toast.LENGTH_LONG).show();
		} else
			Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onAllCategoriesFinished(ArrayList<Category> categories) {
		if (categories != null) {
			for (Category category : categories) {
				Category savedCategory = CategoryProvider.readCategory(this,
						category.getSystem_id());
				if (savedCategory != null) {
					category.setId(savedCategory.getId());
					CategoryProvider.updateCategory(this, category);
				} else {
					CategoryProvider.insertCategory(this, category);
				}
			}
		}
	}

	@Override
	public void onAllCoversFinished(ArrayList<Cover> covers) {
		if (covers != null) {
			for (Cover cover : covers) {
				Cover savedCover = CoverProvider.readCover(this,
						cover.getSystem_id());
				if (savedCover != null) {
					cover.setId(savedCover.getId());
					CoverProvider.updateCover(this, cover);
				} else {
					CoverProvider.insertCover(this, cover);
				}
			}
		}
	}

	@Override
	public void onAllPleilistsFinished(ArrayList<Pleilist> pleilists) {
		if (pleilists != null) {
			for (Pleilist pleilist : pleilists) {
				Pleilist savedPleilist = PleilistProvider.readPleilist(this,
						pleilist.getSystem_id());
				if (savedPleilist != null) {
					pleilist.setId(savedPleilist.getId());
					pleilist.setFavorite(savedPleilist.getFavorite());
					PleilistProvider.updatePleilist(this, pleilist);
				} else {
					PleilistProvider.insertPleilist(this, pleilist);
				}
			}
		}
	}

	@Override
	public void onAllTracksFinished(ArrayList<Track> tracks) {
		if (tracks != null) {
			for (Track track : tracks) {
				Track savedTrack = TrackProvider.readTrack(this,
						track.getSystem_id());
				if (savedTrack != null) {
					track.setId(savedTrack.getId());
					TrackProvider.updateTrack(this, track);
				} else {
					TrackProvider.insertTrack(this, track);
				}
			}
		}
	}

	protected static boolean imageExists(String imageName, Context context) {
		File file = new File(context.getFilesDir(), imageName);
		if (file.exists())
			return true;
		else
			return false;
	}

	private void setActionBar() {
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.actionbar_main_view);

		ImageView playButton = (ImageView) findViewById(R.id.imageView_actionBar_main_play);

		playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

	}

	@Override
	public void onSavedFAvoriteDone(boolean succes, Pleilist pleilist) {

	}

	@Override
	public void onFavoritesUpdated(boolean b) {

	}

	@Override
	public void onFavoritedRemoved(boolean b, Pleilist pleilist) {
		// TODO Auto-generated method stub
		
	}

}
