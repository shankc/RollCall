package ivorylab.apps.rollcall;

import android.content.SharedPreferences;
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

public class PresentListActivity extends AppCompatActivity {
    ArrayList<Employee> PresentEmployees;
    MyAdapter mAdapter;
    RecyclerView absent_list;
    ProgressWheel prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.absent_list);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Absent List");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        PresentEmployees= new ArrayList<>();
        absent_list= (RecyclerView) findViewById(R.id.absent_list);
        prog= (ProgressWheel) findViewById(R.id.progress_wheel);
        absent_list.setVisibility(View.INVISIBLE);
        prog.setVisibility(View.VISIBLE);
        prog.spin();
        new GetPresentEmployees(){

            @Override
        protected void onPostExecute(ArrayList<Employee> array){
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
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter= new MyAdapter(PresentEmployees);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<Employee> theNames;
        private Boolean mTwoPane;
        private ColorGenerator generator = ColorGenerator.MATERIAL;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mName;
            public TextView Time;

            public ImageView FirstLetter;

            public View mView;

            public MyViewHolder(View view) {
                super(view);
                mName = (TextView) view.findViewById(R.id.gmailitem_title);
                FirstLetter = (ImageView) view.findViewById(R.id.gmailitem_letter);
                Time= (TextView) view.findViewById(R.id.the_time);
                mView = view;


            }
        }


        public MyAdapter(ArrayList<Employee> names) {
            this.theNames = names;
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
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(letter, generator.getRandomColor());
            holder.FirstLetter.setImageDrawable(drawable);

            holder.Time.setText(theNames.get(position).getInTime().substring(0,5));
        }

        @Override
        public int getItemCount() {
            return theNames.size();
        }
    }


    class GetPresentEmployees extends AsyncTask<Void,Void,ArrayList<Employee>>{

        @Override
        protected ArrayList<Employee> doInBackground(Void... params) {
            config c = new config();
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat datetime= new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String dte,tim;
            dte=dateFormat.format(date);
            tim=datetime.format(date);
            ArrayList<Employee> temp = new ArrayList<>();
            SharedPreferences data = getSharedPreferences("DataStorage",0);

            OkHttpClient client = new OkHttpClient();
            String url = c.getURL()+"/api/employee/GetEmployees?SuperId="+data.getString("SuperId","")+"&DateOfTransaction="+dte+"&InStatus=P";
            Request request = new Request.Builder().url(url).build();

            Response response = null;
            try{
                response = client.newCall(request).execute();
                String content  = response.body().string();
                Log.d("Content ", content);
                JSONArray array = new JSONArray(content);
                Log.d("AbsentActivity"," the JSon arrya"+array);
                JSONObject obj= null;

                for(int i=0;i<array.length();i++)
                {
                    Employee emp= new Employee();
                    obj= array.getJSONObject(i);
                    emp.setUserName(obj.getString("UserName"));
                    emp.setInTime(obj.getString("InTime"));
                   // temp.add(emp);
                    PresentEmployees.add(emp);

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
