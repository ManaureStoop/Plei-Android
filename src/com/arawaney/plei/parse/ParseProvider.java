package com.arawaney.plei.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.arawaney.plei.db.provider.CategoryProvider;
import com.arawaney.plei.db.provider.CoverProvider;
import com.arawaney.plei.db.provider.PleilistProvider;
import com.arawaney.plei.listener.ParseListener;
import com.arawaney.plei.model.Category;
import com.arawaney.plei.model.Cover;
import com.arawaney.plei.model.PelilistTrack;
import com.arawaney.plei.model.Pleilist;
import com.arawaney.plei.model.Track;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ParseProvider {
	private static final String LOG_TAG = "Plei-ParseProvider";

	private static final String NAME_TAG = "name";
	private static final String IMAGE_FILE_TAG = "imageFile";
	private static final String IMAGE_TAG = "image";

	private static final String TYPE_TAG = "type";
	private static final String CATEGORY_TAG = "category";
	private static final String PLEILIST_TAG = "pleilist";
	private static final String SECTION_TAG = "section";
	private static final String DELETED_TAG = "deleted";
	private static final String FLAGED_TAG = "flagged";

	private static final String ORDER_TAG = "order";
	private static final String TRACKS_TAG = "tracks";
	private static final String COVER_IMAGE_TAG = "coverImage";

	private static final String ARTIST_TAG = "artist";
	private static final String URL_TAG = "URL";
	private static final String YOUTUBE_URL_TAG = "youtubeURL";

	private static final String CATEGORY_CLASS = "Category";
	private static final String COVER_CLASS = "Cover";
	private static final String PLEILIST_CLASS = "Pleilist";
	private static final String TRACK_CLASS = "Track";

	private static final String UPDATED_AT_TAG = "updatedAt";

	public static void logIn(String username, final String password,
			final Context context, final ParseListener listener) {
		final ProgressDialog progressDialog = ProgressDialog.show(context, "",
				"Realizando Login...");
		ParseUser.logInInBackground(username, password, new LogInCallback() {

			public void done(ParseUser user, ParseException e) {
				boolean succes;
				if (user != null) {
					succes = true;

				} else {
					succes = false;
					Log.e(LOG_TAG, "Error by login :" + e.getMessage());
				}
				progressDialog.dismiss();
				listener.OnLoginResponse(succes);
			}

		});
	}

	public static void initializeParse(final Context context) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
				Parse.initialize(context,
						"X5n0LmTy7RSybqBP2QWmGoQDQNUqa82bXwee6Sx3",
						"acnE1q6yEAQ24KMofj74rnOowvBN6TVqjKwomlcZ");
//			}
//		}).start();

	}

	public static void updateCategories(Context context,
			final ParseListener listener) {
		Date updateAt = CategoryProvider.getLastUpdate(context);

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				CATEGORY_CLASS);
		if (updateAt != null) {
			query.whereGreaterThan(UPDATED_AT_TAG, updateAt);
		}
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> cList, ParseException e) {

				if (e == null) {
					ArrayList<Category> categories = new ArrayList<Category>();

					for (ParseObject object : cList) {
						Category category = readCategoryFromCursor(object,
								listener);
						category.setSystem_id(object.getObjectId());
						categories.add(category);
					}

					listener.onAllCategoriesFinished(categories);

				} else {
					listener.onAllCategoriesFinished(null);
					Log.d(LOG_TAG,
							" Query error getting Categories: "
									+ e.getMessage());
				}

			}

		});

	}

	protected static Category readCategoryFromCursor(
			ParseObject parsedCategory, ParseListener listener) {
		final Category category = new Category();

		if (parsedCategory.getString(NAME_TAG) != null) {
			category.setName(parsedCategory.getString(NAME_TAG));
		}
		if (parsedCategory.getString(IMAGE_FILE_TAG) != null) {
			category.setImageFile(parsedCategory.getString(IMAGE_FILE_TAG));
		}

		Calendar updateCalendar = Calendar.getInstance();

		updateCalendar.setTimeInMillis(parsedCategory.getUpdatedAt().getTime());

		category.setUpdated_at(updateCalendar);

		return category;
	}

	public static void updateCovers(final Context context,
			final ParseListener listener) {
		Date updateAt = CoverProvider.getLastUpdate(context);

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(COVER_CLASS);
		if (updateAt != null) {
			query.whereGreaterThan(UPDATED_AT_TAG, updateAt);
		}
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> cList, ParseException e) {

				if (e == null) {
					ArrayList<Cover> covers = new ArrayList<Cover>();

					for (ParseObject object : cList) {
						final Cover cover = readCoverFromCursor(object,
								listener);
						cover.setSystem_id(object.getObjectId());
						covers.add(cover);

						if (cover.getImageFile() != null) {
							if (!imageExists(cover.getImageFile(), context)) {
								ParseFile applicantResume = (ParseFile) object
										.get(IMAGE_TAG);
								applicantResume
										.getDataInBackground(new GetDataCallback() {
											public void done(byte[] image,
													ParseException e) {
												if (e == null) {
													FileOutputStream outputStream;

													try {
														outputStream = context
																.openFileOutput(
																		cover.getImageFile(),
																		Context.MODE_PRIVATE);
														outputStream
																.write(image);
														outputStream.close();
														Log.d(LOG_TAG,
																"Image : "
																		+ cover.getImageFile()
																		+ " from : "
																		+ cover.getName());
													} catch (Exception error) {
														error.printStackTrace();
													}
												} else {
													Log.d(LOG_TAG,
															"Error dowloading image for cover :"
																	+ cover.getName());
												}
											}
										});
							}
						}
					}

					listener.onAllCoversFinished(covers);

				} else {
					listener.onAllCoversFinished(null);
					Log.d(LOG_TAG,
							" Query error getting Covers" + e.getMessage());
				}

			}

		});

	}

	protected static boolean imageExists(String imageName, Context context) {
		File file = new File(context.getFilesDir(), imageName);
		if (file.exists())
			return true;
		else
			return false;
	}

	protected static Cover readCoverFromCursor(ParseObject parsedCover,
			ParseListener listener) {
		final Cover cover = new Cover();

		if (parsedCover.getString(NAME_TAG) != null) {
			cover.setName(parsedCover.getString(NAME_TAG));
		}
		if (parsedCover.getParseFile(IMAGE_TAG) != null) {
			cover.setImageFile(parsedCover.getParseFile(IMAGE_TAG).getName());
		}
		if (parsedCover.getString(TYPE_TAG) != null) {
			cover.setType(parsedCover.getString(TYPE_TAG));
		}
		if (parsedCover.getParseObject(CATEGORY_TAG) != null) {
			String categoryId = parsedCover.getParseObject(CATEGORY_TAG)
					.getObjectId();
			cover.setCategoryId(categoryId);
		}
		if (parsedCover.getParseObject(PLEILIST_TAG) != null) {
			String pleilistId = parsedCover.getParseObject(PLEILIST_TAG)
					.getObjectId();
			cover.setPleilistId(pleilistId);
		}
		if (parsedCover.getInt(SECTION_TAG) != -1) {
			cover.setSection(parsedCover.getInt(SECTION_TAG));
		}

		Calendar updateCalendar = Calendar.getInstance();

		updateCalendar.setTimeInMillis(parsedCover.getUpdatedAt().getTime());

		cover.setUpdated_at(updateCalendar);

		return cover;
	}

	public static void updatePleilists(final Context context,
			final ParseListener listener) {
		Date updateAt = PleilistProvider.getLastUpdate(context);

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				PLEILIST_CLASS);
		if (updateAt != null) {
			query.whereGreaterThan(UPDATED_AT_TAG, updateAt);
		}
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> cList, ParseException e) {

				if (e == null) {
					ArrayList<Pleilist> pleilists = new ArrayList<Pleilist>();

					for (ParseObject object : cList) {
						final Pleilist pleilist = readPleilistFromCursor(
								object, listener);
						pleilist.setSystem_id(object.getObjectId());
						pleilists.add(pleilist);

						if (pleilist.getImage() != null) {
							if (!imageExists(pleilist.getImage(), context)) {
								ParseFile applicantResume = (ParseFile) object
										.get(IMAGE_TAG);
								applicantResume
										.getDataInBackground(new GetDataCallback() {
											public void done(byte[] image,
													ParseException e) {
												if (e == null) {
													FileOutputStream outputStream;

													try {
														outputStream = context
																.openFileOutput(
																		pleilist.getImage(),
																		Context.MODE_PRIVATE);
														outputStream
																.write(image);
														outputStream.close();
														Log.d(LOG_TAG,
																"Image : "
																		+ pleilist
																				.getImage()
																		+ " from : "
																		+ pleilist
																				.getName());
													} catch (Exception error) {
														error.printStackTrace();
													}
												} else {
													Log.d(LOG_TAG,
															"Error dowloading image for pleilist :"
																	+ pleilist
																			.getName());
												}
											}
										});
							}
						}
					}

					listener.onAllPleilistsFinished(pleilists);

				} else {
					listener.onAllPleilistsFinished(null);
					Log.d(LOG_TAG,
							" Query error getting Pleilists: " + e.getMessage());
				}

			}

		});

	}

	protected static Pleilist readPleilistFromCursor(
			ParseObject parsedPleilist, ParseListener listener) {
		final Pleilist pleilist = new Pleilist();

		if (parsedPleilist.getString(NAME_TAG) != null) {
			pleilist.setName(parsedPleilist.getString(NAME_TAG));
		}
		if (parsedPleilist.getParseFile(IMAGE_TAG) != null) {
			pleilist.setImage(parsedPleilist.getParseFile(IMAGE_TAG).getName());
		}
		if (parsedPleilist.getParseFile(COVER_IMAGE_TAG) != null) {
			pleilist.setCoverImage(parsedPleilist.getParseFile(COVER_IMAGE_TAG)
					.getName());
		}
		if (parsedPleilist.getBoolean(DELETED_TAG) == true) {
			pleilist.setDeleted(Pleilist.DELETED);
		} else {
			pleilist.setDeleted(Pleilist.NOT_DELETED);
		}
		if (parsedPleilist.getBoolean(FLAGED_TAG) == true) {
			pleilist.setDeleted(Pleilist.FLAGED);
		} else {
			pleilist.setDeleted(Pleilist.NOT_FLAGED);
		}
		if (parsedPleilist.getParseObject(CATEGORY_TAG) != null) {
			String categoryId = parsedPleilist.getParseObject(CATEGORY_TAG)
					.getObjectId();
			pleilist.setCategoryId(categoryId);
		}
		if (parsedPleilist.getInt(ORDER_TAG) != -1) {
			pleilist.setOrder(parsedPleilist.getInt(ORDER_TAG));
		}

		Calendar updateCalendar = Calendar.getInstance();

		updateCalendar.setTimeInMillis(parsedPleilist.getUpdatedAt().getTime());

		pleilist.setUpdated_at(updateCalendar);

		return pleilist;
	}

	public static void updateTracks(Context context,
			final ParseListener listener) {
		Date updateAt = PleilistProvider.getLastUpdate(context);

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				PLEILIST_CLASS);
		if (updateAt != null) {
			query.whereGreaterThan(UPDATED_AT_TAG, updateAt);
		}
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> cList, ParseException e) {

				if (e == null) {
					ArrayList<Track> tracks = new ArrayList<Track>();

					for (ParseObject object : cList) {
						Track track = readTrackFromCursor(object, listener);
						track.setSystem_id(object.getObjectId());
						tracks.add(track);
					}

					listener.onAllTracksFinished(tracks);

				} else {
					listener.onAllTracksFinished(null);
					Log.d(LOG_TAG,
							" Query error getting Tracks: " + e.getMessage());
				}

			}

		});

	}

	protected static Track readTrackFromCursor(ParseObject parsedTrack,
			ParseListener listener) {
		final Track track = new Track();

		if (parsedTrack.getString(NAME_TAG) != null) {
			track.setName(parsedTrack.getString(NAME_TAG));
		}
		if (parsedTrack.getString(URL_TAG) != null) {
			track.setUrl(parsedTrack.getString(URL_TAG));
		}
		if (parsedTrack.getString(YOUTUBE_URL_TAG) != null) {
			track.setYoutubeUrl(parsedTrack.getString(YOUTUBE_URL_TAG));
		}
		if (parsedTrack.getString(ARTIST_TAG) != null) {
			track.setYoutubeUrl(parsedTrack.getString(ARTIST_TAG));
		}
		if (parsedTrack.getInt(ORDER_TAG) != -1) {
			track.setOrder(parsedTrack.getInt(ORDER_TAG));
		}

		Calendar updateCalendar = Calendar.getInstance();

		updateCalendar.setTimeInMillis(parsedTrack.getUpdatedAt().getTime());

		track.setUpdated_at(updateCalendar);

		return track;
	}

}
