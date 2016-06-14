package apps.perennialcode.rollcall;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import apps.perennialcode.rollcall.Tools.config;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ManualAtt extends AppCompatActivity implements OnItemSelectedListener{

    TextView Employee_Name;
    EditText Comments;
    TextView The_Date,IN_TIME,OUT_TIME,EmployeeName;
    Button Update,Cancel;
    String text_date="",text_intime="",text_outtime="",text_commments="";
    Response finalResponse;
    String Name="",InStatus="",OutStatus="",InTime="",OutTime="",Selected="";
    int RegId=0;
    Spinner spinner,spinner1;
    ArrayAdapter<CharSequence> adapter,adapter1;
    String Current_Date="";
    Boolean isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_att);
        The_Date= (TextView) findViewById(R.id.the_date);
        IN_TIME= (TextView) findViewById(R.id.in_time);
        OUT_TIME= (TextView) findViewById(R.id.out_time);
        Update= (Button) findViewById(R.id.update_button);
        Cancel= (Button) findViewById(R.id.cancel_button);
        EmployeeName=(TextView)findViewById(R.id.emp_name);
        Comments= (EditText) findViewById(R.id.manual_comment);
        //setting the date
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Manual Attendance");

        String Absentee;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                Absentee = null;
                Name="";
                RegId=0;
                InTime="";
                OutTime="";
                InStatus="";
                OutStatus="";

            } else {
                Absentee = extras.getString("AbsentName");
                Name= extras.getString("Name");
                InTime=extras.getString("InTime");
                OutTime= extras.getString("OutTime");
                InStatus=extras.getString("InStatus");
                OutStatus=extras.getString("OutStatus");
                RegId=extras.getInt("RegId");


            }
        }
        else {
            Absentee=(String)savedInstanceState.getSerializable("AbsentName");
            Name= (String)savedInstanceState.getSerializable("Name");
            InStatus=(String)savedInstanceState.getSerializable("InStatus");
            OutStatus=(String)savedInstanceState.getSerializable("OutStatus");
            InStatus=(String)savedInstanceState.getSerializable("InStatus");
            InTime=(String)savedInstanceState.getSerializable("InTime");
            OutTime=(String)savedInstanceState.getSerializable("OutTime");
            RegId=(int)savedInstanceState.getSerializable("RegId");
        }
        if(Absentee==null){

        EmployeeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManualAtt.this,EmployeeListActivity.class);
                startActivity(intent);
            }
        });
        }
        else
        {
            EmployeeName.setText(Absentee);
            Log.d("ManualAtt"," the absentee name is "+Absentee);
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat datetime= new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String dte,tim;
            dte=dateFormat.format(date);
            EmployeeName.setText(Name);
            IN_TIME.setText(InTime);
            OUT_TIME.setText(OutTime);
            text_intime=InTime;
            text_outtime=OutTime;
            The_Date.setText(dte);
            Current_Date=dte;
            text_date=Current_Date;
            Name = Absentee;
        }






        The_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(ManualAtt.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                    /*      to get date and time    */
                        selectedmonth = selectedmonth + 1;
                        The_Date.setText("" + selectedmonth + "/" + selectedday + "/" + selectedyear);
                        text_date=The_Date.getText().toString();
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
            }
        });
