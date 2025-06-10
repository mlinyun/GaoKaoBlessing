package com.mlinyun.gaokaoblessing.ui.blessing;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.mlinyun.gaokaoblessing.R;
import com.mlinyun.gaokaoblessing.databinding.ActivityBlessingWallBinding;
import com.mlinyun.gaokaoblessing.ui.blessing.adapter.BlessingWallAdapter;
import com.mlinyun.gaokaoblessing.ui.blessing.viewmodel.BlessingWallViewModel;

/**
 * 祈福墙Activity - 按照UI原型图实现
 * 包含祈福列表、分类筛选、搜索、实时更新等功能
 */
public class BlessingWallActivity extends AppCompatActivity {

    private ActivityBlessingWallBinding binding;
    private BlessingWallViewModel viewModel;
    private BlessingWallAdapter adapter;

    // 筛选状态
    private String currentCategory = "全部";
    private String currentSortOrder = "最新";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlessingWallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolbar();
        initViewModel();
        initViews();
        initObservers();
        loadInitialData();
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("祈福墙");
        }
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(BlessingWallViewModel.class);
    }

    private void initViews() {
        // 设置RecyclerView
        adapter = new BlessingWallAdapter();
        adapter.setOnBlessingClickListener(this::onBlessingClick);
        adapter.setOnLikeClickListener(this::onLikeClick);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        binding.rvBlessings.setLayoutManager(layoutManager);
        binding.rvBlessings.setAdapter(adapter);

        // 设置分类标签
        initCategoryTabs();

        // 设置排序选项
        initSortOptions();

        // 设置搜索功能
        setupSearch();

        // 设置下拉刷新
        binding.swipeRefresh.setOnRefreshListener(() -> {
            refreshBlessings();
        });

        // 设置浮动操作按钮
        binding.fabCreate.setOnClickListener(v -> {
            // 跳转到创建祈福页面
            startActivity(new android.content.Intent(this, CreateBlessingActivity.class));
        });
    }

    private void initObservers() {
        // 观察祈福列表
        viewModel.getBlessings().observe(this, blessings -> {
            if (blessings != null) {
                adapter.updateBlessings(blessings);
                binding.swipeRefresh.setRefreshing(false);

                // 更新统计信息
                binding.tvBlessingCount.setText(String.format("共 %d 条祈福", blessings.size()));
            }
        });

        // 观察加载状态
        viewModel.getLoading().observe(this, loading -> {
            if (!binding.swipeRefresh.isRefreshing()) {
                binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });

        // 观察错误信息
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                binding.swipeRefresh.setRefreshing(false);
            }
        });

        // 观察分类列表
        viewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                updateCategoryTabs(categories);
            }
        });
    }

    private void loadInitialData() {
        viewModel.loadBlessings(currentCategory, currentSortOrder);
        viewModel.loadCategories();
    }

    private void initCategoryTabs() {
        // 默认分类
        String[] defaultCategories = {"全部", "学业", "健康", "平安", "成功", "鼓励"};

        for (String category : defaultCategories) {
            TabLayout.Tab tab = binding.tlCategories.newTab();
            tab.setText(category);
            binding.tlCategories.addTab(tab);
        }

        binding.tlCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    private void updateCategoryTabs(java.util.List<String> categories) {
        binding.tlCategories.removeAllTabs();

        // 添加"全部"标签
        TabLayout.Tab allTab = binding.tlCategories.newTab();
        allTab.setText("全部");
        binding.tlCategories.addTab(allTab);

        // 添加其他分类
        for (String category : categories) {
            TabLayout.Tab tab = binding.tlCategories.newTab();
            tab.setText(category);
            binding.tlCategories.addTab(tab);
        }
    }

    private void initSortOptions() {
        binding.chipLatest.setOnClickListener(v -> {
            currentSortOrder = "最新";
            updateSortChips();
            loadBlessings();
        });

        binding.chipPopular.setOnClickListener(v -> {
            currentSortOrder = "热门";
            updateSortChips();
            loadBlessings();
        });

        binding.chipRandom.setOnClickListener(v -> {
            currentSortOrder = "随机";
            updateSortChips();
            loadBlessings();
        });

        updateSortChips();
    }

    private void updateSortChips() {
        binding.chipLatest.setChecked("最新".equals(currentSortOrder));
        binding.chipPopular.setChecked("热门".equals(currentSortOrder));
        binding.chipRandom.setChecked("随机".equals(currentSortOrder));
    }

    private void setupSearch() {
        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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

    private void loadBlessings() {
        viewModel.loadBlessings(currentCategory, currentSortOrder);
    }

    private void refreshBlessings() {
        viewModel.refreshBlessings(currentCategory, currentSortOrder);
    }

    private void searchBlessings(String query) {
        viewModel.searchBlessings(query);
    }

    private void onBlessingClick(com.mlinyun.gaokaoblessing.data.entity.BlessingEntity blessing) {
        // 显示祈福详情
        showBlessingDetail(blessing);
    }

    private void onLikeClick(com.mlinyun.gaokaoblessing.data.entity.BlessingEntity blessing) {
        // 处理点赞
        viewModel.toggleLike(blessing.getId());
    }

    private void showBlessingDetail(com.mlinyun.gaokaoblessing.data.entity.BlessingEntity blessing) {
        // 显示祈福详情对话框或跳转到详情页
        Toast.makeText(this, "祈福详情: " + blessing.getContent(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blessing_wall, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_filter) {
            showFilterDialog();
            return true;
        } else if (itemId == R.id.action_share) {
            shareBlessingWall();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showFilterDialog() {
        // 显示筛选对话框
        Toast.makeText(this, "筛选功能开发中...", Toast.LENGTH_SHORT).show();
    }

    private void shareBlessingWall() {
        // 分享祈福墙
        android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "来看看高考祈福墙，为考生们加油吧！");
        startActivity(android.content.Intent.createChooser(shareIntent, "分享祈福墙"));
    }
}
