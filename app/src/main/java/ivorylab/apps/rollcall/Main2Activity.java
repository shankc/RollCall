package ivorylab.apps.rollcall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ivorylab.apps.rollcall.Tools.config;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// the DashBoard activity.
public class Main2Activity extends AppCompatActivity {

    private float[] yData = { 30, 25, 45 };
    private String[] xData = { "late", "absent", "present" };

    PieChart chart;

    LinearLayout present, absent1,absent2, late, main;

    ProgressWheel progress;
    int mLate_Number,mAbsent_Number,mPresent_Number;
FloatingActionButton fbutton;
    View view;
    TextView Present_Number,Late_Number,Absent_Number;
     String Email;
    ArrayList<Entry> yVals1 = new ArrayList<Entry>();
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        view = findViewById(R.id.layout);

        chart = (PieChart) findViewById(R.id.pie_chart);
        progress = (ProgressWheel) findViewById(R.id.progress_wheel);
        main = (LinearLayout) findViewById(R.id.main_content);
      Present_Number= (TextView) findViewById(R.id.present_number);
        Late_Number= (TextView) findViewById(R.id.late_number);
        Absent_Number= (TextView) findViewById(R.id.absent_number);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                Email = null;
            } else {
                Email= extras.getString("Email");
            }
        } else {
            Email = (String) savedInstanceState.getSerializable("Email");
        }

        present = (LinearLayout) findViewById(R.id.present);
        late = (LinearLayout) findViewById(R.id.late);
        absent1 = (LinearLayout) findViewById(R.id.absent_btn);
        absent2= (LinearLayout) findViewById(R.id.absent_button);

        present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PresentListActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        late.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Log.d("Main2Activity", " the late tab has been clicked");
                Intent intent = new Intent(Main2Activity.this,LateActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        absent1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Main2Activity","absent has been clicked");
                Intent intent = new Intent(Main2Activity.this, AbsentActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

            }
        });
        absent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, AbsentActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        getData();
        fbutton= (FloatingActionButton) findViewById(R.id.fab);
        fbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
Intent intent = new Intent(Main2Activity.this,More.class);
                startActivity(intent);
            }
        });


    }


    private void getData(){
        main.setVisibility(View.INVISIBLE);

        progress.setVisibility(View.VISIBLE);
        progress.spin();

        new getDashboardDetails(){

            @Override
            protected void onPostExecute(JSONObject jsonArray) {
                super.onPostExecute(jsonArray);
                Log.d("Main2Activity"," the json data "+jsonArray);
                setDashboardData(jsonArray);
            }
        }.execute();






    }

    private void setDashboardData(JSONObject jsonArray) {
        progress.setVisibility(View.INVISIBLE);
        progress.stopSpinning();
        if(jsonArray == null){

            Snackbar snackbar = Snackbar
                    .make(view, "Please check the Network Connection", Snackbar.LENGTH_LONG)
                    .setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(intent);
                        }
                    });

            snackbar.show();
        }
        else
        {
            setData();
        }
    }

    private void setData() {

        progress.stopSpinning();
        progress.setVisibility(View.INVISIBLE);

        main.setVisibility(View.VISIBLE);

    }


    class getDashboardDetails extends AsyncTask<Void,Void,JSONObject>{  // type originally was JSONArray now Object passed.

        @Override
        protected JSONObject doInBackground(Void... params) {

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat datetime= new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String dte,tim;
            dte=dateFormat.format(date);
            tim=datetime.format(date);

            config c = new config();

            OkHttpClient client = new OkHttpClient();
      DataStorage data = new DataStorage();
            SharedPreferences settings = getSharedPreferences("DataStorage", 0);

            // website reference http://app.rollcallattendance.com/api/dashboardstatus/get?startDate=5/16/2016&StartTime=09:30:00&SuperId=82481
            String url = c.getURL()+"/api/dailystatus/getdailystatus?SuperId="+settings.getString("SuperId","")+"&DateOfTransaction="+dte+"&StartTime="+tim;
            String txt=settings.getString("SuperId","");
            Log.d("Main2Activity", " the UserDetails is " + txt);


            Request request = new Request.Builder().url(url).build();

            Response response = null;

            try {
                response = client.newCall(request).execute();
                String content  = response.body().string();
                Log.d("Content ", content);
              //  return new JSONArray(content);
                JSONObject object = new JSONObject(content);
                Log.d("Main2Activity","the JSON object is => "+object);
               final  int late=object.getInt("LateCount");
               final  int absent=object.getInt("AbsentCount");
                final int present2=object.getInt("OnTimeCount");
                yVals1.add(new Entry(late,0));
                yVals1.add(new Entry(absent,1));
                yVals1.add(new Entry(present2,2));

                mLate_Number=late;
                mAbsent_Number=absent;
                mPresent_Number=present2;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Late_Number.setText(Integer.toString(late));
                        Absent_Number.setText(Integer.toString(absent));
                        Present_Number.setText(Integer.toString(present2));
                    }
                });
                Log.d("Main2Activity", "the Late,Absent,Prent timing is " + late + " " + absent + " " + present2);
                SettingPieChart();

                return object;
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
                //return null;
            }

            return null;

        }
    }

    public void SettingPieChart(){
        chart.setUsePercentValues(true);
       chart.setDescription("Office Attendance");
        chart.setDescriptionPosition(942,123);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColorTransparent(true);
        chart.setHoleRadius(7);
        chart.setTransparentCircleRadius(10);

        // enable rotation of the chart by touch
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);

       // ArrayList<Entry> yVals1 = new ArrayList<Entry>(); // making this global..

       // for (int i = 0; i < yData.length; i++)
         //   yVals1.add(new Entry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        PieDataSet dataSet = new PieDataSet(yVals1,"");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);


        int[] Vcolors= {Color.YELLOW,Color.RED, Color.GREEN};

       dataSet.setColors(Vcolors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        chart.setData(data); // differs from the already set data menthod.

        // undo all highlights
        chart.highlightValues(null);
    }



}
