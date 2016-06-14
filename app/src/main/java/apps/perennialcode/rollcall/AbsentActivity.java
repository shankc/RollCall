package apps.perennialcode.rollcall;

/**
 * Created by mayuukhvarshney on 16/05/16.
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
public class AbsentActivity extends AppCompatActivity {

    MyAdapter mAdapter;
    ArrayList<Employee> Names;

    String Choice;
    String[] options= {"Leave Application","Manual Attendance"};
    RecyclerView absent_list;
    ProgressWheel prog;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.absent_list);
        Names= new ArrayList<>();
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Absent List");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // get oriinal daata the list from as a JSON call. preferably absentees list.
        absent_list= (RecyclerView) findViewById(R.id.absent_list);
        prog= (ProgressWheel) findViewById(R.id.progress_wheel);
        absent_list.setVisibility(View.INVISIBLE);
        prog.setVisibility(View.VISIBLE);
        prog.spin();
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected) {
            new GetAbsentees() {
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
        { prog.stopSpinning();
            prog.setVisibility(View.INVISIBLE);
            Toast.makeText(AbsentActivity.this,"Please Check Network Connectivity",Toast.LENGTH_SHORT).show();

        }
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter= new MyAdapter(Names);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<Employee> theNames;
        private Boolean mTwoPane;
        private ColorGenerator generator = ColorGenerator.MATERIAL;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mName;

            public ImageView FirstLetter;

            public View mView;
            public MyViewHolder(View view) {
                super(view);
                mName=(TextView)view.findViewById(R.id.gmailitem_title);
                FirstLetter=(ImageView)view.findViewById(R.id.gmailitem_letter);
                mView = view;


            }
        }


        public MyAdapter(ArrayList<Employee> names) {
            this.theNames = names;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.absent_list_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.mName.setText(theNames.get(position).getUserName());
            String letter = String.valueOf(theNames.get(position).getUserName().charAt(0));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(letter, generator.getRandomColor());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(AbsentActivity.this);
                    // Set the dialog title
                    builder.setTitle(R.string.change_details)
                            // Specify the list array, the items to be selected by default (null for none),
                            // and the listener through which to receive callbacks when items are selected
                            .setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        Intent intent1 = new Intent(AbsentActivity.this,LeaveForm.class);
                                        intent1.putExtra("AbsentName",theNames.get(position).getUserName());
                                        intent1.putExtra("RegId", theNames.get(position).getRegID());
                                        startActivity(intent1);
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(AbsentActivity.this,ManualAtt.class);
                                        intent.putExtra("AbsentName",theNames.get(position).getUserName());
                                        intent.putExtra("Name",theNames.get(position).getUserName());
                                        intent.putExtra("RegId",theNames.get(position).getRegID());
                                        intent.putExtra("InStatus",theNames.get(position).getInStatus());
                                        intent.putExtra("OutStatus",theNames.get(position).getOutStatus());
                                        intent.putExtra("OutTime",theNames.get(position).getOutTime());
                                        intent.putExtra("InTime",theNames.get(position).getInTime());
                                        startActivity(intent);
                                    }
                                }
                            });
                    builder.create();
                    builder.show();
                }
            });
          holder.FirstLetter.setImageDrawable(drawable);
        }

        @Override
        public int getItemCount() {
            return theNames.size();
        }
    }
    class GetAbsentees extends AsyncTask<Void,Void,ArrayList<Employee>>{

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
            SharedPreferences data = getSharedPreferences("DataStorage",0);

            OkHttpClient client = new OkHttpClient();
            String url = c.getURL()+"/api/employee/GetEmployees?SuperId="+data.getString("SuperId","")+"&DateOfTransaction="+dte+"&InStatus=A";
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
                    Names.add(emp);

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
