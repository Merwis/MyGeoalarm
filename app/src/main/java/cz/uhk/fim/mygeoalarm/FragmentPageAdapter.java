package cz.uhk.fim.mygeoalarm;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Petr on 11. 5. 2015.
 */
public class FragmentPageAdapter extends FragmentPagerAdapter {

    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;
    private Context mContext;

    public FragmentPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags = new HashMap<Integer, String>();
        mContext = context;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return new MainFragmentTab();
            case 1:
                return new DestinationsListFragmentTab();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);

        if (obj instanceof Fragment) {
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        Log.d("adapter", "tag " + tag);
        if (tag == null) {
            return null;
        }
        return mFragmentManager.findFragmentByTag(tag);
    }
}
