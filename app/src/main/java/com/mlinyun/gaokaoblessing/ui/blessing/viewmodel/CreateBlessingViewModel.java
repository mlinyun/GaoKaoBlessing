package com.mlinyun.gaokaoblessing.ui.blessing.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mlinyun.gaokaoblessing.data.database.AppDatabase;
import com.mlinyun.gaokaoblessing.data.entity.BlessingEntity;
import com.mlinyun.gaokaoblessing.data.entity.BlessingTemplateEntity;
import com.mlinyun.gaokaoblessing.data.entity.Student;
import com.mlinyun.gaokaoblessing.data.repository.BlessingRepository;
import com.mlinyun.gaokaoblessing.data.repository.BlessingTemplateRepository;
import com.mlinyun.gaokaoblessing.data.repository.StudentRepository;
import com.mlinyun.gaokaoblessing.data.repository.impl.BlessingRepositoryImpl;
import com.mlinyun.gaokaoblessing.data.repository.impl.BlessingTemplateRepositoryImpl;
import com.mlinyun.gaokaoblessing.manager.UserSessionManager;

import java.util.Date;
import java.util.List;

/**
 * 创建祈福ViewModel
 */
public class CreateBlessingViewModel extends AndroidViewModel {

    private final BlessingRepository blessingRepository;
    private final BlessingTemplateRepository templateRepository;
    private final StudentRepository studentRepository;
    private final UserSessionManager userSessionManager;

    // LiveData
    private final MutableLiveData<List<Student>> students = new MutableLiveData<>();
    private final MutableLiveData<List<BlessingTemplateEntity>> templates = new MutableLiveData<>();
    private final MutableLiveData<Boolean> createResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public CreateBlessingViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(application);
        this.blessingRepository = new BlessingRepositoryImpl(database.blessingDao());
        this.templateRepository = new BlessingTemplateRepositoryImpl(database.blessingTemplateDao());
        this.studentRepository = new StudentRepository(database.studentDao());
        this.userSessionManager = UserSessionManager.getInstance(application);

        // 初始化默认模板
        initializeDefaultTemplates();
    }

    // Getters for LiveData
    public LiveData<List<Student>> getStudents() {
        return students;
    }

    public LiveData<List<BlessingTemplateEntity>> getTemplates() {
        return templates;
    }

    public LiveData<Boolean> getCreateResult() {
        return createResult;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    /**
     * 加载学生列表
     */
    public void loadStudents() {
        loading.setValue(true);

        long currentUserId = userSessionManager.getCurrentUserId();

        // 观察学生数据变化
        studentRepository.getAllStudents(String.valueOf(currentUserId))
                .observeForever(studentList -> {
                    students.postValue(studentList);
                    loading.postValue(false);
                });
    }

    /**
     * 加载默认祈福模板
     */
    public void loadDefaultTemplates() {
        loading.setValue(true);

        templateRepository.getDefaultTemplates(20)
                .thenAccept(templateList -> {
                    templates.postValue(templateList);
                    loading.postValue(false);
                })
                .exceptionally(throwable -> {
                    error.postValue("加载模板失败: " + throwable.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }

    /**
     * 根据分类加载祈福模板
     */
    public void loadTemplatesByCategory(String category) {
        loading.setValue(true);

        templateRepository.getTemplatesByCategory(category)
                .thenAccept(templateList -> {
                    templates.postValue(templateList);
                    loading.postValue(false);
                })
                .exceptionally(throwable -> {
                    error.postValue("加载模板失败: " + throwable.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }

    /**
     * 创建祈福记录
     */
    public void createBlessing(Student student, String content, String effectType) {
        if (student == null || content == null || content.trim().isEmpty()) {
            error.setValue("祈福信息不完整");
            return;
        }

        loading.setValue(true);

        // 创建祈福实体
        BlessingEntity blessing = new BlessingEntity();
        blessing.setStudentId(student.getId());
        blessing.setStudentName(student.getName());
        blessing.setContent(content.trim());
        blessing.setBlessingType("完整祈福");
        blessing.setEffectType(effectType != null ? effectType : "默认");
        blessing.setAuthorId(userSessionManager.getCurrentUserId());
        blessing.setAuthorName(userSessionManager.getCurrentUsername());
        blessing.setCreatedAt(new Date());
        blessing.setUpdatedAt(new Date());
        blessing.setPublic(true);

        // 根据内容推断分类
        String category = inferCategory(content);
        blessing.setTemplateCategory(category);

        blessingRepository.createBlessing(blessing)
                .thenAccept(blessingId -> {
                    if (blessingId > 0) {
                        createResult.postValue(true);
                    } else {
                        error.postValue("创建祈福失败");
                    }
                    loading.postValue(false);
                })
                .exceptionally(throwable -> {
                    error.postValue("创建祈福失败: " + throwable.getMessage());
                    loading.postValue(false);
                    return null;
                });
    }

    /**
     * 根据内容推断祈福分类
     */
    private String inferCategory(String content) {
        if (content.contains("学业") || content.contains("成绩") || content.contains("考试") ||
                content.contains("金榜题名") || content.contains("学习")) {
            return "学业";
        } else if (content.contains("健康") || content.contains("身体") || content.contains("精神") ||
                content.contains("平安") || content.contains("安康")) {
            return "健康";
        } else if (content.contains("平安") || content.contains("顺利") || content.contains("安全")) {
            return "平安";
        } else if (content.contains("成功") || content.contains("梦想") || content.contains("理想") ||
                content.contains("前程")) {
            return "成功";
        } else if (content.contains("加油") || content.contains("坚持") || content.contains("相信") ||
                content.contains("努力")) {
            return "鼓励";
        } else {
            return "其他";
        }
    }

    /**
     * 初始化默认模板
     */
    private void initializeDefaultTemplates() {
        templateRepository.getAllTemplates()
                .thenAccept(templateList -> {
                    if (templateList == null || templateList.isEmpty()) {
                        // 如果没有模板，初始化默认模板
                        templateRepository.initializeDefaultTemplates();
                    }
                })
                .exceptionally(throwable -> {
                    // 忽略错误，继续初始化
                    templateRepository.initializeDefaultTemplates();
                    return null;
                });
    }
}
