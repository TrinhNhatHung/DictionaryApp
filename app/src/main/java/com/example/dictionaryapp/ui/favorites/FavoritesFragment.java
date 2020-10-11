package com.example.dictionaryapp.ui.favorites;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.dictionaryapp.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLanguage;
    private Fragment favoriteEnglishFragment;
    private Fragment favoriteVietNameseFragment;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        return root;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favoriteEnglishFragment = new FavoriteEnglishFragment();
        favoriteVietNameseFragment = new FavoriteVietnameseFragment();
        viewPager = view.findViewById(R.id.view_pager);
        tabLanguage = view.findViewById(R.id.tab_langueges);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(),0);
        viewPager.setAdapter(viewPagerAdapter);
        tabLanguage.setupWithViewPager(viewPager);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{

        private List<Fragment> fragments = new ArrayList<Fragment>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            fragments.add(favoriteEnglishFragment);
            fragments.add(favoriteVietNameseFragment);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return favoriteEnglishFragment;
            }
            if (position == 1){
                return favoriteVietNameseFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "English";
            } else {
                return "Vietnamese";
            }
        }
    }

}