package com.example.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends ActionBarActivity {
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    static final String DISPLAY_MESSAGE_ACTION="com.example.test.DISPLAY_MESSAGE";
    static final String SERVER_URL = "http://exceltest.comuv.com/register.php";
    //static final String SERVER_URL = "http://doylefermi.x20.in/register.php";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static String acc = "";
    public String msg = "";
    public static String accn = "";
    String SENDER_ID = "1019787135827";


    static final String TAG = "GCMDemo";
    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;
    String email="";
    String name="";
    EditText ema;
    EditText nam;

    String imei=null;
    String sim_operator_Name=null;
    String droid_version=null;
    String phone_no=null;
    String phone_dpi=null;
    String phone_manuf=null;
    String phone_model=null;
    String device_info=null;

    TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	setContentView(R.layout.activity_main);
		 setContentView(R.layout.activity_main);

		Account[] accounts = AccountManager.get(this).getAccounts();

		acc= accounts[0].name;
        Log.d("Account", "Name " + nam);
        accn=acc.substring(0, acc.indexOf('@'));

        nam=(EditText)findViewById(R.id.name); nam.setText(accn);
        ema=(EditText)findViewById(R.id.email); ema.setText(acc);

        //Device info
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        imei=telephonyManager.getDeviceId();
        sim_operator_Name=telephonyManager.getSimOperatorName();
        droid_version = android.os.Build.VERSION.RELEASE;
        phone_no=telephonyManager.getLine1Number();
        phone_dpi=metrics.densityDpi+"dp";
        phone_manuf=Build.MANUFACTURER;
        phone_model=android.os.Build.PRODUCT;

        if(sim_operator_Name==null){sim_operator_Name="null";}
        if(phone_no==null){phone_no="null";}
        if(phone_manuf==null){phone_manuf="null";}
        if(phone_model==null){phone_model="null";}




        device_info=accn+"\n"+acc+"\n"+sim_operator_Name+"\n"+droid_version+"\n"+phone_no+"\n"+phone_dpi+"\n"+phone_manuf+" "+phone_model ;

		context = getApplicationContext();
        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(context);
        if (!regid.isEmpty()) {


            setContentView(R.layout.activity_gcmnotification_intent_service);

            textView=(TextView)findViewById(R.id.textView);
            textView.setText(device_info);




            Toast.makeText(getApplicationContext(), "Device registered, registration ID=" + regid,
                    Toast.LENGTH_LONG).show();
        }
        else {
            Button btn = (Button) findViewById(R.id.submitbut);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    name = nam.getText().toString();


                    email = ema.getText().toString();

                    // Start func if NET

                    //setContentView(R.layout.activity_main);
                    mDisplay = (TextView) findViewById(R.id.name);

                    //context = getApplicationContext();
                    //gcm = GoogleCloudMessaging.getInstance(this);
                    //regid = getRegistrationId(context);

                    if (regid.isEmpty()) {
                        registerInBackground();
                    } else {
                        setContentView(R.layout.activity_gcmnotification_intent_service);
                        Toast.makeText(getApplicationContext(), "Device registered, registration ID=" + regid,
                                Toast.LENGTH_LONG).show();
       /* PackageManager p = getPackageManager();
        p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);*/
                    }


                }
            });

            //Intent intent=new Intent(this, GcmBroadcastReceiver.class);
            //startActivity(intent);

            //g.onReceive(this,);
        }
	}
        private String getRegistrationId(Context context) {
            final SharedPreferences prefs = getGCMPreferences(context);
            String registrationId = prefs.getString(PROPERTY_REG_ID, "");
            if (registrationId.isEmpty()) {
                Log.i(TAG, "Registration not found.");
                return "";
            }
            // Check if app was updated; setContentView(R.layout.activity_main);if so, it must clear the registration ID
            // since the existing regID is not guaranteed to work with the new
            // app version.
               return registrationId;
        }
        /**
         * @return Application's {@code SharedPreferences}.
         */
        private SharedPreferences getGCMPreferences(Context context) {
            // This sample app persists the registration ID in shared preferences, but
            // how you store the regID in your app is up to you.
            return getSharedPreferences(MainActivity.class.getSimpleName(),
                    Context.MODE_PRIVATE);
        }
        private void registerInBackground() {
        	    setContentView(R.layout.activity_gcm_broadcast_receiver);
        	    new AsyncTask<Void,Void,String>() {
        	        @Override
        	        protected String doInBackground(Void... params) {

        	            try {
        	                if (gcm == null) {
        	                    gcm = GoogleCloudMessaging.getInstance(context);
        	                }
        	                regid = gcm.register(SENDER_ID);
        	                msg = "Device registered, registration ID=" + regid;

        	                // You should send the registration ID to your server over HTTP,
        	                // so it can use GCM/HTTP or CCS to send messages to your app.
        	                // The request to your server should be authenticated if your app
        	                // is using accounts.
        	                sendRegistrationIdToBackend();

        	                // For this demo: we don't need to send it because the device
        	                // will send upstream messages to a server that echo back the
        	                // message using the 'from' address in the message.

        	                // Persist the regID - no need to register again.
        	                storeRegistrationId(context, regid);
        	            } catch (IOException ex) {
        	                msg = "Error :" + ex.getMessage();
        	                // If there is an error, don't just keep trying to register.
        	                // Require the user to click a button again, or perform
        	                // exponential back-off.
        	            }
        	            return msg;
        	        }

                     private void sendRegistrationIdToBackend() {
                	 final int MAX_ATTEMPTS = 5;
                    final int BACKOFF_MILLI_SECONDS = 2000;
                    final Random random = new Random();
                    Log.i(TAG, "registering device (regId = " + regid + ")");
                    String serverUrl = SERVER_URL;
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("regId", regid);
                    params.put("name",name);
                    params.put("email",email);
                         params.put("sim_name",sim_operator_Name);
                         params.put("android_version",droid_version);
                         params.put("phone_no",phone_no);
                         params.put("dpi",phone_dpi);
                         params.put("manuf",phone_manuf);
                         params.put("model",phone_model);


                    long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
                    // Once GCM returns a registration id, we need to register on our server
                    // As the server might be down, we will retry it a couple
                    // times.
                    for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                        Log.d(TAG, "Attempt #" + i + " to register");
                        try {
                            post(serverUrl, params);
                           // displayMessage(context, "Registered");
                            return;
                        } catch (IOException e) {
                            // Here we are simplifying and retrying on any error; in a real
                            // application, it should retry only on unrecoverable errors
                            // (like HTTP error code 503).
                            Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                            if (i == MAX_ATTEMPTS) {
                                break;
                            }
                            try {
                                Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                                Thread.sleep(backoff);
                            } catch (InterruptedException e1) {
                                // Activity finished before we complete - exit.
                                Log.d(TAG, "Thread interrupted: abort remaining retries!");
                                Thread.currentThread().interrupt();
                                return;
                            }
                            // increase backoff exponentially
                            backoff *= 2;
                        }
                    }
                  //  String message = context.getString(R.string.server_register_error,
                    //        MAX_ATTEMPTS);
                    //CommonUtilities.displayMessage(context, message);

                }
                private  void post(String endpoint, Map<String, String> params)throws IOException{
                    URL url;
                    try {
                        url = new URL(endpoint);
                    } catch (MalformedURLException e) {
                        throw new IllegalArgumentException("invalid url: " + endpoint);
                    }
                    StringBuilder bodyBuilder = new StringBuilder();
                    Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
                    // constructs the POST body using the parameters
                    while (iterator.hasNext()) {
                        Entry<String, String> param = iterator.next();
                        bodyBuilder.append(param.getKey()).append('=')
                                .append(param.getValue());
                        if (iterator.hasNext()) {
                            bodyBuilder.append('&');
                        }
                    }
                    String body = bodyBuilder.toString();
                    Log.v(TAG, "Posting '" + body+ "' to " + url);
                    byte[] bytes = body.getBytes();
                    HttpURLConnection conn = null;
                    try {
                        Log.e("URL", "> " + url);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setFixedLengthStreamingMode(bytes.length);
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Content-Type",
                                "application/x-www-form-urlencoded;charset=UTF-8");
                        // post the request
                        OutputStream out = conn.getOutputStream();
                        out.write(bytes);
                        out.close();
                        // handle the response
                        int status = conn.getResponseCode();
                        if (status != 200) {
                          throw new IOException("Post failed with error code " + status);
                        }
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                }

                protected void onPostExecute(String msg) {
                	//setContentView(R.layout.activity_gcm_broadcast_receiver);
                    mDisplay.append(msg + "\n");
                    mDisplay.setText(msg);
                }


            }.execute(null, null, null);}

        private void storeRegistrationId(Context context, String regId) {
            final SharedPreferences prefs = getGCMPreferences(context);
            //int appVersion = getAppVersion(context);
           // Log.i(TAG, "Saving regId on app version " + appVersion);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PROPERTY_REG_ID, regId);
          //  editor.putInt(PROPERTY_APP_VERSION, appVersion);
            editor.commit();
        }
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
