package com.arawaney.plei.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.util.Log;

public class YoutubeUtil {

	public static String getYoutubeVideoId(String youtubeUrl) {
		String video_id = "18";
		if (youtubeUrl != null && youtubeUrl.trim().length() > 0
				&& youtubeUrl.startsWith("http")) {

			String expression = "^.*((youtu.be"
					+ "\\/)"
					+ "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var
																								// regExp
																								// =
																								// /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
			CharSequence input = youtubeUrl;
			Pattern pattern = Pattern.compile(expression,
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(input);
			if (matcher.matches()) {
				String groupIndex1 = matcher.group(7);
				if (groupIndex1 != null && groupIndex1.length() == 11)
					video_id = groupIndex1;
			}
		}
		if (video_id.equals("")) {

			Log.d("YoutubeUtil", "Error getting youtube id from: " + youtubeUrl);
		}
		return video_id;
	}

	public static String getVideoInfo(final String getVideoInfoUrl) {

		String targetFormat = "17";

		String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/537.4";
		String encoding = "UTF-8";

		CookieStore cookieStore = new BasicCookieStore();
		HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(getVideoInfoUrl);
		httpget.setHeader("User-Agent", userAgent);

		try {
//			Log.d("LOG_TAG", "Executing " + getVideoInfoUrl);
			HttpResponse response = httpclient.execute(httpget, localContext);
			HttpEntity entity = response.getEntity();
			if (entity != null
					&& response.getStatusLine().getStatusCode() == 200) {
				InputStream instream = entity.getContent();
				String videoInfo = getStringFromInputStream(encoding, instream);
				if (videoInfo != null && videoInfo.length() > 0) {

					// Log.d("LOG_TAG", videoInfo);
					String decoded = URLDecoder.decode(videoInfo, encoding);

					Hashtable<String, String> keyValues = decodeUrlFormEncoded(videoInfo);
					String encodedStreamMap = keyValues
							.get("url_encoded_fmt_stream_map");
					if (encodedStreamMap != null) {
						String[] formats = encodedStreamMap.split(",");
						for (String fmt : formats) {
							Hashtable<String, String> formatInfo = decodeUrlFormEncoded(fmt);
							String itag = formatInfo.get("itag");
							if (itag.equals(targetFormat)) {
								String url = formatInfo.get("url");
								return url;
							}
						}
					}

				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static Hashtable<String, String> decodeUrlFormEncoded(
			String videoInfo) throws UnsupportedEncodingException {
		String[] lArgs = videoInfo.split("&");
		Hashtable<String, String> keyValues = new Hashtable<String, String>();
		for (int j = 0; j < lArgs.length; j++) {
			String[] keyValuePairs = lArgs[j].split("=");
			if (keyValuePairs != null) {
				if (keyValuePairs.length >= 2) {
					keyValues.put(keyValuePairs[0],
							URLDecoder.decode(keyValuePairs[1], "UTF-8"));
//					Log.d("LOG_TAG",
//							keyValuePairs[0]
//									+ " : "
//									+ URLDecoder.decode(keyValuePairs[1],
//											"UTF-8"));
				}
			}
		}
		return keyValues;
	}

	private static String getStringFromInputStream(String encoding,
			InputStream instream) throws UnsupportedEncodingException,
			IOException {
		Writer writer = new StringWriter();

		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(instream,
					encoding));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			instream.close();
		}
		String result = writer.toString();
		return result;
	}

}
