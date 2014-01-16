package com.encryptor;

import java.nio.charset.Charset;

import android.os.Bundle;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ENCRYPTOR_MAIN extends Activity {

	EditText editTextContent;
    EditText editTextKey;
	
	//for debug
    boolean DebugOn = false;
	TextView textViewDebug;
	ToggleButton toggleButtonDebug;
	/////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encryptor__main);
		editTextContent = (EditText)findViewById(R.id.editTextContent);
        editTextKey = (EditText)findViewById(R.id.editTextKey);
        
        //handle encryption button event
        Button btn = (Button)findViewById(R.id.buttonEncrypt);
        btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0)
			{
				updateContent(ENCRYPTOR_MODE.ENCRYPTION);
				
			}});
        // handle decryption button event
        btn = (Button)findViewById(R.id.buttonDecrypt);
        btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v)
			{
				updateContent(ENCRYPTOR_MODE.DECRYPTION);
				
			}});
        // copy content to clipboard
        btn = (Button)findViewById(R.id.buttonCopyToClip);
        btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v)
			{
				ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("Copy Content To Clipboard", 
						editTextContent.getText().toString());
				clipboard.setPrimaryClip(clip);
				
			}});
        // read content from clipboard
        btn = (Button)findViewById(R.id.buttonPasteFromClip);
        btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v)
			{
				ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
				if(clipboard.hasPrimaryClip())
				{
					ClipData clip = clipboard.getPrimaryClip();
					if(clip.getItemCount()>0)
					{
						String data = (String) clip.getItemAt(0).coerceToText(v.getContext());
						editTextContent.setText(data);
					}
				}
				
			}});

		//for debug
		textViewDebug = (TextView)findViewById(R.id.textViewDebug);
		toggleButtonDebug = (ToggleButton)findViewById(R.id.toggleButtonDebug);
		toggleButtonDebug.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1 == true)
				{
					DebugOn = true;
				}
				else
				{
					DebugOn = false;
				}
				
			}});
		////////////////////////////////////

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.encryptor__main, menu);
		return true;
	}
	
	//update content of EditText
	private void updateContent(ENCRYPTOR_MODE mode)
	{
		//verify if key value is valid
		int key = Integer.valueOf(editTextKey.getText().toString());
		if(key<1 || key>255)
		{
			Toast.makeText(this, R.string.KEY_ERR, Toast.LENGTH_SHORT).show();
			return;
		}
		
		//for debug
		if(DebugOn)
		{
		textViewDebug.append("\nkey value:"+key+"\n");
		}
		////////////////////////////////////////////
		
		if(mode == ENCRYPTOR_MODE.ENCRYPTION)
		{
			//encrypt message
			String tmp = encryptString(editTextContent.getText().toString(), (byte)key);
			editTextContent.setText(tmp);
		}
		else if(mode == ENCRYPTOR_MODE.DECRYPTION)
		{
			//decrypt message
			String tmp = decryptString(editTextContent.getText().toString(), (byte)key);
			editTextContent.setText(tmp);
		}
		else
		{
			//do nothing
		}
	}
	
	
	private String encryptString(String msg, byte key)
	{
		//encode string with UTF-16 firstly, then get byte array
		byte[] bytes = msg.getBytes(Charset.forName("UTF-16LE"));
		
		//for debug
		if (DebugOn)
		{
			textViewDebug.append("\nmsg array using UTF-16LE:");
			for (byte b : bytes)
			{
				textViewDebug.append(String.format("%02x ", b));
			}
			textViewDebug.append("\n");
		}
		/////////////////////////////
		
		//shift each byte by 1
		for(int i=0; i<bytes.length; i++)
		{
			bytes[i] += key;
		}
		//change byte array back to string format using UTF-16 charset
		String transformed = new String(bytes, Charset.forName("UTF-16LE"));
		return transformed;
	}
	
	private String decryptString(String transformed, byte key)
	{
		//encode with UTF-16, then get byte array
		byte[] bytes = transformed.getBytes(Charset.forName("UTF-16LE"));
		
		//for debug
		if(DebugOn)
		{
			textViewDebug.append("\ntransformed msg array using UTF-16LE:");
			for (byte b : bytes)
			{
				textViewDebug.append(String.format("%02x ", b));
			}
			textViewDebug.append("\n");
		}
				/////////////////////////////
				
		//decrease 1 to get original byte
		for(int i=0; i<bytes.length; i++)
		{
			bytes[i] -= key;
		}
		//transform byte array to string using charset UTF-16
		String original = new String(bytes, Charset.forName("UTF-16LE"));
		
		//for debug
		if(DebugOn)
		{
			textViewDebug.append("\nmsg array restored using UTF-16LE:");
			for (byte b : bytes)
			{
				textViewDebug.append(String.format("%02x ", b));
			}
			textViewDebug.append("\n");
		}
				/////////////////////////////
		
		return original;
	}

	private enum ENCRYPTOR_MODE
	{
		ENCRYPTION,
		DECRYPTION
	}

}
