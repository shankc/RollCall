package apps.perennialcode.rollcall;

/**
 * Created by mayuukhvarshney on 17/05/16.
 *
 *
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import apps.perennialcode.rollcall.Tools.config;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class LateActivity extends AppCompatActivity {
    ArrayList<String> Times;ArrayList<Employee>NAMES;
    MyAdapter mAdapter;
    String[] options= {"Change Employee Timing"};
    RecyclerView absent_list;
    ProgressWheel prog;
    boolean isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.absent_list);
        Times = new ArrayList<>();
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Late List");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat datetime= new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String dte,tim;
        dte=dateFormat.format(date);
        tim=datetime.format(date);

        NAMES= new ArrayList<>();
        absent_list= (RecyclerView) findViewById(R.id.absent_list);
        prog= (ProgressWheel) findViewById(R.id.progress_wheel);
        absent_list.setVisibility(View.INVISIBLE);
        prog.setVisibility(View.VISIBLE);
        prog.spin();
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected) {
            new GetLateEmplyees() {
                @Override
                protected void onPostExecute(ArrayList<Employee> array) {
                    super.onPostExecute(array);
                    prog.stopSpinning();
                    prog.setVisibility(View.INVISIBLE);
                    absent_list.setVisibility(View.VISIBLE);
                    View recyclerView = findViewById(R.id.absent_list);
                    assert recyclerView != null;
                    setupRecyclerView((RecyclerView) recyclerView);

                }
            }.execute();
        }
        else
        {
            prog.stopSpinning();
            prog.setVisibility(View.INVISIBLE);
            Toast.makeText(LateActivity.this,"Please Check the Network Connection",Toast.LENGTH_SHORT).show();
        }


    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter= new MyAdapter(NAMES,Times);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();



    }
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<Employee> theNames;ArrayList<String>theTime;
        private Boolean mTwoPane;
        private ColorGenerator generator = ColorGenerator.MATERIAL;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mName;

            public ImageView FirstLetter;
            public TextView EntryTime;

            public View mView;
            public MyViewHolder(View view) {
                super(view);
                mName=(TextView)view.findViewById(R.id.gmailitem_title);
                FirstLetter=(ImageView)view.findViewById(R.id.gmailitem_letter);
                EntryTime= (TextView) view.findViewById(R.id.the_time);
                mView=view;


            }
        }


        public MyAdapter(ArrayList<Employee> names,ArrayList<String> times) {
            this.theNames = names;this.theTime=times;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.late_list_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.mName.setText(theNames.get(position).getUserName());
            String letter = String.valueOf(theNames.get(position).getUserName().charAt(0));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LateActivity.this);
                    // Set the dialog title
                    builder.setTitle(R.string.change_latedetails)
                            // Specify the list array, the items to be selected by default (null for none),
                            // and the listener through which to receive callbacks when items are selected
                            .setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(LateActivity.this,ManualAtt.class);
                                        intent.putExtra("AbsentName",theNames.get(position).getUserName());
                                        intent.putExtra("Name",theNames.get(position).getUserName());
                                        intent.putExtra("RegId",theNames.get(position).getRegID());
                                        intent.putExtra("InStatus",theNames.get(position).getInStatus());
                                        intent.putExtra("OutStatus",theNames.get(position).getOutStatus());
                                        intent.putExtra("OutTime",theNames.get(position).getOutTime());
                                        intent.putExtra("InTime",theNames.get(position).getInTime());
                                        startActivity(intent);

                                }
                            });
                    builder.create();
                    builder.show();
                }
            });
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(letter, generator.getRandomColor());
            holder.FirstLetter.setImageDrawable(drawable);
            holder.EntryTime.setText(theTime.get(position).substring(0,5));
        }

        @Override
        public int getItemCount() {
            return theNames.size();
        }
    }

    class GetLateEmplyees extends AsyncTask<Void,Void,ArrayList<Employee>>{


        @Override
        protected ArrayList<Employee> doInBackground(Void... params) {
            config c = new config();
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat datetime= new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String dte,tim;
            dte=dateFormat.format(date);
            tim=datetime.format(date);
            ArrayList<String> temp = new ArrayList<>();
            ArrayList<String> tempTime= new ArrayList<>();
            SharedPreferences data = getSharedPreferences("DataStorage",0);

            OkHttpClient client = new OkHttpClient();
            String url = c.getURL()+"/api/employee/GetEmployees?SuperId="+data.getString("SuperId","")+"&DateOfTransaction="+dte+"&InStatus=L";
            Request request = new Request.Builder().url(url).build();

            Response response = null;
            try{
                response = client.newCall(request).execute();
                String content  = response.body().string();
                Log.d("Content ", content);
                JSONArray array = new JSONArray(content);
                Log.d("AbsentActivity"," the JSon arrya"+array);
                JSONObject object= null;
                for(int i=0;i<array.length();i++)
                {
                    Employee emp = new Employee();
                    object= array.getJSONObject(i);
                    emp.setUserName(object.getString("UserName"));
                    emp.setInStatus(object.getString("InStatus"));
                    emp.setOutStatus(object.getString("OutStatus"));
                    emp.setInTime(object.getString("InTime"));
                    emp.setOutTime(object.getString("OutTime"));
                    emp.setRegID(object.getInt("RegistrationId"));
                    NAMES.add(emp);
                    Times.add(object.getString("InTime"));

                }
                return NAMES;

            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
                //return null;
            }
            return null;
        }
    }

}
