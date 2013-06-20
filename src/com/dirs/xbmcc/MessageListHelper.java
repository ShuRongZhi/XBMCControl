package com.dirs.xbmcc;

import java.util.ArrayList;

public class MessageListHelper{
	private MessageListHelper(){};
	private static MessageListHelper instance = null;
	private static ArrayList<String> list;
	
	
	public synchronized static MessageListHelper getInstance(){
		if(instance == null){
			instance = new MessageListHelper();
			list = new ArrayList<String>();
		}
		return instance;
	}
	
	public synchronized boolean isMessageListEmpty(){
		return list.isEmpty();
	}
	
	public synchronized void putMessage(String msg){
		list.add(msg);
	}
	
	public synchronized String getMessage(){
		if(!list.isEmpty()){
			String str = list.get(0);
			list.remove(0);
			return str;
		}
		return "";
	}
}