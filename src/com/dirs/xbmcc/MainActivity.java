package com.dirs.xbmcc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button Btn_Connection = null;
	private EditText Edit_IPAddr = null;
	private String IPAddress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.d("debug","MainActivity Creaet!");
		
		Btn_Connection = (Button)findViewById(R.id.Connection);
		Btn_Connection.setOnClickListener(new onConnectionClient());
		Edit_IPAddr = (EditText)findViewById(R.id.IPAddr);
		Edit_IPAddr.setOnClickListener(new onEditClick());
		
		/*
		//Debug!
		Intent i = new Intent();
		i.setClass(getApplicationContext(), ControlActivity.class);
		startActivity(i);
		finish();
		//Debug!
		*/
	}
	
	class onConnectionClient implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String ipAddr = Edit_IPAddr.getText().toString();
			if(ipAddr.length() == 0){
				Toast.makeText(getApplicationContext(), R.string.IP_Empty,Toast.LENGTH_SHORT).show();
			}
			else
			{
				Log.d("debug","IP Reg:" + getString(R.string.IPReg));
				Pattern pattern = Pattern.compile(getString(R.string.IPReg));
				Matcher matcher = pattern.matcher(ipAddr);
				if(!matcher.matches())
				{
					Toast.makeText(getApplicationContext(), R.string.IP_Error,Toast.LENGTH_LONG).show();
					return;
				}
			}
			IPAddress = ipAddr;
			Log.d("debug","IP Adress : " + IPAddress);
			Toast.makeText(getApplicationContext(),R.string.Connecting,Toast.LENGTH_LONG).show();
			new ConnectionThread().start();
		}
		
	}
	
	class onEditClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Edit_IPAddr.setText("");
		}
		
	}
	
	class ConnectionThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Log.d("debug","ConnectionThread Start");
			SocketHelper mSocketHelper;
			mSocketHelper = SocketHelper.getInstance();
			Message msg = new Message();
			if(mSocketHelper.Init(IPAddress,3471)){
				msg.what = 1;
				if(!MainActivity.this.UpdateUIHandler.sendMessage(msg)){
					Log.d("debug","Send Message Failed!");
				}
			}
			else{
				msg.what = 0;
				mSocketHelper.Disconnection();
				if(!MainActivity.this.UpdateUIHandler.sendMessage(msg)){
					Log.d("debug","Send Message Failed!");
				}
			}
		}

		
	}
	Handler UpdateUIHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			//连接成功
			case 1:
				//结束当前Activity,并开启ControlActivity
				Intent startActivityIntent = new Intent();
				startActivityIntent.setClass(getApplicationContext(), ControlActivity.class);
				startActivity(startActivityIntent);
				finish();
				break;
			//连接失败
			case 0:
				Toast.makeText(getApplicationContext(), R.string.ConnectionFailed,Toast.LENGTH_LONG).show();;
				break;
			}
		}
		
	};
	
}