IN_TIME.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(ManualAtt.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                IN_TIME.setText( selectedHour + ":" + selectedMinute);
                text_intime=IN_TIME.getText().toString();
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
});
        OUT_TIME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ManualAtt.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        OUT_TIME.setText( selectedHour + ":" + selectedMinute);
                        text_outtime=OUT_TIME.getText().toString();
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

         spinner = (Spinner) findViewById(R.id.instatus);
       adapter = ArrayAdapter.createFromResource(this,
                R.array.drop_down, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


       spinner1 = (Spinner) findViewById(R.id.outstatus);
        adapter1 = ArrayAdapter.createFromResource(this,
                R.array.drop_down, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(this);


        //p ,l, a, e
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
      isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_commments=(String) Comments.getText().toString();
                Log.d("ManualAtt"," the comment is "+text_commments);
                InputMethodManager inputManager = (InputMethodManager) ManualAtt.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(
                        ManualAtt.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                if(!text_commments.equals("") && !text_date.equals("") && !text_intime.equals("") && !text_outtime.equals("") && !Name.equals("")) {
                    if (isConnected) {
                        new ManualAttUpdate() {
                            @Override
                            protected void onPostExecute(Boolean result) {
                                super.onPostExecute(result);
                                if (result) {

                                    Toast.makeText(ManualAtt.this, "Manual Attendance successfully updated :)", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ManualAtt.this, "There was Something Wrong :(", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }.execute();
                    }
                    else
                    {
                        Toast.makeText(ManualAtt.this,"Please Check Network Connection ",Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                        Log.d("Manuatt", " Validation done came to else method on null data ");
                        Toast.makeText(ManualAtt.this, "Please Fill in All Details", Toast.LENGTH_SHORT).show();
                    }
                }



        });

Cancel.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        finish();
    }
});

    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        Spinner spin = (Spinner)parent;
        if(spin.getId()==R.id.instatus){
            InStatus=parent.getItemAtPosition(pos).toString().substring(0,1);

        }
        else
        {
            OutStatus=parent.getItemAtPosition(pos).toString().substring(0,1);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Spinner spin = (Spinner)parent;
        if(spin.getId()==R.id.instatus){
            InStatus="";
        }
        else {
            OutStatus="";
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extras = intent.getExtras();
            {
                Name = extras.getString("Name");
                InTime = extras.getString("InTime");
                OutTime = extras.getString("OutTime");
                InStatus = extras.getString("InStatus");
                OutStatus = extras.getString("OutStatus");
                RegId = extras.getInt("RegId");
                Log.d("ManualAtt", "it came here " + Name + " " + InStatus+" "+OutStatus);
                text_outtime=OutTime;
                text_intime=InTime;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        DateFormat datetime = new SimpleDateFormat("HH:mm:ss");
                        Date date = new Date();
                        String dte, tim;
                        dte = dateFormat.format(date);
                        EmployeeName.setText(Name);
                        IN_TIME.setText(InTime);
                        OUT_TIME.setText(OutTime);
                        The_Date.setText(dte);
                        Current_Date = dte;
                        text_date=dte;

                    }
                });
               ManualAtt.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (InStatus != null) {
                            if (InStatus.charAt(0) == 'P') {
                                spinner.setSelection(0);
                                Log.d("ManuAtt", " the INstatus and selection " + InStatus + " ");
                            } else if (InStatus.charAt(0) == 'L') {
                                spinner.setSelection(1);
                                Log.d("ManuAtt", " the INstatus and selection " + InStatus + " ");
                            } else if (InStatus.charAt(0) == 'A') {
                                spinner.setSelection(2);
                                Log.d("ManuAtt", " the INstatus and selection " + InStatus + " ");
                            } else {
                                spinner.setSelection(3);
                            }

                        }
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(OutStatus!=null){
                            if(OutStatus.equals("P")){
                                spinner1.setSelection(0);
                            }
                            else if(OutStatus.equals("L")){
                                spinner1.setSelection(1);
                            }
                            else if(OutStatus.equals("A")){
                                spinner1.setSelection(2);
                            }
                            else
                            {
                                spinner1.setSelection(3);
                            }
                        }
                    }
                });

            }
        }
    };
    class ManualAttUpdate extends AsyncTask<Void,Void,Boolean>{

        Response response;
        @Override
        protected Boolean doInBackground(Void... params) {
            SharedPreferences data = getSharedPreferences("DataStorage",0);
            config c= new config();
            String URL= c.getURL()+"/api/employee/PostManualAttendance";
            OkHttpClient client = new OkHttpClient();
            Log.d("ManualAtt"," the values are "+text_date+" "+text_outtime+" "+text_outtime+" "+InStatus+" "+OutStatus+" "+text_commments);
RequestBody formBody;
            Request request;

                 formBody = new FormBody.Builder()


                        .add("DateofTransaction", text_date)
                        .add("InTime", text_intime)
                        .add("OutTime", text_outtime)
                        .add("InStatus", InStatus)
                        .add("OutStatus", OutStatus)
                        .add("Comments", text_commments)
                        .add("UpdatedBy", data.getString("UserName", ""))
                        .add("RegistrationId", Integer.toString(RegId))
                        .add("SuperID", data.getString("SuperId", ""))


                        .build();
                 request = new Request.Builder()
                        .url(URL)
                        .post(formBody)
                        .build();


            try {
               response = client.newCall(request).execute();
                finalResponse=response;
                Log.d("ManualAtt"," response is "+response);
                String content = response.body().string();
                JSONObject obj = new JSONObject(content);
                Boolean Result = obj.getBoolean("Result");

 Log.d("ManualAtt"," the response is "+text_commments+" "+Result);
                return Result;


            } catch (IOException |JSONException e) {
                e.printStackTrace();
            }
          return null;
        }

    }
    @Override
    protected void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("TheName"));
    }




}
