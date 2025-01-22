package cn.leftshine.apkexport.adapter;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import cn.leftshine.apkexport.fragment.AppFragment;

/**
 * Created by leftshine.com on 2018/2/1.
 */

public class ContentPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "ContentPagerAdapter";
    private List<AppFragment> tabFragments;
    private List<String> tabIndicators;
    private AppFragment mCurrentFragment;
    private Callback callback;

    public interface Callback {
        void onInstantiateItem(int position, AppFragment curFragment);
        void onSetPrimaryItem(int position, AppFragment curFragment, List<AppFragment> allFragments);
    }

    public ContentPagerAdapter(FragmentManager fm, List<AppFragment> tabFragments, List<String> tabIndicators, Callback callback) {
        super(fm);
        this.tabFragments = tabFragments;
        this.tabIndicators = tabIndicators;
        this.callback = callback;
    }

    @Override
    public Fragment getItem(int position) {
        return tabFragments.get(position);
    }

    @Override
    public int getCount() {
        return tabIndicators.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabIndicators.get(position);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Log.d(TAG, "setPrimaryItem: position="+position);
        super.setPrimaryItem(container, position, object);
        mCurrentFragment = (AppFragment) object;
        callback.onSetPrimaryItem(position, mCurrentFragment, tabFragments);
    }

    // reference https://stackoverflow.com/questions/14035090/how-to-get-existing-fragments-when-using-fragmentpageradapter
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
//        // save the appropriate reference depending on position
//        switch (position) {
//            case 0:
//                m1stFragment = (FragmentA) createdFragment;
//                break;
//            case 1:
//                m2ndFragment = (FragmentB) createdFragment;
//                break;
//        }
        tabFragments.set(position, (AppFragment) createdFragment);
        Log.d(TAG, "instantiateItem: tabFragments set "+ position);
        callback.onInstantiateItem(position, (AppFragment) createdFragment);
        return createdFragment;
    }

    public AppFragment getCurrentFragment(){
        return mCurrentFragment;
    }

    public AppFragment getFragment(int position){
        return tabFragments.get(position);
    }

    public List<AppFragment> getAllFragment(){
        return tabFragments;
    }

    public int getCurrentItemCount(){
        if (mCurrentFragment == null)  return 0;
        AppInfoAdapter adapter =  mCurrentFragment.getmAdapter();
        if (adapter == null) return 0;
        return adapter.getCount();
    }

}