package com.encryptor;

import java.nio.charset.Charset;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ENCRYPTOR_MAIN extends Activity {

	RadioGroup radioGroupEncryptionStatus;
	EditText editTextContent;
	ENCRYPTION_STATUS EncryptionStatus;
    EditText editTextKey;
	
	//for debug
	TextView textViewDebug;
	/////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encryptor__main);
		radioGroupEncryptionStatus = (RadioGroup)findViewById(R.id.radioGroupEncryptionStatus);
		editTextContent = (EditText)findViewById(R.id.editTextContent);
        editTextKey = (EditText)findViewById(R.id.editTextKey);

		//for debug
		textViewDebug = (TextView)findViewById(R.id.textViewDebug);
		////////////////////////////////////

		Button btn = (Button)findViewById(R.id.buttonUpdate);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				updateContent();
			}});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.encryptor__main, menu);
		return true;
	}
	
	private void updateEncryptionStatus()
	{
		switch(radioGroupEncryptionStatus.getCheckedRadioButtonId())
		{
		case R.id.radioEncrypted:
			EncryptionStatus = ENCRYPTION_STATUS.ENCRYPTED;
			break;
		case R.id.radioDecrypted:
			EncryptionStatus = ENCRYPTION_STATUS.DECRYPTED;
			break;
			default:
				break;
		}
	}
	
	//update content of EditText
	private void updateContent()
	{
		//verify if key value is valid
		int key = Integer.valueOf(editTextKey.getText().toString());
		if(key<1 || key>255)
		{
			Toast.makeText(this, R.string.KEY_ERR, Toast.LENGTH_SHORT).show();
			return;
		}
		
		//for debug
		textViewDebug.append("\nkey value:"+key+"\n");
		////////////////////////////////////////////
		
		updateEncryptionStatus();
		if(EncryptionStatus == ENCRYPTION_STATUS.ENCRYPTED)
		{
			//encrypt message
			String tmp = encryptString(editTextContent.getText().toString(), (byte)key);
			editTextContent.setText(tmp);
		}
		else
		{
			//decrypt message
			String tmp = decryptString(editTextContent.getText().toString(), (byte)key);
			editTextContent.setText(tmp);
		}
	}
	
	
	private String encryptString(String msg, byte key)
	{
		//encode string with UTF-16 firstly, then get byte array
		byte[] bytes = msg.getBytes(Charset.forName("UTF-16LE"));
		
		//for debug
		textViewDebug.append("\nmsg array using UTF-16LE:");
		for(byte b : bytes)
		{
			textViewDebug.append(String.format("%02x ", b));
		}
		textViewDebug.append("\n");
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
				textViewDebug.append("\ntransformed msg array using UTF-16LE:");
				for(byte b : bytes)
				{
					textViewDebug.append(String.format("%02x ", b));
				}
				textViewDebug.append("\n");
				/////////////////////////////
				
		//decrease 1 to get original byte
		for(int i=0; i<bytes.length; i++)
		{
			bytes[i] -= key;
		}
		//transform byte array to string using charset UTF-16
		String original = new String(bytes, Charset.forName("UTF-16LE"));
		
		//for debug
				textViewDebug.append("\nmsg array restored using UTF-16LE:");
				for(byte b : bytes)
				{
					textViewDebug.append(String.format("%02x ", b));
				}
				textViewDebug.append("\n");
				/////////////////////////////
		
		return original;
	}

	private enum ENCRYPTION_STATUS
	{
		ENCRYPTED,
		DECRYPTED
	}

}
