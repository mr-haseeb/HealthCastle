package com.npdevs.healthcastle;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Locale;

public class FrontActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {
	private TextView maxCalorie,consumedCalorie,burntCalorie,allowedCalorie,steps;
	private Button checkSafe,addFood,addExercise;
	private DatabaseHelper databaseHelper;
	private DatabaseHelper2 databaseHelper2;
	private TextToSpeech textToSpeech;
	private String MOB_NUMBER;
	private String[] categorties=new String[]{"Whole Milk","Paneer (Whole Milk)","Butter","Ghee","Apple","Banana","Grapes","Mango","Musambi","Orange","Cooked Cereal","Rice Cooked","Chapatti","Potato","Dal","Mixed Vegetables","Fish","Mutton","Egg","Biscuit (Sweet)","Cake (Plain)","Cake (Rich Chocolate)","Dosa (Plain)","Dosa (Masala)","Pakoras","Puri","Samosa","Vada (Medu)","Biryani (Mutton)","Biryani (Veg.)","Curry (Chicken)","Curry (Veg.)","Fried Fish","Pulav (Veg.)","Carrot Halwa","Jalebi","Kheer","Rasgulla"};
	private int[] measure=new int[]{230,60,14,15,150,60,75,100,130,130,100,25,60,150,100,150,50,30,40,15,50,50,100,100,50,40,35,40,200,200,100,100,85,100,45,20,100,50};
	private int[] calories=new int[]{150,150,45,45,55,55,55,55,55,55,80,80,80,80,80,80,55,75,75,70,135,225,135,250,175,85,140,70,225,200,225,130,140,130,165,100,180,140};
	private DrawerLayout dl;
	private ActionBarDrawerToggle t;
	private NavigationView nv;
	private int age,weight,height,sex;
	private SensorManager sensorManager;
	private Sensor sensor;
	boolean running = false;
	String[] activites=new String[]{"Weight Lifting: general","Weight Lifting: vigorous","Bicycling, Stationary: moderate","Rowing, Stationary: moderate","Bicycling, Stationary: vigorous","Dancing: slow, waltz, foxtrot","Volleyball: non-competitive, general play","Walking: 3.5 mph","Dancing: disco, ballroom, square","Soccer: general","Tennis: general","Swimming: backstroke","Running: 5.2 mph","Bicycling: 14-15.9 mph","Digging","Chopping & splitting wood","Sleeping","Cooking","Auto Repair","Paint house: outside","Computer Work","Welding","Coaching Sports","Sitting in Class"};
	int[] calories1=new int[]{112,223,260,260,391,112,112,149,205,260,260,298,335,372,186,223,23,93,112,186,51,112,149,65};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(t.onOptionsItemSelected(item))
			return true;

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_front);
		MOB_NUMBER=getIntent().getStringExtra("MOB_NUMBER");

		sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
		loadUserData();
		schedulealarm();

		TextView heart=findViewById(R.id.textView12);
		heart.setText(readHeartbeat());

		dl = findViewById(R.id.activity_front);
		t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

		dl.addDrawerListener(t);
		t.syncState();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		nv = findViewById(R.id.nv);
		View headerView = nv.getHeaderView(0);
		Button editProfile=headerView.findViewById(R.id.button8);
		editProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FrontActivity.this,EditProfile.class);
				intent.putExtra("MOB_NUMBER",MOB_NUMBER);
				startActivity(intent);
				finish();
			}
		});
		nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				int id = item.getItemId();
				switch(id)
				{
					case R.id.heartbeat:
						//Toast.makeText(FrontActivity.this, "Click finish when satisfied!",Toast.LENGTH_SHORT).show();
						Intent intent=new Intent(FrontActivity.this,HeartMeter.class);
						startActivity(intent);
						finish();
						return true;
					case R.id.addfood:
						//Toast.makeText(FrontActivity.this, "Settings... who got time for that?",Toast.LENGTH_SHORT).show();
						Intent intent1 = new Intent(FrontActivity.this,AddNewFood.class);
						startActivity(intent1);
						return true;
					case R.id.addexercise:
						//Toast.makeText(FrontActivity.this, "I won't give help!",Toast.LENGTH_SHORT).show();
						Intent intent2 = new Intent(FrontActivity.this,AddNewExercise.class);
						startActivity(intent2);
						return true;
					case R.id.heartbeatstats:
						intent = new Intent(FrontActivity.this,HeartGraph.class);
						intent.putExtra("MOB_NUMBER",MOB_NUMBER);
						startActivity(intent);
						return true;
					case R.id.caloriestats:
						intent = new Intent(FrontActivity.this,CalorieGraph.class);
						intent.putExtra("MOB_NUMBER",MOB_NUMBER);
						startActivity(intent);
						return true;
					case R.id.stepsstats:
						intent = new Intent(FrontActivity.this,StepsGraph.class);
						intent.putExtra("MOB_NUMBER",MOB_NUMBER);
						startActivity(intent);
						return true;
					case R.id.mobSearch:
						intent = new Intent(FrontActivity.this,PhoneSearch.class);
						intent.putExtra("MOB_NUMBER",MOB_NUMBER);
						startActivity(intent);
						return true;
					case R.id.connection:
						intent = new Intent(FrontActivity.this,Connections.class);
						intent.putExtra("MOB_NUMBER",MOB_NUMBER);
						startActivity(intent);
						return true;
					case R.id.logout:
						Toast.makeText(FrontActivity.this,"Logged out",Toast.LENGTH_SHORT).show();
						clearTable();
						saveTable();
						intent=new Intent(FrontActivity.this,MainActivity.class);
						startActivity(intent);
						finish();
						return true;
					case R.id.contact:
						intent = new Intent(FrontActivity.this,About.class);
						startActivity(intent);
						return true;
					case R.id.feedback:
						Toast.makeText(FrontActivity.this,"Sorry, currently not available",Toast.LENGTH_LONG).show();
						return true;
					default:
						return true;
				}
			}

		});

		textToSpeech = new TextToSpeech(this, this);

		Button button = findViewById(R.id.button10);

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
				startActivityForResult(intent, 10);
			}
		});


		maxCalorie = findViewById(R.id.textView2);
		consumedCalorie = findViewById(R.id.textView4);
		burntCalorie = findViewById(R.id.textView6);
		allowedCalorie = findViewById(R.id.textView8);
		steps = findViewById(R.id.textView10);
		checkSafe = findViewById(R.id.button);
		addFood = findViewById(R.id.button2);
		addExercise = findViewById(R.id.button4);
		databaseHelper = new DatabaseHelper(this);
		Cursor res = databaseHelper.getAllData();
		consumedCalorie.setText(loadPreferences("consumed"));
		burntCalorie.setText(loadPreferences("burnt"));
		if(sex==1){
			double bmr = 88.362+(13.397*weight)+(4.799*height)-(5.677*age);
			bmr = bmr*1.2;
			int bmr1 = (int)bmr;
			maxCalorie.setText(bmr1+"");
			int x = Integer.parseInt(burntCalorie.getText().toString());
			int y = Integer.parseInt(consumedCalorie.getText().toString());
			allowedCalorie.setText(bmr1+x-y+"");
			saveTable2(bmr1+x-y+"");
		}
		else{
			double bmr = 447.593+(9.247*weight)+(3.098*height)-(4.330*age);
			bmr = bmr*1.2;
			int bmr1 = (int)bmr;
			maxCalorie.setText(bmr1+"");
			int x = Integer.parseInt(burntCalorie.getText().toString());
			int y = Integer.parseInt(consumedCalorie.getText().toString());
			allowedCalorie.setText(bmr1+x-y+"");
			saveTable2(bmr1+x-y+"");
		}
		if(res.getCount()==0)
		{
			//databaseHelper.insertData("Ashu","20","10");
			int size = categorties.length;
			for(int i=0;i<size;i++){
				databaseHelper.insertData(categorties[i],measure[i],calories[i]);
			}
			//Toast.makeText(this,"Hi",Toast.LENGTH_SHORT).show();
		}
       /* databaseHelper2 = new DatabaseHelper2(this);
        Cursor res2 = databaseHelper2.getAllData();
        if(res2.getCount()==0){
            databaseHelper.insertData("Ashu","20","10");
            Toast.makeText(this,"Hi",Toast.LENGTH_SHORT).show();
        }*/
		checkSafe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openCheckSafeActivity();
			}
		});
		databaseHelper2 = new DatabaseHelper2(this);
		Cursor res2 = databaseHelper2.getAllData();
		//Log.e("ch",res2.getCount()+"");
		if(res2.getCount()==0){
			int size = activites.length;
			for(int i=0;i<size;i++){
				databaseHelper2.insertData(activites[i],30,calories1[i]);
			}
			Cursor ashu = databaseHelper2.getAllData();
			/*while (ashu.moveToNext()){
				Log.e("ashu",ashu.getString(0)+" "+ashu.getString(1)+" "+ashu.getString(3));
			}*/
		}
		checkSafe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openCheckSafeActivity();
			}
		});
		addFood.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openAddFoodActivity();
			}
		});
		addExercise.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openAddExerciseActivity();
			}
		});
	}

	private void openAddExerciseActivity() {
		Intent intent = new Intent(FrontActivity.this,AddExerciseSearch.class);
		startActivity(intent);
	}

	private void openAddFoodActivity() {
		Intent intent = new Intent(this,AddFoodSearch.class);
		startActivity(intent);
	}

	private void clearTable()
	{
		SharedPreferences preferences = getSharedPreferences("usersave", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}

	private void saveTable()
	{
		SharedPreferences sharedPreferences=getSharedPreferences("usersave",MODE_PRIVATE);
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putString("User","no");
		editor.apply();
	}

	private void schedulealarm() {

		// Construct an intent that will execute the AlarmReceiver
		Intent intent = new Intent(this, AlarmReciever.class);
		// Create a PendingIntent to be triggered when the alarm goes off
		final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReciever.REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// Setup periodic alarm every every half hour from this point onwards
		AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		// First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
		// Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
				1000*40, pIntent);
	}

	private String readHeartbeat()
	{
		SharedPreferences sharedPreferences=getSharedPreferences("heartbeats",MODE_PRIVATE);
		String beats=sharedPreferences.getString("beats","no");
		if(beats.equals("") || beats.isEmpty() || beats.equals("no"))
			beats="NaN";
		return beats;
	}

	private void openCheckSafeActivity() {
		Intent intent = new Intent(this,CheckSafeSearch.class);
		startActivity(intent);
	}

	private String loadPreferences(String whom)
	{
		SharedPreferences sharedPreferences=getSharedPreferences("food",MODE_PRIVATE);
		return sharedPreferences.getString(whom,"0");
	}

	private void loadUserData() {
		SharedPreferences sharedPreferences=getSharedPreferences("usersave",MODE_PRIVATE);
		String temp=sharedPreferences.getString("Age","0");
		if(temp.equals("") || temp.isEmpty() || temp.equals("0"))
			temp="0";
		age=Integer.parseInt(temp);
		temp=sharedPreferences.getString("Height","0");
		if(temp.equals("") || temp.isEmpty() || temp.equals("0"))
			temp="0";
		height=Integer.parseInt(temp);
		temp=sharedPreferences.getString("Weight","0");
		if(temp.equals("") || temp.isEmpty() || temp.equals("0"))
			temp="0";
		weight=Integer.parseInt(temp);
		temp=sharedPreferences.getString("Sex","0");
		if(temp.equals("") || temp.isEmpty() || temp.equals("0"))
			temp="0";
		sex=Integer.parseInt(temp);
	}

	@Override
	public void onResume(){
		super.onResume();
		running = true;
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		//   if(sensorManager!=null)
		//      Log.e("ashu","ashu");
		if(sensor!=null){
			sensorManager.registerListener(this,sensor,sensorManager.SENSOR_DELAY_FASTEST);
		}
		else{
			Toast.makeText(this,"Sensor not found!!!",Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onPause(){
		super.onPause();
		running = false;
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		if(running){
			steps.setText(String.valueOf(sensorEvent.values[0]));
			saveTable1(String.valueOf(sensorEvent.values[0]));
			String burnt1 = loadPreferences("burnt");

			int x = Integer.parseInt(burnt1);
			int z = (int)(0.05*Double.parseDouble(steps.getText().toString()));
			saveTable(x+z+"");
		}
	}
	private void saveTable(String ans)
	{
		SharedPreferences sharedPreferences=getSharedPreferences("food",MODE_PRIVATE);
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putString("burnt",ans);
		editor.apply();
	}
	private void saveTable1(String ans)
	{
		SharedPreferences sharedPreferences=getSharedPreferences("food",MODE_PRIVATE);
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putString("steps",ans);
		editor.apply();
	}
	private void saveTable2(String ans)
	{
		SharedPreferences sharedPreferences=getSharedPreferences("food",MODE_PRIVATE);
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putString("allowed",ans);
		editor.apply();
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {

	}

	@Override
	public void onInit(int i) {

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode==RESULT_OK && data!=null) {
			getWorkDoneFromResult(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
		} else {
			Toast.makeText(getApplicationContext(), "Failed to recognize speech!", Toast.LENGTH_LONG).show();
		}
	}

	private void getWorkDoneFromResult(ArrayList<String> stringArrayListExtra) {
		for (String str : stringArrayListExtra) {
			if(str.toLowerCase().contains("add food")) {
				Intent intent=new Intent(FrontActivity.this,AddFoodSearch.class);
				try {
					intent.putExtra("SEARCH", str.substring(str.lastIndexOf("add food") + 9).trim());
				} catch (Exception e) {
					intent.putExtra("SEARCH","");
				}
				startActivity(intent);
				break;
			}
			if(str.toLowerCase().contains("add exercise")) {
				Intent intent=new Intent(FrontActivity.this,AddExerciseSearch.class);
				try {
					intent.putExtra("SEARCH", str.substring(str.lastIndexOf("add exercise") + 13).trim());
				} catch (Exception e) {
					intent.putExtra("SEARCH","");
				}
				startActivity(intent);
				break;
			}
			if(str.toLowerCase().contains("search person")) {
				Intent intent=new Intent(FrontActivity.this,PhoneSearch.class);
				try {
					intent.putExtra("SEARCH", str.substring(str.lastIndexOf("search person") + 14).trim().replaceAll(" ",""));
				} catch (Exception e) {
					intent.putExtra("SEARCH","");
				}
				intent.putExtra("MOB_NUMBER",MOB_NUMBER);
				startActivity(intent);
				break;
			}
			if(str.toLowerCase().contains("steps graph")) {
				Intent intent=new Intent(FrontActivity.this,StepsGraph.class);
				intent.putExtra("MOB_NUMBER", MOB_NUMBER);
				startActivity(intent);
				break;
			}
			if(str.toLowerCase().contains("heart graph") || str.toLowerCase().contains("heartbeat graph") || str.toLowerCase().contains("heart rate graph")) {
				Intent intent=new Intent(FrontActivity.this,HeartGraph.class);
				intent.putExtra("MOB_NUMBER", MOB_NUMBER);
				startActivity(intent);
				break;
			}
			if(str.toLowerCase().contains("measure heart")) {
				Intent intent=new Intent(FrontActivity.this,HeartMeter.class);
				startActivity(intent);
				break;
			}
			if(str.toLowerCase().contains("food graph") || str.toLowerCase().contains("calorie graph")) {
				Intent intent=new Intent(FrontActivity.this,CalorieGraph.class);
				intent.putExtra("MOB_NUMBER", MOB_NUMBER);
				startActivity(intent);
				break;
			}
		}
	}

	private void speak(String string) {
		textToSpeech.speak(String.valueOf(string), TextToSpeech.QUEUE_ADD, null);
	}
}