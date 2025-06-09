package com.mlinyun.gaokaoblessing.ui.blessing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mlinyun.gaokaoblessing.base.BaseViewModel;
import com.mlinyun.gaokaoblessing.ui.blessing.model.BlessingRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 祈福页面ViewModel - 按照UI原型图功能实现
 */
public class BlessingViewModel extends BaseViewModel {

    // 最近祈福记录
    private MutableLiveData<List<BlessingRecord>> _recentBlessings = new MutableLiveData<>();
    public LiveData<List<BlessingRecord>> recentBlessings = _recentBlessings;

    // 学生数量
    private MutableLiveData<Integer> _studentCount = new MutableLiveData<>();
    public LiveData<Integer> studentCount = _studentCount;

    // 祈福统计数据
    private MutableLiveData<Integer> _totalBlessings = new MutableLiveData<>();
    public LiveData<Integer> totalBlessings = _totalBlessings;

    /**
     * 加载祈福数据
     */
    public void loadBlessingData() {
        showLoading();
        // TODO: 从本地存储或网络加载祈福数据
        // 模拟数据
        List<BlessingRecord> mockBlessings = createMockBlessings();
        _recentBlessings.setValue(mockBlessings);
        _totalBlessings.setValue(mockBlessings.size());
        hideLoading();
    }

    /**
     * 加载学生数据
     */
    public void loadStudentData() {
        // TODO: 从本地存储加载学生数据
        // 模拟数据
        _studentCount.setValue(3);
    }

    /**
     * 创建祝福
     */
    public void createBlessing(String studentName, String content, String type) {
        showLoading();

        BlessingRecord newBlessing = new BlessingRecord(studentName, content, type);

        // 添加到现有列表
        List<BlessingRecord> currentList = _recentBlessings.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        currentList.add(0, newBlessing); // 添加到列表顶部
        _recentBlessings.setValue(currentList);
        _totalBlessings.setValue(currentList.size());

        hideLoading();
        showSuccess("祝福创建成功");
    }

    /**
     * 快速祈福
     */
    public void quickBlessing(String studentName, String predefinedContent) {
        createBlessing(studentName, predefinedContent, "快速祈福");
    }

    /**
     * 发送祝福
     */
    public void sendBlessing(String studentId, String blessingContent) {
        showLoading();
        // TODO: 实现发送祝福逻辑
        hideLoading();
        showSuccess("祝福发送成功");
    }

    /**
     * 获取祈福统计数据
     */
    public void refreshStats() {
        List<BlessingRecord> blessings = _recentBlessings.getValue();
        if (blessings != null) {
            _totalBlessings.setValue(blessings.size());
        }
    }

    /**
     * 创建模拟祈福数据
     */
    private List<BlessingRecord> createMockBlessings() {
        List<BlessingRecord> mockBlessings = new ArrayList<>();

        mockBlessings.add(new BlessingRecord("张小明", "愿你考试顺利，金榜题名！🎓", "学业"));
        mockBlessings.add(new BlessingRecord("李小红", "保持健康，发挥最佳水平！💪", "健康"));
        mockBlessings.add(new BlessingRecord("王小强", "平安应考，心想事成！🙏", "平安"));

        return mockBlessings;
    }
}
