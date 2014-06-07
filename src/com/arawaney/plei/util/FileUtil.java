package com.arawaney.plei.util;

import java.io.File;

import android.content.Context;

public class FileUtil {
	
	public static boolean imageExists(String imageName, Context context) {
		File file = new File(context.getFilesDir(), imageName);
		if (file.exists())
			return true;
		else
			return false;
	}

}
