package com.konka.lrc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import android.text.TextUtils;
import android.util.Log;

public class ParserLrc {
	public static List<LyricObject> parserFile(String path) throws IOException {
		if (TextUtils.isEmpty(path)) {
			Log.d("Ouyang", "TextUtils.isEmpty");
			return null;
		}
		return parserFile(new File(path));
	}

	public static List<LyricObject> parserFile(File file) throws IOException {
		if (!file.exists()) {
			Log.d("Ouyang", "!file.exists()");
			return null;
		}
		ArrayList<LyricObject> mLyricObjects = new ArrayList<LyricObject>();

		FileInputStream stream = new FileInputStream(file);// context.openFileInput(file);
		DetectEncoding de = new DetectEncoding();
		String decodeMode = de.detectEncoding(file);
		// final String plurl = IOUtils.toString(stream, decodeMode);
		final String plurl = IOUtils.toString(stream);
		String regEx = "\\[(\\d{2}):(\\d{2})\\.(\\d{2})\\]([^\\[]+)";

		Pattern pattern = Pattern.compile(regEx);
		Matcher m = pattern.matcher(plurl);
		while (m.find()) {
			try {
				String lrc = m.group(4).trim();
				if (TextUtils.isEmpty(lrc)) {
					continue;
				}
				int minute = Integer.parseInt(m.group(1));
				int second = Integer.parseInt(m.group(2));
				int millisecond = Integer.parseInt(m.group(3));
				int time = minute * 60 * 1000 + second * 1000 + millisecond
						* 10;
				LyricObject lyricObject = new LyricObject();
				lyricObject.setBegintime(time);
				lyricObject.setLrc(lrc);
				mLyricObjects.add(lyricObject);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
//		for (LyricObject lo : mLyricObjects) {
//			Log.d("Ouyang", "歌词：" + lo.getLrc());
//			System.out.println("歌词：" + lo.getLrc());
//		}
		return mLyricObjects;
	}
}
