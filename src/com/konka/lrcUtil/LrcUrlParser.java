package com.konka.lrcUtil;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class LrcUrlParser {

	/***
	 * 解析百度音乐文件信息
	 * 形如：http://box.zhangmen.baidu.com/x?op=12&count=1&title=洋葱$$平安
	 * @param inputStream
	 * @return
	 */
	public static String parasXML(InputStream inputStream) {
		try {
			String lrcid = null, url = null;
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(inputStream);
			Element root = dom.getDocumentElement();
			Node count = root.getFirstChild();
			NodeList items = root.getElementsByTagName("url");
			if (items.getLength() <= 0) {
				System.out.println("歌曲未找到");
				return null;
			} else {
				Element urlNode = (Element) items.item(0);
				// System.out.println(urlNode.getTextContent());
				Element lrcidNode = (Element) (urlNode
						.getElementsByTagName("lrcid").item(0));
				Element encodeNode = (Element) urlNode.getElementsByTagName(
						"encode").item(0);
				Element decodeNode = (Element) urlNode.getElementsByTagName(
						"decode").item(0);
				if (encodeNode == null) {
					System.out.println("歌曲信息为空，无法下载");
					return null;
				} else {
					System.out.println("encode=" + encodeNode.getTextContent());
				}
				if (lrcidNode == null) {
					System.out.println("lrcid == null");
				} else {
					System.out.println(lrcidNode.getNodeName());
				}
				if ("lrcid".equals(lrcidNode.getNodeName())) {
					System.out.println(lrcidNode.getTextContent());
					lrcid = lrcidNode.getTextContent();
				} else {
					System.out.println(lrcidNode.getNodeName());
				}
				// 获取encode里的值
				String temp1 = encodeNode.getTextContent();
				// 获取decodeNode的值
				String temp2 = decodeNode.getTextContent();
				StringBuffer buffer = new StringBuffer();

				// 接下来是将temp1和temp2进行分割解码合并在一起
				String[] arrayTemp1 = temp1.split("/");
				for (int i = 0; i < arrayTemp1.length - 1; i++) {
					buffer.append(arrayTemp1[i] + "/");
				}
				// 把解码的地址合在一起
				buffer.append(temp2);
				url = buffer.toString();
				url = url + "##" + lrcid;
				System.out.println("xml:url=" + url);
				return url;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 解析百度音乐歌词
	 * 形如：http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.search.lrcys&format=xml&query=征服$$$那英
	 * @param inStream
	 * @return
	 */
	public static ArrayList<String> parserXML(InputStream inStream) {
		try {
			// 创建解析器
			Log.d("Ouyang", "创建解析器");
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			XMLContentHandler handler = new XMLContentHandler();
			saxParser.parse(inStream, handler);
			inStream.close();
			return handler.getLrcList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
