package apps.perennialcode.rollcall;

/**
 * Created by mayuukhvarshney on 24/05/16.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
public class InboxActivity extends AppCompatActivity {
    InboxAdapter mAdapter;
    ArrayList<String >Messages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);
        Messages= new ArrayList<>();
        for(int i=0;i<10;i++)
        {
            Messages.add(Integer.toString(i+1000));
        }

        View recyclerView = findViewById(R.id.messages_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);


    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter= new InboxAdapter(Messages);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }
    public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.MyViewHolder> {

        private ArrayList<String> MessageBody;
     //   private Boolean mTwoPane;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView InNumber,InMessageBody;
            private View mView;

            public MyViewHolder(View view) {
                super(view);
                InNumber=(TextView)view.findViewById(R.id.NumberMessage);
                InMessageBody=(TextView)view.findViewById(R.id.MessageBody);
                mView=view;

            }
        }


        public InboxAdapter(ArrayList<String> messages) {
            this.MessageBody = messages;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.inbox_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final String message= MessageBody.get(position);
            holder.InNumber.setText(message);
            holder.InMessageBody.setText("Monkey D Luffy");

        }

        @Override
        public int getItemCount() {
            return MessageBody.size();
        }
    }


}
