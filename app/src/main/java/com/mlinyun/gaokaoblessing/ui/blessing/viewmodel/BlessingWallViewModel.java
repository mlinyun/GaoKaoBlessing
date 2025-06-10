package com.mlinyun.gaokaoblessing.ui.blessing.viewmodel;

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
public class BlessingWallViewModel extends AndroidViewModel {

    private final BlessingRepository blessingRepository;
    private final BlessingTemplateRepository templateRepository;

    // LiveData
    private final MutableLiveData<List<BlessingEntity>> blessings = new MutableLiveData<>();
    private final MutableLiveData<List<String>> categories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public BlessingWallViewModel(@NonNull Application application) {
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
                    List<BlessingEntity> currentList = blessings.getValue();
                    if (currentList != null) {
                        for (BlessingEntity blessing : currentList) {
                            if (blessing.getId() == blessingId) {
                                blessing.setLikeCount(blessing.getLikeCount() + 1);
                                break;
                            }
                        }
                        blessings.postValue(currentList);
                    }
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
        templateRepository.getTemplateCategories()
                .thenAccept(categoryList -> {
                    categories.postValue(categoryList);
                })
                .exceptionally(throwable -> {
                    // 使用默认分类
                    List<String> defaultCategories = new ArrayList<>();
                    defaultCategories.add("学业");
                    defaultCategories.add("健康");
                    defaultCategories.add("平安");
                    defaultCategories.add("成功");
                    defaultCategories.add("鼓励");
                    categories.postValue(defaultCategories);
                    return null;
                });
    }

    /**
     * 加载所有祈福
     */
    private void loadAllBlessings(String sortOrder) {
        if ("热门".equals(sortOrder)) {
            blessingRepository.getPopularBlessings(50)
                    .thenAccept(blessingList -> {
                        blessings.postValue(blessingList);
                        loading.postValue(false);
                    })
                    .exceptionally(this::handleError);
        } else {
            blessingRepository.getPublicBlessings(50, 0)
                    .thenAccept(blessingList -> {
                        if ("随机".equals(sortOrder)) {
                            Collections.shuffle(blessingList);
                        }
                        blessings.postValue(blessingList);
                        loading.postValue(false);
                    })
                    .exceptionally(this::handleError);
        }
    }

    /**
     * 根据分类加载祈福
     */
    private void loadBlessingsByCategory(String category, String sortOrder) {
        blessingRepository.getBlessingsByCategory(category)
                .thenAccept(blessingList -> {
                    if ("随机".equals(sortOrder)) {
                        Collections.shuffle(blessingList);
                    } else if ("热门".equals(sortOrder)) {
                        // 按点赞数排序
                        blessingList.sort((b1, b2) -> Integer.compare(b2.getLikeCount(), b1.getLikeCount()));
                    }
                    // 默认按时间排序（已在DAO中处理）

                    blessings.postValue(blessingList);
                    loading.postValue(false);
                })
                .exceptionally(this::handleError);
    }

    /**
     * 处理错误
     */
    private Void handleError(Throwable throwable) {
        error.postValue("加载失败: " + throwable.getMessage());
        loading.postValue(false);
        return null;
    }
}
