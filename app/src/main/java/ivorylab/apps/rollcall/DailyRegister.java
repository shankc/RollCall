package ivorylab.apps.rollcall;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
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

public class DailyRegister extends AppCompatActivity {
TableLayout table;

    TableRow tr_head;
    TextView label_name,label_intime,label_outtime;
    ArrayList<String> Names,In_Time,Out_Time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_report_table);
        table = (TableLayout) findViewById(R.id.main_table);
           Names= new ArrayList<>();
        In_Time= new ArrayList<>();
        Out_Time= new ArrayList<>();

        new GetDailyInOut(){
            @Override
        protected void onPostExecute(ArrayList<String>array){
                super.onPostExecute(array);
                tr_head = new TableRow(DailyRegister.this);

                tr_head.setId(R.id.worker_table);
                tr_head.setBackgroundColor(Color.parseColor("#4D7AF9"));
                tr_head.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));

                label_name = new TextView(DailyRegister.this);
                label_name.setId(R.id.worker_name);
                label_name.setText("Name");
                label_name.setTextColor(Color.BLACK);
                label_name.setPadding(20, 20, 20, 20);
                tr_head.addView(label_name);// add the column to the table row here

                label_intime = new TextView(DailyRegister.this);
                label_intime.setId(R.id.worket_out);// define id that must be unique
                label_intime.setText("In-Time"); // set the text for the header
                label_intime.setTextColor(Color.BLACK); // set the color
                label_intime.setPadding(5, 5, 5, 5); // set the padding (if required)
                tr_head.addView(label_intime);

                label_outtime = new TextView(DailyRegister.this);
                label_outtime.setId(R.id.worket_out);// define id that must be unique
                label_outtime.setText("Out-Time"); // set the text for the header
                label_outtime.setTextColor(Color.BLACK); // set the color
                label_outtime.setPadding(5, 5, 5, 5); // set the padding (if required)
                tr_head.addView(label_outtime);

                table.addView(tr_head, new LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));


                int count =0;
                for(int i=0;i<array.size();i++)
                {

                    String Name=Names.get(i);
                    String Intime = In_Time.get(i);
                    String OutTime=Out_Time.get(i);
// Create the table row
                    TableRow tr = new TableRow(DailyRegister.this);
                    tr.setId(100 + count);
                    tr.setLayoutParams(new LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));

                    TextView names = new TextView(DailyRegister.this);
                    names.setId(200 + count);
                    names.setText(Name);
                    names.setPadding(15, 15, 15, 15);
                    names.setTextColor(Color.BLACK);
                    tr.addView(names);

                    TextView in_time= new TextView(DailyRegister.this);
                    in_time.setId(200 + count);
                    in_time.setText((Intime));
                    in_time.setTextColor(Color.BLACK);
                    tr.addView(in_time);

                    TextView out_time= new TextView(DailyRegister.this);
                    out_time.setId(200+count);
                    out_time.setText((OutTime));
                    out_time.setTextColor(Color.BLACK);
                    out_time.setPadding(25,0,0,0);
                    tr.addView(out_time);
                    table.addView(tr, new TableLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));
                    count++;
                }


            }
        }.execute();

        }
    class GetDailyInOut extends AsyncTask<Void,Void,ArrayList<String>>{

        @Override
        protected ArrayList<String>doInBackground(Void... params) {
            config c= new config();
            OkHttpClient client = new OkHttpClient();

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat datetime= new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String dte,tim;
            dte=dateFormat.format(date); // time zone UTC add code for it

            Log.d("PresentListActivity", "the date " + dte);
            SharedPreferences data = getSharedPreferences("DataStorage",0);

            String URL=c.getURL()+"/api/employee/GetDayinOuts?SuperId="+data.getString("SuperId","")+"&Startdate="+dte+"&Enddate="+dte;
            Request request = new Request.Builder().url(URL).build();
            Response response =null;
            try{
                response = client.newCall(request).execute();
                String content = response.body().string();
                Log.d("DailyActivity"," the content is "+content);

                JSONArray array= new JSONArray(content);
                Log.d("DailyActivity","the JSON array is "+array);
                JSONObject object=null;
                for(int i=0;i<array.length();i++)
                {
                    object=array.getJSONObject(i);
                    Names.add(object.getString("UserName"));
                    In_Time.add(object.getString("InTime").substring(0,5));
                    Out_Time.add(object.getString("OutTime").substring(0,5));

                }
                return Names;

            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
                //return null;
            }
            return null;
        }
    }
    }


