package sheva.singapp.mvp.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import sheva.singapp.mvp.ui.fragments.FragmentEachItem;

/**
 * Created by shevc on 08.07.2017.
 * Let's GO!
 */

public class PagerWarmUpAdapter extends FragmentPagerAdapter {
    //private Context context;

    public PagerWarmUpAdapter(FragmentManager fm) {
        super(fm);
        //this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return FragmentEachItem.newInstance("");
            case 1:
                return FragmentEachItem.newInstance("");
            case 2:
                return FragmentEachItem.newInstance("");
            case 3:
                return FragmentEachItem.newInstance("");
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
