package com.udacity.popular_movies;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class SettingsActivity extends AppCompatActivity {
    private static String TAG = SettingsActivity.class.getCanonicalName();
    private static final String MY_PREFS_NAME = "MyPrefsFile";
    private SharedPreferences sharedPrefs;
    private Boolean prefPopular = true; // true: sort movies by most prefPopular , false: sort movies by rating

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Get saved preferences settings
        sharedPrefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        getAppPreferences(); //retrieve saved preferences
        // Set radio buttons according to saved preferences
        if (prefPopular) {
            RadioButton rB = findViewById(R.id.radioPopular);
            rB.setChecked(true);
        } else {
            RadioButton rB = findViewById(R.id.radioRated);
            rB.setChecked(true);
        }

        // Listener for any changes to the radio group buttons
        final RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int buttonId) {
                // buttonId is the RadioButton selected
                Log.i("TAG", "You selected radio button ID: " + buttonId );
                View rB = radioGroup.findViewById(buttonId);
                int index = radioGroup.indexOfChild(rB);
                Log.i("TAG", "Button Index: " + index );
                if ( index == 1)  { // button index: 0 = prefPopular, 1 = highest rated
                    prefPopular = false;
                }
                else {
                    prefPopular = true;
                }
                setAppPreferences(); //save the preference change
            }
        });

    }//End onCreate

    //Method for getting app preferences
    private void getAppPreferences(){
        String validText = sharedPrefs.getString("text", null);
        if (validText != null) {
            prefPopular = sharedPrefs.getBoolean("prefPopular", true);
            Log.i("TAG", "Preference prefPopular = " + prefPopular);
        }
    }

    // Method for saving app preferences
    private void setAppPreferences(){
        // Editor for writing preferences
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("prefPopular", prefPopular); //save sort-by setting
        editor.putString("text", "OK"); //so it's not null
        editor.apply();//save preferences
    }

}
