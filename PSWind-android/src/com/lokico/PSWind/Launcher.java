package com.lokico.PSWind;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class Launcher extends Activity {

	public static boolean FULLSCREEN;
	private static String FULLSCREEN_PREF = "FULLSCREEN_PREF";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher);

		// Check our screen preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		FULLSCREEN = prefs.getBoolean(FULLSCREEN_PREF, true);
		setFullScreen(this, FULLSCREEN);

		Button next = (Button) findViewById(R.id.ButtonOmni);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), Omnimap.class);
				startActivity(myIntent);
			}
		});

		next = (Button) findViewById(R.id.ButtonNWKitemap);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), NWKiteMap.class);
				startActivity(myIntent);
			}
		});

		next = (Button) findViewById(R.id.ButtonTJ_Jetty);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(),
						TJ_Seattle.class);
				startActivity(myIntent);
			}
		});

		next = (Button) findViewById(R.id.ButtonTJ_Locust);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(),
						TJ_NorthSound.class);
				startActivity(myIntent);
			}
		});

		next = (Button) findViewById(R.id.Button_NOAA);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), NOAA.class);
				startActivity(myIntent);
			}
		});

		Button btn;
		btn = (Button) findViewById(R.id.ButtonOmni);
		btn.getBackground().setAlpha(100);
		btn = (Button) findViewById(R.id.ButtonNWKitemap);
		btn.getBackground().setAlpha(100);
		btn = (Button) findViewById(R.id.ButtonTJ_Jetty);
		btn.getBackground().setAlpha(100);
		btn = (Button) findViewById(R.id.ButtonTJ_Locust);
		btn.getBackground().setAlpha(100);
		btn = (Button) findViewById(R.id.Button_NOAA);
		btn.getBackground().setAlpha(100);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.launchermenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.toggle_full_screen:
			FULLSCREEN = !FULLSCREEN;
			
			// Save the toggled pref
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(FULLSCREEN_PREF, FULLSCREEN);
			editor.commit();
			
			setFullScreen(this, FULLSCREEN);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static void setFullScreen(Activity a, boolean fullscreen) {
		int fs = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		int nfs = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;

		if (fullscreen) {
			a.getWindow().clearFlags(nfs);
			a.getWindow().setFlags(fs, fs);
		} else {
			a.getWindow().clearFlags(fs);
			a.getWindow().setFlags(nfs, nfs);
		}
	}
}
