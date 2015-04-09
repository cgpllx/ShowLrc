package com.konka.lrcUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.konka.lrc.LyricObject;
import com.konka.lrc.LyricView;
import com.konka.lrc.ParserLrc;

import android.os.Environment;
import android.util.Log;

public class LrcUtil {

	private static String currentSong = "";
	private static List<LyricObject> lrcList = null;

	/***
	 * 根据歌名及歌手搜索歌词文件的下载地址
	 * 
	 * @param title
	 * @param singer
	 * @return
	 */
	public static ArrayList<String> searchLrcUrls(String title, String singer) {
		String commonStr = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.search.lrcys&format=xml&query=";
		String url = commonStr + title + "$$$" + singer;
		ArrayList<String> resultList = null;
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpUriRequest req = new HttpGet(url);
			HttpResponse resp = client.execute(req);
			HttpEntity ent = resp.getEntity();
			InputStream inStream = ent.getContent(); // 将文件导入流，因此用InputStream
			resultList = LrcUrlParser.parserXML(inStream);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Ouyang", "发生错误：" + e.toString());
		}
		return resultList;
	}

	public static ArrayList<String> searchLrcUrls(String title) {
		return searchLrcUrls(title, "");
	}

	/***
	 * 下载歌词文件
	 * 
	 * @param lrcUrl
	 * @param savePath
	 */
	public static void downLoadLrcFromUrl(String lrcUrl, String savePath) {
		Log.d("Ouyang", "开始下载");
		HttpURLConnection connection = null;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(savePath);// 设置输出流路径

			URL url = new URL(lrcUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5 * 1000);
			connection.setReadTimeout(5 * 1000);
			connection.setRequestMethod("GET");
			is = connection.getInputStream();

			byte[] buffer = new byte[1024];// 设置缓冲值
			int length = -1;
			while ((length = is.read(buffer)) != -1) {
				fos.write(buffer, 0, length);
			}
			// 完毕，关闭所有链接
			fos.close();
			is.close();
			Log.d("Ouyang", "下载完成");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 根据歌词地址，下载并保存歌词文件
	 * 
	 * @param lrcUrl
	 * @param fileName
	 * @return
	 */
	public static String saveLrc(String lrcUrl, String fileName) {
		String savePath = Environment.getExternalStorageDirectory()
				+ "/konka/lrc/";
		String lrcSavePath = savePath + fileName + ".lrc";
		createDir(savePath);
		createFile(lrcSavePath);
		downLoadLrcFromUrl(lrcUrl, lrcSavePath);
		return lrcSavePath;
	}

	/***
	 * 根据歌名及歌手，获取歌词文件的本地路径
	 * 
	 * @param title
	 * @param singer
	 * @return
	 */
	public static String getLrcFile(String title, String singer) {
		String savePath = Environment.getExternalStorageDirectory()
				+ "/konka/lrc/";
		String lrcSavePath = savePath + title + ".lrc";
		String path = lrcSavePath;
		if (isFileExist(lrcSavePath) == false) {// 如果歌词文件不存在则上网搜索
			Log.d("Ouyang", "歌词文件不存在");
			ArrayList<String> urlList = LrcUtil.searchLrcUrls(title, singer);
			if (urlList != null && urlList.size() > 0)
				path = LrcUtil.saveLrc(urlList.get(0), title);
		} else {
			Log.d("Ouyang", "歌词已经存在");
		}
		return path;
	}

	/***
	 * 根据歌名以及下载地址，返回歌词文件的本地路径
	 * 
	 * @param title
	 * @param url
	 * @return
	 */
	public static String getOnlineLrcFile(String title, String url) {
		String savePath = Environment.getExternalStorageDirectory()
				+ "/konka/lrc/";
		String lrcSavePath = savePath + title + ".lrc";
		String path = lrcSavePath;
		if (isFileExist(lrcSavePath) == false) {// 如果歌词文件不存在则上网搜索
			path = LrcUtil.saveLrc(url, title);
		} else {
			Log.d("Ouyang", "歌词已经存在");
		}
		return path;
	}

	/***
	 * 最终调用方法，根据歌曲名以及歌手，显示歌词（用以播放本地音乐歌词）
	 * 
	 * @param lrcView
	 * @param title
	 * @param singer
	 */
	public static void showLrc(final LyricView lrcView, final String title,
			final String singer) {
		Log.d("Ouyang", "执行showLrc()");
		if (currentSong.equals(title + "$$$" + singer) && lrcList != null)// 判断，如果与当前播放歌词一致，则不重新加载
		{
			lrcView.setLyricObjects(lrcList);
			return;
		}
		currentSong = title + "$$$" + singer;
		lrcView.setFirst(true);//标记为第一次加载
		Log.d("Ouyang", "加载歌词：" + currentSong);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String path = LrcUtil.getLrcFile(title, singer);
				// List<LyricObject> lrcList = null;
				try {
					Log.d("Ouyang", "歌词地址：" + path);
					lrcList = ParserLrc.parserFile(path);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("Ouyang", "解析歌词文件出错：" + e.toString());
				}
				if (currentSong.equals(title + "$$$" + singer))// 避免多线程导致歌词显示不对号
				{
					Log.d("Ouyang", "显示歌词：" + currentSong);
					lrcView.setLyricObjects(lrcList);
				}
			}
		}).start();
	}

	/***
	 * 最终调用方法，根据歌曲名以及歌词下载地址，显示歌词（用以播放在线音乐歌词）
	 * 
	 * @param lrcView
	 * @param title
	 * @param singer
	 */
	public static void showOnlineLrc(final LyricView lrcView,
			final String title, final String lrcUrl) {
		if (currentSong.equals( title + "$$$" + lrcUrl) && lrcList != null)// 判断，如果与当前播放歌词一致，则不重新加载
		{
			lrcView.setLyricObjects(lrcList);
			return;
		}
		currentSong = title + "$$$" + lrcUrl;
		lrcView.setFirst(true);//标记为第一次加载
		Log.d("Ouyang", "加载歌词：" + currentSong);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String path = LrcUtil.getOnlineLrcFile(title, lrcUrl);
//				List<LyricObject> lrcList = null;
				try {
					Log.d("Ouyang", "歌词地址：" + path);
					lrcList = ParserLrc.parserFile(path);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("Ouyang", "解析歌词文件出错：" + e.toString());
				}
				if (currentSong.equals(title + "$$$" + lrcUrl))// 避免多线程导致歌词显示不对号
				{
					Log.d("Ouyang", "显示歌词：" + currentSong);
					lrcView.setLyricObjects(lrcList);
				}
			}
		}).start();
	}

	/***
	 * 跟路径创建目录，非覆盖型
	 * 
	 * @param savePath
	 */
	public static void createDir(String savePath) {
		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	/***
	 * 根据路径创建文件，覆盖型
	 * 
	 * @param savePath
	 */
	public static void createFile(String savePath) {
		File file = new File(savePath);
		if (file.exists()) {
			file.delete();
		}
	}

	/***
	 * 判断文件是否存在
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isFileExist(String path) {
		try {
			File file = new File(path);
			if (file.exists())
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
