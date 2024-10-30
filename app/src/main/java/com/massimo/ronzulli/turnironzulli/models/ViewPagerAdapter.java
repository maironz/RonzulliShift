package com.massimo.ronzulli.turnironzulli.models;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> listFragments = new ArrayList();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    public void addFragment(Fragment fragment){
        this.listFragments.add(fragment);
    }
    public void removeFragment(Fragment fragment){
        this.listFragments.remove(fragment);
    }
    public void replaceFragment(Fragment fragment,int pos){
        this.listFragments.set(pos,fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return listFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return listFragments.size();
    }

}
