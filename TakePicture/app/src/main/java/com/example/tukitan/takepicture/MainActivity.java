package com.example.tukitan.takepicture;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button startButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        TextView myText = (TextView)findViewById(R.id.text);
        startButton = (Button)findViewById(R.id.start);
        startButton.setOnClickListener(startMethod);
        myText.setText("Helloooooooo");

    }
    private final View.OnClickListener startMethod = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            Intent intentMain_Camera = new Intent(MainActivity.this,CameraActivity.class);
            Log.v("Intent","startActivity");
            startActivity(intentMain_Camera);
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}
