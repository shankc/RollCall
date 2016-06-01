package ivorylab.apps.rollcall;

/**
 * Created by mayuukhvarshney on 23/05/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class More extends AppCompatActivity {
    MyMoreAdapter mAdapter;
    ArrayList<String> More;
    ListView morelist;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
       setContentView(R.layout.more_list);
        morelist= (ListView) findViewById(R.id.moreitems_list);
   More= new ArrayList<>();
        More.add("Leave Application");
        More.add("Manual Attendance");
        More.add("Daily In-Out");
        More.add("Inbox");
        More.add("Outbox");

        mAdapter = new MyMoreAdapter(this,More);
        morelist.setAdapter(mAdapter);

        morelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent intent = new Intent(More.this,LeaveForm.class);
                    startActivity(intent);
                }
                else if(position==1){
                    Intent intent = new Intent(More.this,ManualAtt.class);
                    startActivity(intent);
                }
                else if(position==2){
                    Intent intent = new Intent(More.this,DailyRegister.class);
                    startActivity(intent);
                }
                else if(position==3){

                    Intent intent = new Intent(More.this,InboxActivity.class);
                    startActivity(intent);
                }
                else
                {

                }
            }
        });


    }
}
