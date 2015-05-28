package com.arawaney.plei;

import java.io.File;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorJoiner.Result;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer.TrackInfo;
import android.nfc.cardemulation.OffHostApduService;
import android.os.AsyncTask;
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
import com.arawaney.plei.service.StreamPlayer;
import com.arawaney.plei.util.FontUtil;
import com.arawaney.plei.util.ServiceUtil;
import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.RefreshCallback;

public class MainActivity extends Activity {

	public final static String TYPE_PLEI_LIST = "Pleilist";
	public final static String TYPE_CATEGORY = "Category";

	public final static String TAG_CATEGORY_ID = "categoryId";
	public final static String TAG_PLEILIST_ID = "pleilistId";

	private final String LOG_TAG = "Pleilist-MainActivity";

	static LinearLayout playButton;

	static ProgressDialog progressDialog;
	public AlertDialog.Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkIfCancelPlayerIntent();

		setActionBar();

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	protected void onResume() {
		checkIfCancelPlayerIntent();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		killMediaPlayerService();
		super.onDestroy();
	}
	
	

	private void checkIfCancelPlayerIntent() {
		if (getIntent() != null) {
			if (getIntent().getExtras() != null) {
				if (getIntent().getExtras().getString(
						StreamPlayer.TAG_KILL_SERVICE) != null) {
					if (getIntent().getExtras()
							.getString(StreamPlayer.TAG_KILL_SERVICE)
							.equals(StreamPlayer.INTENT_KILL_SERVICE)) {
						showCancelPlayerAlertDialog();
						getIntent().removeExtra(StreamPlayer.TAG_KILL_SERVICE);
					}
				}
			}

		}
	}

