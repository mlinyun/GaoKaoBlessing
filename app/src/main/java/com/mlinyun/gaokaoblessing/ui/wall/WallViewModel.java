package com.mlinyun.gaokaoblessing.ui.wall;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mlinyun.gaokaoblessing.data.database.AppDatabase;
import com.mlinyun.gaokaoblessing.data.entity.BlessingEntity;
import com.mlinyun.gaokaoblessing.data.repository.BlessingRepository;
import com.mlinyun.gaokaoblessing.data.repository.BlessingTemplateRepository;
import com.mlinyun.gaokaoblessing.data.repository.impl.BlessingRepositoryImpl;
import com.mlinyun.gaokaoblessing.data.repository.impl.BlessingTemplateRepositoryImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 祈福墙ViewModel
 */
public class WallViewModel extends AndroidViewModel {

    private final BlessingRepository blessingRepository;
    private final BlessingTemplateRepository templateRepository;

    // LiveData
    private final MutableLiveData<List<BlessingEntity>> blessings = new MutableLiveData<>();
    private final MutableLiveData<List<String>> categories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public WallViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(application);
        this.blessingRepository = new BlessingRepositoryImpl(database.blessingDao());
        this.templateRepository = new BlessingTemplateRepositoryImpl(database.blessingTemplateDao());
    }

    // Getters for LiveData
    public LiveData<List<BlessingEntity>> getBlessings() {
        return blessings;
    }

    public LiveData<List<String>> getCategories() {
        return categories;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    /**
     * 加载祈福列表
     */
    public void loadBlessings(String category, String sortOrder) {
        loading.setValue(true);
        error.setValue("");

        if ("全部".equals(category)) {
            // 加载所有公开祈福
            loadAllBlessings(sortOrder);
        } else {
            // 根据分类加载
            loadBlessingsByCategory(category, sortOrder);
        }
    }

    /**
     * 刷新祈福列表
     */
    public void refreshBlessings(String category, String sortOrder) {
        loadBlessings(category, sortOrder);
    }

    /**
     * 搜索祈福
     */
    public void searchBlessings(String keyword) {
        loading.setValue(true);
        error.setValue("");

        blessingRepository.searchBlessings(keyword)
                .thenAccept(blessingList -> {
                    blessings.postValue(blessingList);
                    loading.postValue(false);
                })
                .exceptionally(throwable -> {
                    error.postValue("搜索失败: " + throwable.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }

    /**
     * 切换点赞状态
     */
    public void toggleLike(long blessingId) {
        blessingRepository.updateLikeCount(blessingId, 1)
                .thenRun(() -> {
                    // 重新加载当前列表以更新点赞数
                    refreshCurrentBlessings();
                })
                .exceptionally(throwable -> {
                    error.postValue("点赞失败: " + throwable.getMessage());
                    return null;
                });
    }

    /**
     * 加载分类列表
     */
    public void loadCategories() {
        List<String> categoryList = new ArrayList<>();
        categoryList.add("学业");
        categoryList.add("健康");
        categoryList.add("平安");
        categoryList.add("成功");
        categoryList.add("鼓励");
        categories.setValue(categoryList);
    }

    private void loadAllBlessings(String sortOrder) {
        blessingRepository.getPublicBlessings(50, 0)
                .thenAccept(blessingList -> {
                    List<BlessingEntity> sortedList = sortBlessings(blessingList, sortOrder);
                    blessings.postValue(sortedList);
                    loading.postValue(false);
                })
                .exceptionally(throwable -> {
                    error.postValue("加载失败: " + throwable.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }

    private void loadBlessingsByCategory(String category, String sortOrder) {
        blessingRepository.getBlessingsByCategory(category)
                .thenAccept(blessingList -> {
                    List<BlessingEntity> sortedList = sortBlessings(blessingList, sortOrder);
                    blessings.postValue(sortedList);
                    loading.postValue(false);
                })
                .exceptionally(throwable -> {
                    error.postValue("加载失败: " + throwable.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }

    private List<BlessingEntity> sortBlessings(List<BlessingEntity> blessingList, String sortOrder) {
        if (blessingList == null) return new ArrayList<>();

        switch (sortOrder) {
            case "热门":
                blessingList.sort((a, b) -> Integer.compare(b.getLikeCount(), a.getLikeCount()));
                break;
            case "随机":
                Collections.shuffle(blessingList);
                break;
            case "最新":
            default:
                blessingList.sort((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                });
                break;
        }

        return blessingList;
    }

    private void refreshCurrentBlessings() {
        // 重新加载当前显示的祈福列表
        List<BlessingEntity> currentList = blessings.getValue();
        if (currentList != null && !currentList.isEmpty()) {
            // 简单地重新加载所有公开祈福
            loadAllBlessings("最新");
        }
    }
}
