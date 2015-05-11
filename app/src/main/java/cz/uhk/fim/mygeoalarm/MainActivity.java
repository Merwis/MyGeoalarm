package cz.uhk.fim.mygeoalarm;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;


public class MainActivity extends Activity {

    ActionBar.Tab mainTab, destinationsListTab;
    Fragment mainFragmentTab = new MainFragmentTab();
    Fragment destinationsListFragmentTab = new DestinationsListFragmentTab();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();

        //actionBar.setDisplayShowHomeEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mainTab = actionBar.newTab().setText("Hlavni obrazovka");
        destinationsListTab = actionBar.newTab().setText("Seznam destinaci");

        mainTab.setTabListener(new TabListener(mainFragmentTab));
        destinationsListTab.setTabListener(new TabListener(destinationsListFragmentTab));

        getActionBar().addTab(mainTab);
        getActionBar().addTab(destinationsListTab);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
