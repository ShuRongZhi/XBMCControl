package com.dirs.xbmcc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import android.util.Log;

public class SocketHelper {
	private SocketHelper(){};
	private OutputStream out = null;
	private static SocketHelper Instance = null;
	private Socket mSocket = null;
	public synchronized static SocketHelper getInstance(){
		if(Instance == null){
			Instance = new SocketHelper();
		}
		return Instance;
	}
	
	public boolean Init(String host,int port){
		if(host.length() == 0 || port == 0){
			Log.d("debug","Error! HostAddress or Port is Empty!");
			return false;
		}
		try{
			if(mSocket == null){
				mSocket = new Socket();
			}
			SocketAddress socAddress = new InetSocketAddress(host,port);
			mSocket.connect(socAddress,5000);
			out = mSocket.getOutputStream();
			if(out == null){
				Log.d("debug","Error!GetOutputStream Failed!");
				return false;
			}
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean SendMsg(String msg){
		if(msg.length() == 0){
			Log.d("debug","Error! Message Is Empty!");
			return false;
		}
		if(mSocket == null){
			Log.d("debug","Error! mSocket Is NULL!");
			return false;
		}
		//发送消息后不关闭outputstream
		try{
			out.write(msg.getBytes());
		}catch(IOException e){
			e.printStackTrace();
			if(out != null){
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			return false;
		}
		return true;
	}
	
	public void Disconnection(){
		if(mSocket != null){
			try{
				mSocket.close();
				mSocket = null;
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		if(out != null){
			try{
				out.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
