package com.konka.lrcUtil;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class XMLContentHandler extends DefaultHandler {

	private ArrayList<String> lrcUrlList = new ArrayList<String>();
	private String tagName = null;// 当前解析的元素标签
	private StringBuilder lrcUrl  =new StringBuilder();
	public ArrayList<String> getLrcList() {
		return lrcUrlList;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		Log.d("Ouyang", "开始解析");
	}

	/***
	 * 文档解析完毕
	 */
	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	/***
	 * 元素解析开始
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		// super.startElement(uri, localName, qName, attributes);
		if (localName.equals("lrcy_elt")) {
			Log.d("Ouyang", "找到歌词");
			this.tagName = localName;			
		}
		lrcUrl.setLength(0);// 将字符长度设置为0 以便重新开始读取元素内的字符节点 
	}

	/***
	 * 元素解析完毕
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
//		super.endElement(uri, localName, qName);
		 if(localName.equals("lrcy_elt")){
			 if(lrcUrl.length()>0)
				{			 
				 Log.d("Ouyang", "lrcUrl:" + lrcUrl.toString());
				 lrcUrlList.add(lrcUrl.toString());
				}
			}
	}

	/***
	 * 解析内容
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		if (this.tagName != null) {
//		lrcUrl = new String(ch, start, length);
			lrcUrl.append(ch, start, length);//读取数据
		}
	}

}