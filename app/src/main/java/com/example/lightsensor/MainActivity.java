package com.example.lightsensor;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    TextView texx;

    private static final String TAG = "Sensor";


    private SensorManager sensorManager;
    private Sensor mLight;
    TextView light;
    public String LT;
    private Button ShowData;


    RecyclerView recyclerView;

    //Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    FirebaseRecyclerOptions<Post> options;
    FirebaseRecyclerAdapter<Post,MyRecyclerViewHolder> adapter;
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Toast.makeText(this, firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Email "+System.currentTimeMillis());
        time = System.currentTimeMillis();


        texx=(TextView) findViewById(R.id.tex1);
        Button Temp=(Button) findViewById(R.id.but1);
        Button ShowData = (Button) findViewById(R.id.button2);

        ShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, DataRetrieved.class);
                startActivity(intent);
                finish();


            }
        });

        Temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new doit().execute();
            }
        });


        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("EDMT_FIREBASE");



        light = (TextView) findViewById(R.id.light);


        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mLight != null) {
            sensorManager.registerListener((SensorEventListener) MainActivity.this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Light Meter Listener");
        }else {
            light.setText("Light meter Not Supported");
        }

        displayComment();

    }

    private void displayComment() {
        options =
                new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(databaseReference,Post.class)
                .build();
        adapter=
                new FirebaseRecyclerAdapter<Post, MyRecyclerViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MyRecyclerViewHolder holder, int position, @NonNull Post model) {

                   }

                    @NonNull
                    @Override
                    public MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View itemView = LayoutInflater.from(getBaseContext()).inflate(R.layout.post_item,viewGroup,false);
                        return new MyRecyclerViewHolder(itemView);
                    }
                };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_LIGHT) {

            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();

            Long tssLong = System.currentTimeMillis();
            String tss = tssLong.toString();

            light.setText(" "+sensorEvent.values[0]);
            String Light = Float.toString(sensorEvent.values[0]);
            LT=Light;
            //Log.d(TAG, "Time Stamp: " + ts + " Light: " + sensorEvent.values[0]);
            Post post = new Post(ts,Light);

            //databaseReference.push()
            //        .setValue(post);
            adapter.notifyDataSetChanged();


        }


    }

    @Override
    public void onClick(View view) {
        if(view == ShowData){
            //Toast.makeText(this,"Abbas", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Abbas "+System.currentTimeMillis());
            Intent intent = new Intent(MainActivity.this, DataRetrieved.class);
            startActivity(intent);
            finish();
        }

    }

    public class doit extends AsyncTask<Void,Void,Void> {
        String words;



        @Override
        protected Void doInBackground(Void... voids) {


            try {

                Document doc = Jsoup.connect("https://www.google.com/search?q=dublin+temperature").get();

                Element element =  doc.select("#wob_tm").first();
                System.out.println(element.text()+"  °C");


                words=element.text();
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();

                Long tssLong = System.currentTimeMillis();
                String tss = tssLong.toString();

                Log.d(TAG, "Abbas: " + ts + " Light: " + LT);
                String temperature = (words+" °C");


                Post post = new Post(ts,words);
                databaseReference.child(firebaseUser.getUid()).child(ts).child("Temperature").setValue(temperature);
                databaseReference.child(firebaseUser.getUid()).child(ts).child("Light").setValue(LT);

                //databaseReference.push()
                 //       .setValue(post);
                adapter.notifyDataSetChanged();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            texx.setText(words);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
