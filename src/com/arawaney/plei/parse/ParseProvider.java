package com.arawaney.plei.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.arawaney.plei.db.provider.CategoryProvider;
import com.arawaney.plei.db.provider.CoverProvider;
import com.arawaney.plei.db.provider.PleilistProvider;
import com.arawaney.plei.db.provider.TrackProvider;
import com.arawaney.plei.listener.ParseListener;
import com.arawaney.plei.model.Category;
import com.arawaney.plei.model.Cover;
import com.arawaney.plei.model.Pleilist;
import com.arawaney.plei.model.Track;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

	private static final String FAVORITE_TAG = "favorites";

	private static final String UPDATED_AT_TAG = "updatedAt";

	private static final int PLEILIST_LIMIT = 250;
	private static final int TRACK_LIMIT = 1000;

	public static void logIn(String username, final String password,
			final Context context, final ParseListener listener) {

		ParseUser.logInInBackground(username, password, new LogInCallback() {

			public void done(ParseUser user, ParseException e) {
				boolean succes;
				if (user != null) {
					succes = true;

				} else {
					succes = false;
					Log.e(LOG_TAG, "Error by login :" + e.getMessage());
				}
				listener.OnLoginResponse(succes);
			}

		});
	}

	public static void initializeParse(final Context context) {

		Parse.initialize(context, "X5n0LmTy7RSybqBP2QWmGoQDQNUqa82bXwee6Sx3",
				"acnE1q6yEAQ24KMofj74rnOowvBN6TVqjKwomlcZ");

	}

	public static void updateCategories(final Context context,
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

					if (categories != null) {
						if (categories.size() > 0) {
							insertCategories(context, categories);
							listener.onAllCategoriesFinished(true);
						} else
							listener.onAllCategoriesFinished(false);
					} else
						listener.onAllCategoriesFinished(false);

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
														listener.onImageCoverDownloaded();
													} catch (Exception error) {
														error.printStackTrace();
													}
												} else {
													Log.d(LOG_TAG,
															"Error dowloading image for cover :"
																	+ cover.getName());
													Log.d(LOG_TAG,
															e.getMessage());
												}
											}
										});
							}
						}
					}
					if (covers != null) {
						if (covers.size() > 0) {
							insertCovers(context, covers);
							listener.onAllCoversFinished(true);
						} else
							listener.onAllCoversFinished(false);
					} else
						listener.onAllCoversFinished(false);

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
		query.setLimit(PLEILIST_LIMIT);
		if (updateAt != null) {
			query.whereGreaterThan(UPDATED_AT_TAG, updateAt);
		}
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> parsedPleilists, ParseException e) {

				if (e == null) {
					ArrayList<Pleilist> pleilists = new ArrayList<Pleilist>();

					Log.d(LOG_TAG,
							"Pleilists parsed : " + parsedPleilists.size());

					for (ParseObject parsedPleilist : parsedPleilists) {
						final Pleilist pleilist = readPleilistFromCursor(
								parsedPleilist, listener, context);
						pleilist.setSystem_id(parsedPleilist.getObjectId());
						pleilists.add(pleilist);

						if (pleilist.getImage() != null) {
							if (!imageExists(pleilist.getImage(), context)) {
								ParseFile applicantResume = (ParseFile) parsedPleilist
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
														// listener.onImagePleilistDownloaded();

													} catch (Exception error) {
														Log.d(LOG_TAG,
																"Error loading image for"
																		+ pleilist
																				.getName());
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

					DataUpdater.UpdateFavorites(listener, context);
					if (pleilists != null) {
						if (pleilists.size() > 0) {
							insertPleilists(context, pleilists);
							listener.onAllPleilistsFinished(true);
						} else
							listener.onAllPleilistsFinished(false);
					} else
						listener.onAllPleilistsFinished(false);

				} else {
					listener.onAllPleilistsFinished(null);
					Log.d(LOG_TAG,
							" Query error getting Pleilists: " + e.getMessage());
				}

			}

		});

	}

	protected static Pleilist readPleilistFromCursor(
			ParseObject parsedPleilist, final ParseListener listener,
			final Context context) {
		final ArrayList<Track> tracks = new ArrayList<Track>();

		final Pleilist pleilist = new Pleilist();

		if (parsedPleilist.getString(NAME_TAG) != null) {
			pleilist.setName(parsedPleilist.getString(NAME_TAG));
		}
		if (parsedPleilist.getParseFile(IMAGE_TAG) != null) {
			pleilist.setImage(parsedPleilist.getParseFile(IMAGE_TAG).getName());
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

		// ParseRelation<ParseObject> relation = parsedPleilist
		// .getRelation(TRACKS_TAG);
		// relation.getQuery().findInBackground(new FindCallback<ParseObject>()
		// {
		// public void done(List<ParseObject> parsedTracks, ParseException e) {
		// if (e != null) {
		// Log.e(LOG_TAG,
		// "No tracks found for pleilist : "
		// + pleilist.getName());
		// e.printStackTrace();
		//
		// } else {
		// for (ParseObject parsedTrack : parsedTracks) {
		// Track track = readTrackFromCursor(parsedTrack, context);
		// track.setSystem_id(parsedTrack.getObjectId());
		// tracks.add(track);
		//
		// insertTrackPleilistRelation(context, pleilist, parsedTrack, track);
		//
		// insertTrack(context, track);
		// }
		//
		// listener.onAllTracksFinished(tracks);
		// }
		// }
		//
		// });

		return pleilist;
	}

	private static void insertTrackPleilistRelation(final Context context,
			final String pleilistId, ParseObject parsedTrack, Track track) {
		TrackProvider.insertPelilistTrack(context, track.getSystem_id(),
				pleilistId, parsedTrack.getInt(ORDER_TAG));
	}

	private static void insertTrack(final Context context,
			final String pleilistId, ParseObject parsedTrack, Track track) {
		Track savedTrack = TrackProvider.readTrack(context,
				track.getSystem_id());
		if (savedTrack != null) {
			track.setId(savedTrack.getId());
			TrackProvider.updateTrack(context, track);
		} else {

			insertTrackPleilistRelation(context, pleilistId, parsedTrack, track);
			TrackProvider.insertTrack(context, track);
		}
	}

	private static void insertPleilists(final Context context,
			ArrayList<Pleilist> pleilists) {

		if (pleilists != null) {
			for (Pleilist pleilist : pleilists) {
				Pleilist savedPleilist = PleilistProvider.readPleilist(context,
						pleilist.getSystem_id());
				if (savedPleilist != null) {
					pleilist.setId(savedPleilist.getId());
					pleilist.setFavorite(savedPleilist.getFavorite());
					PleilistProvider.updatePleilist(context, pleilist);
				} else {
					PleilistProvider.insertPleilist(context, pleilist);
				}
			}
		}

	}

	private static void insertCovers(final Context context,
			ArrayList<Cover> covers) {
		if (covers != null) {
			for (Cover cover : covers) {
				Cover savedCover = CoverProvider.readCover(context,
						cover.getSystem_id());
				if (savedCover != null) {
					cover.setId(savedCover.getId());
					CoverProvider.updateCover(context, cover);
				} else {
					CoverProvider.insertCover(context, cover);
				}
			}
		}
	}

	private static void insertCategories(final Context context,
			ArrayList<Category> categories) {
		if (categories != null) {
			for (Category category : categories) {
				Category savedCategory = CategoryProvider.readCategory(context,
						category.getSystem_id());
				if (savedCategory != null) {
					category.setId(savedCategory.getId());
					CategoryProvider.updateCategory(context, category);
				} else {
					CategoryProvider.insertCategory(context, category);
				}
			}
		}

	}

	public static void updateTracks(final Context context,
			final ParseListener listener) {

		Date updateAt = TrackProvider.getLastUpdate(context);

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(TRACK_CLASS);
		if (updateAt != null) {
			query.whereGreaterThan(UPDATED_AT_TAG, updateAt);
		}
		query.setLimit(TRACK_LIMIT);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> cList, ParseException e) {

				if (e == null) {
					ArrayList<Track> tracks = new ArrayList<Track>();

					for (ParseObject object : cList) {
						Track track = readTrackFromCursor(object, context);
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
			final Context context) {
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
			track.setArtist(parsedTrack.getString(ARTIST_TAG));
		}

		Calendar updateCalendar = Calendar.getInstance();

		updateCalendar.setTimeInMillis(parsedTrack.getUpdatedAt().getTime());

		track.setUpdated_at(updateCalendar);

		return track;
	}

	public static void insertFavoritePleilist(final Pleilist pleilist,
			Context context, final ParseListener listener) {
		final ParseUser currentUser = ParseUser.getCurrentUser();

		if (currentUser == null) {
			listener.onSavedFAvoriteDone(false, pleilist);
		} else {

			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
					PLEILIST_CLASS);
			query.getInBackground(pleilist.getSystem_id(),
					new GetCallback<ParseObject>() {
						public void done(ParseObject parsedPleilist,
								ParseException e) {
							if (e == null) {
								ParseRelation<ParseObject> relation = currentUser
										.getRelation(FAVORITE_TAG);
								relation.add(parsedPleilist);
								currentUser
										.saveInBackground(new SaveCallback() {

											@Override
											public void done(ParseException e) {
												if (e == null) {
													listener.onSavedFAvoriteDone(
															true, pleilist);
												} else {
													listener.onSavedFAvoriteDone(
															false, pleilist);
												}

											}
										});
							} else {
								Log.d(LOG_TAG,
										"Pleilist :" + pleilist.getSystem_id()
												+ " not found!");
							}
						}
					});

		}

	}

	public static void downloadPleilistCoverImage(final Pleilist pleilist,
			final Context context, final ParseListener listener) {

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				PLEILIST_CLASS);
		query.getInBackground(pleilist.getSystem_id(),
				new GetCallback<ParseObject>() {
					public void done(ParseObject parsedPleilist,
							ParseException e) {
						if (e == null) {
							if (parsedPleilist.getParseFile(COVER_IMAGE_TAG) != null) {
								pleilist.setCoverImage(parsedPleilist
										.getParseFile(COVER_IMAGE_TAG)
										.getName());
							} else {
								Log.d(LOG_TAG, "Pleilist Cover image for :"
										+ pleilist.getName() + " not found!");
							}

							if (pleilist.getCoverImage() != null) {
								if (!imageExists(pleilist.getCoverImage(),
										context)) {
									ParseFile applicantResume = (ParseFile) parsedPleilist
											.get(COVER_IMAGE_TAG);
									applicantResume
											.getDataInBackground(new GetDataCallback() {
												public void done(byte[] image,
														ParseException e) {
													if (e == null) {
														FileOutputStream outputStream;

														try {
															outputStream = context
																	.openFileOutput(
																			pleilist.getCoverImage(),
																			Context.MODE_PRIVATE);
															outputStream
																	.write(image);
															outputStream
																	.close();
															Log.d(LOG_TAG,
																	"CoverImage : "
																			+ pleilist
																					.getImage()
																			+ " from : "
																			+ pleilist
																					.getName());
															
															PleilistProvider.updatePleilist(context, pleilist);
														
															listener.onPleilistCoverImageDownloaded(pleilist);

														} catch (Exception error) {
															Log.d(LOG_TAG,
																	"Error loading cover image for"
																			+ pleilist
																					.getName());
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

						} else {
							Log.d(LOG_TAG,
									"Pleilist :" + pleilist.getSystem_id()
											+ " not found!");
						}
					}
				});

	}

	public static void updateFavorites(final Context context,
			final ParseListener listener) {

		final ParseUser currentUser = ParseUser.getCurrentUser();

		if (currentUser == null) {
			listener.onFavoritesUpdated(false);
		} else {

			ParseRelation<ParseObject> relation = currentUser
					.getRelation(FAVORITE_TAG);
			relation.getQuery().findInBackground(
					new FindCallback<ParseObject>() {
						public void done(List<ParseObject> parsedPleilists,
								ParseException e) {
							if (e != null) {
								Log.d(LOG_TAG, "No favorites found for user : "
										+ currentUser.getUsername());
								listener.onFavoritesUpdated(false);
							} else {
								insertNewFavoriteds(context, parsedPleilists);
								removeUnfavoritedPleilists(context,
										parsedPleilists);
								listener.onFavoritesUpdated(true);

							}
						}

						private void insertNewFavoriteds(final Context context,
								List<ParseObject> parsedPleilists) {
							for (ParseObject parsedpleilist : parsedPleilists) {
								String pleiListId = parsedpleilist
										.getObjectId();
								Pleilist pleilist = PleilistProvider
										.readPleilist(context, pleiListId);
								if (pleilist != null) {
									if (pleilist.getFavorite() == Pleilist.NOT_FAVORITE) {
										pleilist.setFavorite(Pleilist.FAVORITE);
										PleilistProvider.updatePleilist(
												context, pleilist);
										downloadPleilistCoverImage(pleilist, context, listener);
									}
								}

							}
						}

						private void removeUnfavoritedPleilists(
								final Context context,
								List<ParseObject> parsedPleilists) {
							ArrayList<Pleilist> favoritesPlailists = PleilistProvider
									.getFavoritesPleiLists(context);
							boolean isNotFavorite = true;
							if (favoritesPlailists != null) {
								for (Pleilist pleilist : favoritesPlailists) {
									for (ParseObject parsedPleilist : parsedPleilists) {
										if (pleilist.getSystem_id().equals(
												parsedPleilist.getObjectId())) {
											isNotFavorite = false;
										}
									}
									if (isNotFavorite) {
										pleilist.setFavorite(Pleilist.NOT_FAVORITE);
										PleilistProvider.updatePleilist(
												context, pleilist);
									}
									isNotFavorite = true;
								}
							}

						}
					});

		}

	}

	public static void removeFavoritePleilist(final Context context,
			final ParseListener listener, final Pleilist pleilist) {
		final ParseUser currentUser = ParseUser.getCurrentUser();

		if (currentUser == null) {
			listener.onFavoritedRemoved(false, pleilist);
		} else {
			final ParseRelation<ParseObject> relation = currentUser
					.getRelation(FAVORITE_TAG);
			relation.getQuery().findInBackground(
					new FindCallback<ParseObject>() {
						public void done(List<ParseObject> parsedPleilists,
								ParseException e) {
							if (e != null) {
								Log.d(LOG_TAG, "No favorites found for user : "
										+ currentUser.getUsername());
								listener.onFavoritedRemoved(false, pleilist);

							} else {
								for (ParseObject parsedPLeilist : parsedPleilists) {
									if (parsedPLeilist.getObjectId().equals(
											pleilist.getSystem_id())) {
										relation.remove(parsedPLeilist);
										currentUser
												.saveInBackground(new SaveCallback() {

													@Override
													public void done(
															ParseException e) {
														if (e == null) {
															listener.onFavoritedRemoved(
																	true,
																	pleilist);
														} else {
															listener.onFavoritedRemoved(
																	false,
																	pleilist);
														}

													}
												});
									}
								}

							}
						}
					});

		}
	}

	public static void updateTracksByPleilist(final Context context,
			final ParseListener listener, final String pleilistId) {

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				PLEILIST_CLASS);

		query.getInBackground(pleilistId, new GetCallback<ParseObject>() {

			@Override
			public void done(final ParseObject parsedPleilist, ParseException e) {

				if (e == null) {
					ParseRelation<ParseObject> relation = parsedPleilist
							.getRelation(TRACKS_TAG);
					Date updateAt = TrackProvider.getLastUpdateByPleilist(
							context, pleilistId);
					ParseQuery<ParseObject> query = relation.getQuery();
					if (updateAt != null) {
						query.whereGreaterThan(UPDATED_AT_TAG, updateAt);
					}
					query.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> parsedTracks,
								ParseException e) {
							if (e != null) {
								Log.d(LOG_TAG,
										"No tracks found for pleilist : "
												+ parsedPleilist
														.getString(NAME_TAG));
								e.printStackTrace();

							} else {
								if (parsedTracks.size() > 0) {
									for (ParseObject parsedTrack : parsedTracks) {
										Track track = readTrackFromCursor(
												parsedTrack, context);
										track.setSystem_id(parsedTrack
												.getObjectId());

										insertTrack(context, pleilistId,
												parsedTrack, track);
									}
									listener.onAllTracksByPLeilistFinished();
								}

							}
						}

					});
				} else {
					Log.d(LOG_TAG, "No  pleilist found for  : " + pleilistId);
				}

			}
		});

	}

}