	private void showCancelPlayerAlertDialog() {
		if (builder == null) {
			builder = new Builder(this);
			// new AlertDialog.Builder(this)
			builder.setTitle(
					getResources().getString(
							R.string.alert_dialog_stop_player_title))
					.setMessage(
							getResources().getString(
									R.string.alert_dialog_stop_player_question))
					.setPositiveButton(
							getResources()
									.getString(
											R.string.alert_dialog_stop_player_possitive),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									killMediaPlayerService();
									builder = null;

								}

								
							})
					.setNegativeButton(
							getResources().getString(
									R.string.alert_dialog_stop_player_negative),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
									builder = null;
								}
							})

					.show();

		}

	}
	
	private void killMediaPlayerService() {
		stopService(new Intent(MainActivity.this,
				StreamPlayer.class));
		Intent intent = new Intent();
		intent.setAction(TrackActivity.ACTION_FINISH);
		sendBroadcast(intent);
		playButton.setVisibility(View.INVISIBLE);
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

			refreshUI(inflater);

			checkIfMusicPLaying();

			connectAndUpdateParse(this);

			return rootView;
		}

		private void refreshUI(LayoutInflater inflater) {
			loadCovers();
			refreshScrollViews(inflater);
		}

		private void connectAndUpdateParse(final ParseListener listener) {

			UpdateData updateTask = new UpdateData(getActivity(), listener);
			updateTask.execute();

		}

		private void checkIfMusicPLaying() {
			if (ServiceUtil.isServiceRunning(StreamPlayer.class.getName(),
					getActivity())) {
				playButton.setVisibility(View.VISIBLE);
			} else
				playButton.setVisibility(View.GONE);
		}

		@Override
		public void onResume() {
			refreshUI(inflater);
			checkIfMusicPLaying();
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
			coverTitle.setTypeface(FontUtil.getTypeface(getActivity(),
					FontUtil.HELVETICA_NEUE_LIGHT));
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
			}else{
				Log.d("Error loading Image", "Irmage NULL with cover : "+ cover.getName());
			}
			return coverView;
		}

		private void onCoverCLicked(final Cover cover) {
			Log.d("test", "CLick on view" + cover.getName());
			if (cover.getType().equals(TYPE_CATEGORY)) {
				Intent i = new Intent(getActivity(), CategoryList.class);
				i.putExtra(TAG_CATEGORY_ID, cover.getCategoryId());
				startActivity(i);
			} else if (cover.getType().equals(TYPE_PLEI_LIST)) {
				Intent i = new Intent(getActivity(), TrackActivity.class);
				i.putExtra(MainActivity.TAG_PLEILIST_ID, cover.getPleilistId());
				i.putExtra(TrackActivity.TAG_CALL_MODE,
						TrackActivity.MODE_NEW_PLEILIST);
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
				if (coversFavoritos == null) {
					Log.d("test1", "favoritos null!!!");
				}
			}

		}

		private ArrayList<Cover> transforPleilistToCover(
				ArrayList<Pleilist> pleilistFavoritos) {
			
			ArrayList<Cover> covers = new ArrayList<Cover>();
			
			for (Pleilist pleilist : pleilistFavoritos) {
				
				Cover cover = new Cover();
				cover.setSystem_id(pleilist.getSystem_id());
				cover.setName(pleilist.getName());
				
				if (pleilist.getCoverImage() != null) {
					cover.setImageFile(pleilist.getCoverImage());

				} else {
					cover.setImageFile(pleilist.getImage());

				}
				cover.setPleilistId(pleilist.getSystem_id());
				cover.setType(TYPE_PLEI_LIST);

				covers.add(cover);

			}
			return covers;
		}

		private void loadPlanesCovers() {
			coversPlanes = CoverProvider.readCoversBySection(getActivity(),
					SECTION_PLANES);
			if (coversPlanes == null) {
				Log.d("test1", "planes null!!!");
			}

		}

		private void loadGenerosCovers() {
			coversGeneros = CoverProvider.readCoversBySection(getActivity(),
					SECTION_GENEROS);
			if (coversGeneros == null) {
				Log.d("test1", "generos null!!!");
			}

		}

		private void loadDestacadosCovers() {
			coversDestacados = CoverProvider.readCoversBySection(getActivity(),
					SECTION_DESTACADOS);
			if (coversDestacados == null) {
				Log.d("test1", "destacados null!!!");
			}

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
			progressDialog.dismiss();
			Toast toast = new Toast(getActivity());
			toast.setDuration(Toast.LENGTH_LONG);
			if (succes) {
				Toast.makeText(getActivity(), "Login sucessfull",
						Toast.LENGTH_LONG).show();
			} else
				Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_LONG)
						.show();
		}

		@Override
		public void onAllCategoriesFinished(Boolean b) {
			if (b) {

				refreshUI(inflater);

			}
			// Update Pleilists and Tracks
			UpdatePleilist updatePleilist = new UpdatePleilist(getActivity(),
					this);
			updatePleilist.execute();
		}

		@Override
		public void onAllCoversFinished(Boolean b) {
			if (b) {

				refreshUI(inflater);
			}
		}

		@Override
		public void onAllPleilistsFinished(Boolean b) {

			if (b) {
				refreshUI(inflater);
			}

		}

		@Override
		public void onAllTracksFinished(ArrayList<Track> tracks) {
			if (tracks != null) {
				for (Track track : tracks) {
					Track savedTrack = TrackProvider.readTrack(getActivity(),
							track.getSystem_id());
					if (savedTrack != null) {
						track.setId(savedTrack.getId());
						TrackProvider.updateTrack(getActivity(), track);
					} else {
						TrackProvider.insertTrack(getActivity(), track);
					}
				}
				refreshUI(inflater);
			}
		}

		@Override
		public void onSavedFAvoriteDone(boolean succes, Pleilist pleilist) {
			if (succes) {
				PleilistProvider.updatePleilist(getActivity(), pleilist);
				ParseProvider.downloadPleilistCoverImage(pleilist, getActivity(), this);
			} else {
				pleilist.setFavorite(Pleilist.NOT_FAVORITE);
				Toast.makeText(getActivity(),
						getResources().getString(R.string.toast_favorited_failed),
						Toast.LENGTH_LONG).show();
			}

		}

		@Override
		public void onFavoritesUpdated(boolean b) {
			refreshUI(inflater);
		}

		@Override
		public void onFavoritedRemoved(boolean b, Pleilist pleilist) {
			// TODO Auto-generated method stub

		}

		class UpdateData extends AsyncTask<Request, Void, Result> {
			Context context;
			ParseListener listener;

			public UpdateData(Context context, ParseListener listener) {
				this.context = context;
				this.listener = listener;
			}

			@Override
			protected void onPreExecute() {
				progressDialog = new ProgressDialog(context);
				progressDialog.setTitle("Realizando Login...");
				super.onPreExecute();

			}

			@Override
			protected Result doInBackground(Request... params) {

				ParseProvider.initializeParse(context);

				if (ParseUser.getCurrentUser() == null) {
					publishProgress();
					ParseProvider.logIn("manaurestoop@gmail.com",
							"manaure.stoop!", context, listener);
				}

				DataUpdater.UpdateAllData(listener, context);

				return null;

			}

			@Override
			protected void onProgressUpdate(Void... values) {
				super.onProgressUpdate(values);
				progressDialog.show();
			}

			@Override
			protected void onPostExecute(Result res) {

			}
		}

		class UpdatePleilist extends AsyncTask<Request, Void, Result> {
			Context context;
			ParseListener listener;

			public UpdatePleilist(Context context, ParseListener listener) {
				this.context = context;
				this.listener = listener;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Result doInBackground(Request... params) {
				ParseProvider.updatePleilists(context, listener);
				return null;
			}

			@Override
			protected void onPostExecute(Result res) {

			}
		}

		class UpdateTracks extends AsyncTask<Request, Void, Result> {
			Context context;
			ParseListener listener;

			public UpdateTracks(Context context, ParseListener listener) {
				this.context = context;
				this.listener = listener;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Result doInBackground(Request... params) {
				ParseProvider.updateTracks(context, listener);
				return null;
			}

			@Override
			protected void onPostExecute(Result res) {

			}
		}

		@Override
		public void onImageCoverDownloaded() {
			refreshUI(inflater);
		}

		@Override
		public void onImagePleilistDownloaded() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAllTracksByPLeilistFinished() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPleilistCoverImageDownloaded(Pleilist pleilist) {
			loadCovers();
			refreshUI(inflater);
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

		playButton = (LinearLayout) findViewById(R.id.layout_actionBar_main_play);

		playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						TrackActivity.class);
				i.putExtra(TrackActivity.TAG_CALL_MODE,
						TrackActivity.MODE_OPEN_PLAYING_PLEILIST);
				startActivity(i);
			}
		});

	}

}
