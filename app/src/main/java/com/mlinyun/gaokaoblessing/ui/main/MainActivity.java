package com.mlinyun.gaokaoblessing.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mlinyun.gaokaoblessing.R;
import com.mlinyun.gaokaoblessing.base.BaseActivity;
import com.mlinyun.gaokaoblessing.databinding.ActivityMainBinding;
import com.mlinyun.gaokaoblessing.ui.blessing.BlessingFragment;
import com.mlinyun.gaokaoblessing.ui.profile.ProfileFragment;
import com.mlinyun.gaokaoblessing.ui.student.StudentFragment;
import com.mlinyun.gaokaoblessing.ui.wall.WallFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private Fragment currentFragment;

    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected Class<MainViewModel> getViewModelClass() {
        return MainViewModel.class;
    }

    @Override
    protected void initView() {
        setupBottomNavigation();

        // 初始化Fragment
        initFragments();

        // 默认显示祈福页面
        switchFragment(R.id.nav_blessing);
    }

    @Override
    protected void initObserver() {
        // 主Activity暂时不需要特殊的观察者
    }

    @Override
    protected void initData() {
        // 主Activity暂时不需要初始化数据
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = mBinding.bottomNavigation;
        bottomNav.setOnItemSelectedListener(item -> {
            switchFragment(item.getItemId());
            return true;
        });
    }

    private void initFragments() {
        // 初始化各个Fragment
        fragmentMap.put(R.id.nav_blessing, new BlessingFragment());
        fragmentMap.put(R.id.nav_wall, new WallFragment());
        fragmentMap.put(R.id.nav_student, new StudentFragment());
        fragmentMap.put(R.id.nav_profile, new ProfileFragment());
    }

    private void switchFragment(int itemId) {
        Fragment fragment = fragmentMap.get(itemId);
        if (fragment == null) {
            // 如果Fragment还未实现，显示占位Fragment
            fragment = createPlaceholderFragment(getTabName(itemId));
        }

        if (fragment != currentFragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }

            if (fragment.isAdded()) {
                transaction.show(fragment);
            } else {
                transaction.add(R.id.nav_host_fragment, fragment);
            }

            transaction.commit();
            currentFragment = fragment;
        }
    }

    private Fragment createPlaceholderFragment(String tabName) {
        return new PlaceholderFragment(tabName);
    }

    private String getTabName(int itemId) {
        if (itemId == R.id.nav_blessing) return "祈福";
        else if (itemId == R.id.nav_wall) return "祈福墙";
        else if (itemId == R.id.nav_student) return "学生";
        else if (itemId == R.id.nav_profile) return "我的";
        else return "未知";
    }

    /**
     * 切换到个人中心页面
     */
    public void switchToProfile() {
        mBinding.bottomNavigation.setSelectedItemId(R.id.nav_profile);
        switchFragment(R.id.nav_profile);
    }

    /**
     * 切换到学生管理页面
     */
    public void switchToStudent() {
        mBinding.bottomNavigation.setSelectedItemId(R.id.nav_student);
        switchFragment(R.id.nav_student);
    }

    /**
     * 切换到祈福墙页面
     */
    public void switchToWall() {
        mBinding.bottomNavigation.setSelectedItemId(R.id.nav_wall);
        switchFragment(R.id.nav_wall);
    }

    /**
     * 占位Fragment，用于显示未实现的页面
     */
    public static class PlaceholderFragment extends Fragment {
        private String tabName;

        public PlaceholderFragment(String tabName) {
            this.tabName = tabName;
        }

        @Override
        public android.view.View onCreateView(android.view.LayoutInflater inflater,
                                              android.view.ViewGroup container,
                                              Bundle savedInstanceState) {
            android.widget.TextView textView = new android.widget.TextView(getActivity());
            textView.setText(tabName + " 页面正在开发中...");
            textView.setTextSize(18);
            textView.setGravity(android.view.Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.text_primary, null));
            return textView;
        }
    }
}