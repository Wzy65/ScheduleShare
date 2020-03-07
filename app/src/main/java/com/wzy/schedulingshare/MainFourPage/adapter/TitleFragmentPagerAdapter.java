package com.wzy.schedulingshare.MainFourPage.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;

/**
 * @ClassName TitleFragmentPagerAdapter
 * @Author Wei Zhouye
 * @Date 2020/2/23
 * @Version 1.0
 */
public class TitleFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;
    private String [] titles;

    public TitleFragmentPagerAdapter(FragmentManager fm, List<Fragment> mFragmentList) {
        super(fm);
        this.mFragmentList = mFragmentList;
    }

    /**
     * titles是给TabLayout设置title用的
     * @param fm
     * @param mFragmentList
     * @param titles
     */
    public TitleFragmentPagerAdapter(FragmentManager fm, List<Fragment> mFragmentList, String[] titles) {
        super(fm);
        this.mFragmentList = mFragmentList;
        this.titles = titles;
    }

    /**
     * 描述：获取索引位置的Fragment.
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position < mFragmentList.size()){
            fragment = mFragmentList.get(position);

        }else{
            fragment = mFragmentList.get(0);

        }
        return fragment;
    }

    /**
     * 返回viewpager对应的title。
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && titles.length>0){
            return titles[position];
        }
        return null;
    }

    /**
     * 描述：获取数量.
     * @return
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}