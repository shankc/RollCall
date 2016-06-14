package apps.perennialcode.rollcall;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

public class LeaveForm extends AppCompatActivity implements OnItemSelectedListener {

    TextView EndDate;
    EditText Comment;
    TextView StartDate,EmployeeName;
   String TypedComment="";
    Button Approve_Button,Cancel_Button;
    String dte="",tim="",textEndDate="",Name="",text_date="",Leave_Type="";
    int RegId=0;
    boolean isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_form);
        LocalBroadcastManager.getInstance(LeaveForm.this).registerReceiver(mMessageReceiver,
                new IntentFilter("TheName"));
        if(getSupportActionBar()!=null)
        getSupportActionBar().setTitle("Leave Application");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        StartDate = (TextView) findViewById(R.id.start_date);
        EndDate= (TextView) findViewById(R.id.end_date);
        Comment = (EditText) findViewById(R.id.comments);
        EmployeeName=(TextView) findViewById(R.id.employee_name);

        Approve_Button= (Button) findViewById(R.id.approve);
        Cancel_Button= (Button) findViewById(R.id.cancel);
        String Absentee;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                Absentee = null;
            } else {
                Absentee = extras.getString("AbsentName");
                RegId=extras.getInt("RegId");
                Name = Absentee;
                Log.d("LeaveForm"," the regid is => "+RegId);
            }
        } else {
            Absentee = (String) savedInstanceState.getSerializable("AbsentName");
            RegId=(int)savedInstanceState.getSerializable("RegId");
            Name = Absentee;
        }
        if(Absentee==null){
        EmployeeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaveForm.this,EmployeeListActivity.class);
                startActivity(intent);
            }
        });
        }
        else
        {
            EmployeeName.setText(Absentee);
            Log.d("LeaveForm"," the absentee name "+Absentee);
        }

        mMessageReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String name;
                Bundle extras= intent.getExtras();
                name= extras.getString("Name");
                RegId=extras.getInt("RegId");
                Log.d("LeaveForm"," the name and regid =>"+name+" "+RegId);
                Name = name;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EmployeeName.setText(name);

                    }
                });

            }
        };


        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
      dateFormat.format(cal.getTime());
        dte=dateFormat.format(cal.getTime());
        StartDate.setText(dte);
        text_date=dte;
        StartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(LeaveForm.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                    /*      to get date and time    */
                        selectedmonth = selectedmonth + 1;
                        StartDate.setText("" + selectedmonth + "/" + selectedday + "/" + selectedyear);
                        text_date=StartDate.getText().toString();
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
            }
        });
EndDate.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(LeaveForm.this, new OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                selectedmonth = selectedmonth + 1;
                EndDate.setText("" + selectedmonth + "/" + selectedday + "/" + selectedyear);
                textEndDate=EndDate.getText().toString();
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }
});

        Cancel_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Spinner spinner = (Spinner) findViewById(R.id.leavedrop);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.drop_late, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

Approve_Button.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(View v) {
        TypedComment = Comment.getText().toString();
        Log.d("LeaveForm", "the typed comment " + TypedComment+" "+Name+" ");
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!text_date.equals("") && !textEndDate.equals("") && !TypedComment.equals("") && !Name.equals("")) {
            if(isConnected) {
                Log.d("LeaveForm ", " validation sucess");
                new PostToDataBase() {
                    @Override
                    protected void onPostExecute(Boolean response) {
                        super.onPostExecute(response);
                        if (response != null) {
                            if (response) {
                                Toast.makeText(LeaveForm.this, "The Leave Application Posted :)", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LeaveForm.this, "There was a Problem :(", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(LeaveForm.this, "There was a Problem :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
            else
            {
                Toast.makeText(LeaveForm.this,"Please Check the Network Connection",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            //Log.d("LeaveForm","Validation done came in else method for null data ");
            Toast.makeText(LeaveForm.this,"Please Fill in All Fields",Toast.LENGTH_SHORT).show();
        }
    }

});
        Cancel_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        
    }
   private BroadcastReceiver mMessageReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String name;
            Bundle extras= intent.getExtras();
            name= extras.getString("Name");
            RegId=extras.getInt("RegId");
            Log.d("LeaveForm"," the name and regid =>"+name+" "+RegId);
            Name=name;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EmployeeName.setText(name);

                }
            });

        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Leave_Type=parent.getItemAtPosition(position).toString().substring(0,2);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Leave_Type="";

    }

    public class PostToDataBase extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
                Response response;
            config c= new config();
            String URL= c.getURL()+"/api/Employee/PostLeave";
            SharedPreferences data = getSharedPreferences("DataStorage",0);
            OkHttpClient client = new OkHttpClient();
Log.d("LeaveForm"," the regid=> "+RegId+" "+text_date+" "+textEndDate+" "+TypedComment);
            RequestBody formBody = new FormBody.Builder()
                    .add("RegistrationId", Integer.toString(RegId))
                    .add("LeaveType", Leave_Type) // apply a drop down.
                     .add("StartDate",text_date)
                    .add("EndDate", textEndDate)
                    .add("UpdatedBy",data.getString("UserName", ""))
                    .add("Comments", TypedComment)
                    .add("SuperId",data.getString("SuperId",""))



                    .build();
            Request request = new Request.Builder()
                    .url(URL)
                    .post(formBody)
                    .build();

            try {
                 response = client.newCall(request).execute();
                Log.d("LeaveForm"," response is => "+response);
                String content = response.body().string();
                JSONObject object = new JSONObject(content);
                boolean result;
                if(response.isSuccessful())
                {
                    result = object.getBoolean("Result");
                }
                else
                {
                    result=false;
                }
                Log.d("LeaveForm ","Result=> "+result);

                return result;
                // Do something with the response.
            } catch (IOException |JSONException e) {
                e.printStackTrace();
            }
            return null ;
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("TheName"));
    }


}
