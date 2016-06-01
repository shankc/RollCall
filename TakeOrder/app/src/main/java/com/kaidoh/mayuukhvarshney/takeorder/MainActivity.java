package com.kaidoh.mayuukhvarshney.takeorder;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout.LayoutParams;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TableLayout table;

    TableRow tr_head;
    TextView label_name,label_intime,label_outtime;
    ArrayList<String> Names,In_Time,Out_Time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        table = (TableLayout) findViewById(R.id.main_table);
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        tr_head = new TableRow(this);

        tr_head.setId(R.id.worker_table);
        tr_head.setBackgroundColor(Color.parseColor("#4D7AF9"));
        tr_head.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        label_name = new TextView(this);
        label_name.setId(R.id.worker_name);
        label_name.setText("Quantity");
        label_name.setTextColor(Color.BLACK);
        label_name.setPadding(5, 5, 5, 5);
        tr_head.addView(label_name);// add the column to the table row here

        label_intime = new TextView(this);
        label_intime.setId(R.id.worket_out);// define id that must be unique
        label_intime.setText("Item-Name"); // set the text for the header
        label_intime.setTextColor(Color.BLACK); // set the color
        label_intime.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_intime);

        label_outtime = new TextView(this);
        label_outtime.setId(R.id.worket_out);// define id that must be unique
        label_outtime.setText("Amount"); // set the text for the header
        label_outtime.setTextColor(Color.BLACK); // set the color
        label_outtime.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_outtime);

        table.addView(tr_head, new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));


        int count =0;
        for(int i=0;i<10;i++)
        {

            String Name=Integer.toString(i);
            String Intime = Integer.toString(i+10);
            String OutTime=Integer.toString(i + 1000);
// Create the table row
            TableRow tr = new TableRow(this);
            tr.setId(100 + count);
            tr.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));

            TextView names = new TextView(this);
            names.setId(200 + count);
            names.setText(Name);
            names.setPadding(5, 5, 5, 5);
            names.setTextColor(Color.BLACK);
            tr.addView(names);

            TextView in_time= new TextView(this);
            in_time.setId(200 + count);
            in_time.setText((Intime));
            in_time.setTextColor(Color.BLACK);
            tr.addView(in_time);

            TextView out_time= new TextView(this);
            out_time.setId(200+count);
            out_time.setText((OutTime));
            out_time.setTextColor(Color.BLACK);
            out_time.setPadding(5,0,0,0);
            tr.addView(out_time);
            table.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            count++;
        }





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

            FoodItemsFragment fragobj=new FoodItemsFragment();
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.FragmentContainer, fragobj);
            ft.addToBackStack(null);
            Log.d("MainActivity","the gallery has been clicked");
            ft.commit();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
