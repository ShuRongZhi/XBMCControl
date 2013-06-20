package com.dirs.xbmcc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ControlActivity extends Activity {

	private Button Btn_ESC = null;
	private Button Btn_Up = null;
	private Button Btn_Down = null;
	private Button Btn_Left = null;
	private Button Btn_Right = null;
	private Button Btn_Enter = null;
	private Button Btn_OpenKeyboard = null;
	private Button Btn_Disconnect = null;
	private Button Btn_Del = null;
	private EditText Edit_GetInput = null;

	private boolean isStop;
	private boolean isKeyBoardOpen;
	private boolean isCapsLock;

	private SocketHelper mSocket = null;
	private MessageListHelper mListHelper = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);
		Log.d("debug", "ControlActivity Create!");
		mListHelper = MessageListHelper.getInstance();

		isStop = false;
		isKeyBoardOpen = false;
		isCapsLock = false;

		Btn_ESC = (Button) findViewById(R.id.Btn_ESC);
		Btn_Up = (Button) findViewById(R.id.Btn_Up);
		Btn_Down = (Button) findViewById(R.id.Btn_Down);
		Btn_Left = (Button) findViewById(R.id.Btn_Left);
		Btn_Right = (Button) findViewById(R.id.Btn_Right);
		Btn_Enter = (Button) findViewById(R.id.Btn_Enter);
		Btn_OpenKeyboard = (Button) findViewById(R.id.Btn_OpenKeyboard);
		Btn_Del = (Button)findViewById(R.id.Btn_Del);
		Btn_Disconnect = (Button)findViewById(R.id.Btn_Disconnect);
		Edit_GetInput = (EditText) findViewById(R.id.GetInput);

		onButtonClickListener ClickListener = new onButtonClickListener();

		Btn_ESC.setOnClickListener(ClickListener);
		Btn_Up.setOnClickListener(ClickListener);
		Btn_Down.setOnClickListener(ClickListener);
		Btn_Left.setOnClickListener(ClickListener);
		Btn_Right.setOnClickListener(ClickListener);
		Btn_Enter.setOnClickListener(ClickListener);
		Btn_Del.setOnClickListener(ClickListener);
		Btn_Disconnect.setOnClickListener(ClickListener);
		Btn_OpenKeyboard.setOnClickListener(ClickListener);

		new SendThread().start();

	}

	class onButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.Btn_Down:
				mListHelper.putMessage("VK_DOWN");
				break;
			case R.id.Btn_Enter:
				mListHelper.putMessage("VK_RETURN");
				break;
			case R.id.Btn_ESC:
				mListHelper.putMessage("VK_ESCAPE");
				break;
			case R.id.Btn_Left:
				mListHelper.putMessage("VK_LEFT");
				break;
			case R.id.Btn_OpenKeyboard:
				Log.d("debug", "Button OpenKeyBoard Click!");
				if (!isKeyBoardOpen) {
					Btn_Del.setVisibility(View.VISIBLE);
					Toast.makeText(getApplicationContext(), R.string.InputType,Toast.LENGTH_SHORT).show();
					Edit_GetInput.setVisibility(View.VISIBLE);
					Edit_GetInput.addTextChangedListener(new TextWatcher() {
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
							// TODO Auto-generated method stub
							//如果before等于1，表示删除
							if(before != 1){
								char ch = s.toString().charAt(start);
								Log.d("debug","Char is:" + ch);
								if(ch >= 'A' && ch <= 'Z'){
									if(!isCapsLock){
										Log.d("debug","打开大写");
										mListHelper.putMessage("VK_CAPITAL");
									}
								}
								if(ch >= 'a' && ch <= 'z'){
									int i = 'A' - 'a';
									//将小写转换为大写
									ch = (char) ((char)ch + i);
									if(isCapsLock){
										Log.d("debug","关闭大写");
										isCapsLock = false;
										mListHelper.putMessage("VK_CAPITAL");
									}
								}
								mListHelper.putMessage("VK_" + ch);
							}else{
								
								Log.d("debug","Delate Char!");
								mListHelper.putMessage("VK_BACK");
							}
						}
						
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,
								int after) {
							// TODO Auto-generated method stub
							//old = s.toString();
						}
						
						@Override
						public void afterTextChanged(Editable s) {
							// TODO Auto-generated method stub
							
						}
					});
					
					isKeyBoardOpen = true;
					
				} else {
					Btn_Del.setVisibility(View.INVISIBLE);
					Edit_GetInput.setVisibility(View.INVISIBLE);
					isKeyBoardOpen = false;
				}
				break;
			case R.id.Btn_Right:
				mListHelper.putMessage("VK_RIGHT");
				break;
			case R.id.Btn_Up:
				mListHelper.putMessage("VK_UP");
				break;
			case R.id.Btn_Del:
				mListHelper.putMessage("VK_BACK");
				break;
			case R.id.Btn_Disconnect:
				Toast.makeText(getApplicationContext(), R.string.Disconnection,Toast.LENGTH_LONG).show();
				mSocket.Disconnection();
				//结束掉Activity
				finish();
			}
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("debug", "ControlActivity Destory!");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("debug", "ControlActivity Pause!");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("debug", "ControlActivity Start!");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("debug", "ControlActiviy Stop!");
		isStop = true;
		mSocket.Disconnection();
	}

	class SendThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Log.d("debug", "Send Message Thread Run!");
			mSocket = SocketHelper.getInstance();
			while (!isStop) {
				if (mListHelper.isMessageListEmpty()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					String str = mListHelper.getMessage();
					Log.d("debug", "Message is " + str);
					if (!mSocket.SendMsg(str)) {
						Message msg = new Message();
						msg.what = -1;
						ControlActivity.this.UpdateUI.sendMessage(msg);
						mSocket.Disconnection();
					}
				}
			}
		}

	}

	Handler UpdateUI = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			// 发送数据失败
			case -1:
				Toast.makeText(getApplicationContext(), R.string.SendFail,
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

}
