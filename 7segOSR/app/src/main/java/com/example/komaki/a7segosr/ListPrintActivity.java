package com.example.komaki.a7segosr;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListPrintActivity extends AppCompatActivity {

    String sdPath = Environment.getExternalStorageDirectory().getPath();

    File[] files;
    ListView lv;
    List<String> filelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_print);
        filelist = new ArrayList<String>();

        files = new File(sdPath + "/7segOCRresult/RESULT/").listFiles();
        if(files != null){
            for(int i=0;i<files.length;i++){
                filelist.add(files[i].getName());
                System.out.println(files[i].getName());
            }
        }else {
            Log.i("ListPrintActivity","files is null");
        }

        lv = (ListView) findViewById(R.id.listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,filelist);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listview = (ListView) parent;
                String item = (String) listview.getItemAtPosition(position);
                showItem(item);
            }

        });

    }

    public void showItem(String str){
        Toast.makeText(this,"file:" +str,Toast.LENGTH_SHORT).show();
    }
}
