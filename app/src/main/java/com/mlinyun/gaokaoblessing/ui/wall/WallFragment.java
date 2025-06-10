package com.mlinyun.gaokaoblessing.ui.wall;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.mlinyun.gaokaoblessing.base.BaseFragment;
import com.mlinyun.gaokaoblessing.databinding.FragmentWallBinding;
import com.mlinyun.gaokaoblessing.ui.blessing.CreateBlessingActivity;
import com.mlinyun.gaokaoblessing.ui.blessing.adapter.BlessingWallAdapter;

/**
 * 祈福墙Fragment - 显示所有用户的祈福内容
 */
public class WallFragment extends BaseFragment<FragmentWallBinding, WallViewModel> {

    private BlessingWallAdapter adapter;

    // 筛选状态
    private String currentCategory = "全部";
    private String currentSortOrder = "最新";

    @Override
    protected FragmentWallBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentWallBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<WallViewModel> getViewModelClass() {
        return WallViewModel.class;
    }

    @Override
    protected void initView() {
        setupRecyclerView();
        setupCategoryTabs();
        setupSortOptions();
        setupSearch();
        setupRefresh();
        setupFAB();
    }

    @Override
    protected void initObserver() {
        // 观察祈福列表
        mViewModel.getBlessings().observe(this, blessings -> {
            if (blessings != null) {
                adapter.updateBlessings(blessings);
                mBinding.swipeRefresh.setRefreshing(false);

                // 更新统计信息
                mBinding.tvBlessingCount.setText(String.format("共 %d 条祈福", blessings.size()));
            }
        });

        // 观察加载状态
        mViewModel.getLoading().observe(this, loading -> {
            if (!mBinding.swipeRefresh.isRefreshing()) {
                mBinding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });

        // 观察错误信息
        mViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                mBinding.swipeRefresh.setRefreshing(false);
            }
        });

        // 观察分类列表
        mViewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                updateCategoryTabs(categories);
            }
        });
    }

    @Override
    protected void initData() {
        loadInitialData();
    }

    private void setupRecyclerView() {
        // 设置RecyclerView
        adapter = new BlessingWallAdapter();
        adapter.setOnBlessingClickListener(this::onBlessingClick);
        adapter.setOnLikeClickListener(this::onLikeClick);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mBinding.rvBlessings.setLayoutManager(layoutManager);
        mBinding.rvBlessings.setAdapter(adapter);
    }

    private void setupCategoryTabs() {
        // 默认分类
        String[] defaultCategories = {"全部", "学业", "健康", "平安", "成功", "鼓励"};

        for (String category : defaultCategories) {
            TabLayout.Tab tab = mBinding.tlCategories.newTab();
            tab.setText(category);
            mBinding.tlCategories.addTab(tab);
        }

        mBinding.tlCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentCategory = tab.getText().toString();
                loadBlessings();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupSortOptions() {
        mBinding.chipLatest.setOnClickListener(v -> {
            currentSortOrder = "最新";
            updateSortChips();
            loadBlessings();
        });

        mBinding.chipPopular.setOnClickListener(v -> {
            currentSortOrder = "热门";
            updateSortChips();
            loadBlessings();
        });

        mBinding.chipRandom.setOnClickListener(v -> {
            currentSortOrder = "随机";
            updateSortChips();
            loadBlessings();
        });

        updateSortChips();
    }

    private void setupSearch() {
        mBinding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBlessings(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadBlessings();
                }
                return true;
            }
        });
    }

    private void setupRefresh() {
        mBinding.swipeRefresh.setOnRefreshListener(() -> {
            refreshBlessings();
        });
    }

    private void setupFAB() {
        mBinding.fabCreate.setOnClickListener(v -> {
            // 跳转到创建祈福页面
            startActivity(new Intent(getContext(), CreateBlessingActivity.class));
        });
    }

    private void loadInitialData() {
        mViewModel.loadBlessings(currentCategory, currentSortOrder);
        mViewModel.loadCategories();
    }

    private void updateCategoryTabs(java.util.List<String> categories) {
        mBinding.tlCategories.removeAllTabs();

        // 添加"全部"标签
        TabLayout.Tab allTab = mBinding.tlCategories.newTab();
        allTab.setText("全部");
        mBinding.tlCategories.addTab(allTab);

        // 添加其他分类
        for (String category : categories) {
            TabLayout.Tab tab = mBinding.tlCategories.newTab();
            tab.setText(category);
            mBinding.tlCategories.addTab(tab);
        }
    }

    private void updateSortChips() {
        mBinding.chipLatest.setChecked("最新".equals(currentSortOrder));
        mBinding.chipPopular.setChecked("热门".equals(currentSortOrder));
        mBinding.chipRandom.setChecked("随机".equals(currentSortOrder));
    }

    private void loadBlessings() {
        mViewModel.loadBlessings(currentCategory, currentSortOrder);
    }

    private void refreshBlessings() {
        mViewModel.refreshBlessings(currentCategory, currentSortOrder);
    }

    private void searchBlessings(String query) {
        mViewModel.searchBlessings(query);
    }

    private void onBlessingClick(com.mlinyun.gaokaoblessing.data.entity.BlessingEntity blessing) {
        // 显示祈福详情
        showBlessingDetail(blessing);
    }

    private void onLikeClick(com.mlinyun.gaokaoblessing.data.entity.BlessingEntity blessing) {
        // 处理点赞
        mViewModel.toggleLike(blessing.getId());
    }

    private void showBlessingDetail(com.mlinyun.gaokaoblessing.data.entity.BlessingEntity blessing) {
        // 显示祈福详情对话框或跳转到详情页
        Toast.makeText(getContext(), "祈福详情: " + blessing.getContent(), Toast.LENGTH_SHORT).show();
    }
}
