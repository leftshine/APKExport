package cn.leftshine.apkexport.adapter;

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
    private List<AppFragment> tabFragments;
    private List<String> tabIndicators;
    private AppFragment mCurrentFragment;

    public ContentPagerAdapter(FragmentManager fm, List<AppFragment> tabFragments, List<String> tabIndicators) {
        super(fm);
        this.tabFragments = tabFragments;
        this.tabIndicators = tabIndicators;
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
        mCurrentFragment = (AppFragment) object;
        super.setPrimaryItem(container, position, object);
    }

    public AppFragment getCurrentFragment(){
        return mCurrentFragment;
    }
}