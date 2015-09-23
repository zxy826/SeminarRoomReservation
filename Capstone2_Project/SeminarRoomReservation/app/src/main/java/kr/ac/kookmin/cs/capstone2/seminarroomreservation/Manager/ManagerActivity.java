package kr.ac.kookmin.cs.capstone2.seminarroomreservation.Manager;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

import kr.ac.kookmin.cs.capstone2.seminarroomreservation.R;
import kr.ac.kookmin.cs.capstone2.seminarroomreservation.Reservation.UsingStatusFragment;


public class ManagerActivity extends AppCompatActivity implements DialogInterface.OnDismissListener, UsingStatusFragment.AccidentListener {

    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        viewPager  = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new CustomPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manager, menu);
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



    @Override
    public void onDismiss(DialogInterface dialog) {
        Fragment fragment = ((CustomPagerAdapter) viewPager.getAdapter()).getFragment(0);
        if (fragment != null) {
            fragment.onResume();
        }
    }

    @Override
    public void deliverData() {
        Fragment fragment = ((CustomPagerAdapter) viewPager.getAdapter()).getFragment(0);
        if (fragment != null) {
            fragment.onResume();
            System.out.println("deliverData check");
        }
    }
}
