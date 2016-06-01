package ivorylab.apps.rollcall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import ivorylab.apps.rollcall.Tools.config;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mayuukhvarshney on 23/05/16.
 */
public class EmployeeListActivity extends AppCompatActivity {
    ArrayList<Employee> AllNames;
    MyAdapter mAdapter;
RecyclerView absentlist;
    ProgressWheel prog;

    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.absent_list);
        AllNames= new ArrayList<>();
        absentlist= (RecyclerView) findViewById(R.id.absent_list);
        prog= (ProgressWheel) findViewById(R.id.progress_wheel);

        absentlist.setVisibility(View.INVISIBLE);
        prog.setVisibility(View.VISIBLE);
        prog.spin();
 new GetAllEmployees(){
     @Override
 protected void onPostExecute(ArrayList<Employee> array){
         super.onPostExecute(array);

         prog.stopSpinning();
         prog.setVisibility(View.INVISIBLE);
         absentlist.setVisibility(View.VISIBLE);
        //AllNames=array;



         View recyclerView = findViewById(R.id.absent_list);
         assert recyclerView != null;
         setupRecyclerView((RecyclerView) recyclerView);

     }
 }.execute();



    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter= new MyAdapter(AllNames);
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

                    Intent intent = new Intent("TheName");
                    intent.putExtra("Name",theNames.get(position).getUserName());
                    intent.putExtra("RegId",theNames.get(position).getRegID());
                    intent.putExtra("InStatus",theNames.get(position).getInStatus());
                    intent.putExtra("OutStatus",theNames.get(position).getOutStatus());
                    intent.putExtra("OutTime",theNames.get(position).getOutTime());
                    intent.putExtra("InTime",theNames.get(position).getInTime());
                    LocalBroadcastManager.getInstance(EmployeeListActivity.this).sendBroadcast(intent);
                    finish();
                }
            });
            holder.FirstLetter.setImageDrawable(drawable);
        }

        @Override
        public int getItemCount() {
            return theNames.size();
        }
    }
    class GetAllEmployees extends AsyncTask<Void,Void,ArrayList<Employee>>{

        @Override
        protected ArrayList<Employee> doInBackground(Void... params) {
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
                ArrayList<Employee> temp= new ArrayList<>();
                for(int i=0;i<array.length();i++)

                {
                    Employee emp = new Employee();
                    object=array.getJSONObject(i);
                    emp.setUserName(object.getString("UserName"));
                    emp.setInStatus(object.getString("InStatus"));
                    emp.setOutStatus(object.getString("OutStatus"));
                    emp.setInTime(object.getString("InTime"));
                    emp.setOutTime(object.getString("OutTime"));
                    emp.setRegID(object.getInt("RegistrationId"));
                    temp.add(emp);
                    AllNames.add(emp);
                  //  Log.d("EmployeeActiity","the names are "+AllNames.get(i).getUserName());


                }
                return temp;

            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
                //return null;
            }
            return null;
        }
    }

}
