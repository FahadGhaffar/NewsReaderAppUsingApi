package com.example.newsreaderappusingapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
   ListView newsReaderIdList;
   ArrayList<String> arrayList=new ArrayList<>();
   ArrayList<String> content=new ArrayList<>();
   ArrayAdapter<String> arrayAdapter;

    SQLiteDatabase articalsDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsReaderIdList=findViewById(R.id.newsReaderId);

        arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
        newsReaderIdList.setAdapter(arrayAdapter);

        newsReaderIdList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(MainActivity.this,ArticalActivity.class);
                intent.putExtra("content",content.get(i));
                startActivity(intent);
            }
        });
        readContent readNewsClass=new readContent();

        try {
            readNewsClass.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

        } catch (Exception e) {
            e.printStackTrace();
        }

        articalsDatabase =this.openOrCreateDatabase("Articals",MODE_PRIVATE,null);
  articalsDatabase.execSQL("Create TABLE IF NOT EXISTS articals (id INTEGER PRIMARY KEY,articalsId INTTEGER ,title VARCHAR ,content VARCHAR )");

      updateListview();

    }


    public void updateListview(){
        Cursor c=articalsDatabase.rawQuery("SELECT *FROM articals",null);

        int contentIndex =c.getColumnIndex("content");
        int titleIndex=c.getColumnIndex("title");
if (c.moveToFirst()){


    arrayList.clear();
content.clear();
}

do{

    arrayList.add(c.getString(titleIndex));
content.add(c.getString(contentIndex));

}while (c.moveToNext());
arrayAdapter.notifyDataSetChanged();

    }



    public class readContent extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... strings) {
         String result="";
            URL url;
            HttpURLConnection connection;
try {
    url=new URL(strings[0]);
    connection=(HttpURLConnection) url.openConnection();
    InputStream in= connection.getInputStream();
    InputStreamReader reader=new InputStreamReader(in);
    int data=reader.read();
    while (data!=-1){
    char current =(char)data;
    result+=current;
    data=reader.read();


    }
    Log.i("urlResult",result);

    JSONArray jsonArray=new JSONArray(result);
    int numberItem=20;
    if (jsonArray.length()<20){

        numberItem=jsonArray.length();
    }
    articalsDatabase.execSQL("DELETE FROM articals");
    for (int i=0;i<numberItem;i++){

     //   Log.i("jsonResult",jsonArray.getString(i));
        String getNewsid=jsonArray.getString(i);
     url=new URL("https://hacker-news.firebaseio.com/v0/item/"+getNewsid+".json?print=pretty");
     connection=(HttpURLConnection) url.openConnection();
     in=connection.getInputStream();
     reader=new InputStreamReader(in);
     data=reader.read();

    String articalInfo="";
    while (data!=-1){

        char current =(char) data;

        articalInfo+=current;

        data=reader.read();

    }
  Log.i("artifcalInfo",articalInfo);
JSONObject jsonObject=new JSONObject(articalInfo);

        if (!jsonObject.isNull("title") && !jsonObject.isNull("url")){
        String articalTitle=jsonObject.getString("title");
        String articalurl=jsonObject.getString("url");


            url=new URL(articalurl);
            connection=(HttpURLConnection) url.openConnection();
            in=connection.getInputStream();
            reader=new InputStreamReader(in);
            data=reader.read();

            String articalContent="";
            while (data!=-1){

                char current =(char) data;

                articalContent+=current;

                data=reader.read();

            }
   String sql="INSERT INTO articals (articalsId,title,content) VALUES (?, ? ,?)";

            SQLiteStatement sqLiteStatement=articalsDatabase.compileStatement(sql);
            sqLiteStatement.bindString(1,getNewsid);
            sqLiteStatement.bindString(2,articalTitle);
            sqLiteStatement.bindString(3,articalContent);

            sqLiteStatement.execute();

Log.i("articalContent",articalContent);}
    }
} catch (MalformedURLException e) {
    e.printStackTrace();
} catch (IOException e) {
    e.printStackTrace();
} catch (JSONException e) {
    e.printStackTrace();
}

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            updateListview();
        }
    }
}
