package com.mlinyun.gaokaoblessing.ui.student;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.mlinyun.gaokaoblessing.data.database.AppDatabase;
import com.mlinyun.gaokaoblessing.data.entity.Student;
import com.mlinyun.gaokaoblessing.data.repository.StudentRepository;
import com.mlinyun.gaokaoblessing.manager.UserSessionManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 学生管理ViewModel - 修复版本
 * 解决MediatorLiveData重复添加源的问题
 */
public class StudentViewModel extends AndroidViewModel {

    private StudentRepository studentRepository;
    private UserSessionManager userSessionManager;

    // 搜索和筛选相关的LiveData
    private MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private MutableLiveData<String> filterSchool = new MutableLiveData<>();
    private MutableLiveData<String> filterClass = new MutableLiveData<>();
    private MutableLiveData<String> filterSubjectType = new MutableLiveData<>();
    private MutableLiveData<Boolean> showFollowedOnly = new MutableLiveData<>(false);

    // 学生数据相关的LiveData
    private MediatorLiveData<List<Student>> studentsLiveData = new MediatorLiveData<>();
    private LiveData<List<Student>> allStudentsLiveData;
    private LiveData<Integer> studentCountLiveData;
    private LiveData<List<String>> schoolsLiveData;
    private LiveData<List<String>> classesLiveData;

    // 源管理
    private LiveData<List<Student>> currentDynamicSource;
    private Set<LiveData<?>> addedSources = new HashSet<>();

    // 控制标志
    private boolean isInitialized = false;

    // UI状态相关的LiveData
    private MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> showAddDialog = new MutableLiveData<>();
    private MutableLiveData<Student> editingStudent = new MutableLiveData<>();

    public StudentViewModel(Application application) {
        super(application);
        this.userSessionManager = UserSessionManager.getInstance(application);
        AppDatabase database = AppDatabase.getInstance(application);
        this.studentRepository = new StudentRepository(database.studentDao());
        initializeLiveData();
    }

    public void setStudentRepository(StudentRepository repository) {
        this.studentRepository = repository;
        resetAndReinitialize();
    }

    private void resetAndReinitialize() {
        clearAllSources();
        isInitialized = false;
        initializeLiveData();
    }

    private void initializeLiveData() {
        if (studentRepository == null || userSessionManager == null || isInitialized) {
            return;
        }

        String userId = getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            return;
        }

        isInitialized = true;

        // 初始化基础数据源        allStudentsLiveData = studentRepository.getAllStudents(userId);
        studentCountLiveData = studentRepository.getStudentCount(userId);
        schoolsLiveData = studentRepository.getAllSchools(userId);
        classesLiveData = studentRepository.getAllClasses(userId);

        // 安全地添加源
        safeAddSource(allStudentsLiveData, () -> applyCurrentFilter());
        safeAddSource(searchQuery, () -> applyCurrentFilter());
        safeAddSource(showFollowedOnly, () -> applyCurrentFilter());
    }

    private void safeAddSource(LiveData<?> source, Runnable observer) {
        if (source != null && !addedSources.contains(source)) {
            // 特殊处理allStudentsLiveData，确保数据能正确传递
            if (source == allStudentsLiveData) {
                studentsLiveData.addSource(allStudentsLiveData, students -> {
                    // 当基础学生数据改变时，重新应用过滤器
                    observer.run();
                    // 如果当前没有使用动态源，直接设置数据
                    if (currentDynamicSource == null) {
                        studentsLiveData.setValue(students);
                    }
                });
                addedSources.add(source);
            } else {
                // 对其他源（如搜索查询、筛选条件）的处理
                studentsLiveData.addSource(source, obj -> observer.run());
                addedSources.add(source);
            }
        }
    }

    private void applyCurrentFilter() {
        if (!isInitialized || studentRepository == null) {
            return;
        }

        String userId = getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            return;
        }

        String query = searchQuery.getValue();
        String school = filterSchool.getValue();
        String className = filterClass.getValue();
        String subjectType = filterSubjectType.getValue();
        Boolean followedOnly = showFollowedOnly.getValue();

        LiveData<List<Student>> targetSource = determineTargetSource(
                userId, query, school, className, subjectType, followedOnly
        );

        updateDynamicSource(targetSource);
    }

    private LiveData<List<Student>> determineTargetSource(String userId, String query,
                                                          String school, String className, String subjectType, Boolean followedOnly) {

        if (followedOnly != null && followedOnly) {
            return studentRepository.getFollowedStudents(userId);
        } else if (query != null && !query.trim().isEmpty()) {
            return studentRepository.searchStudents(userId, query.trim());
        } else if (school != null || className != null || subjectType != null) {
            return studentRepository.searchWithFilters(userId, null, school, className, subjectType);
        } else {
            return allStudentsLiveData;
        }
    }

    private void updateDynamicSource(LiveData<List<Student>> newSource) {
        if (newSource == currentDynamicSource) {
            return; // 源没有变化，无需更新
        }

        // 移除旧的动态源
        if (currentDynamicSource != null &&
                currentDynamicSource != allStudentsLiveData &&
                addedSources.contains(currentDynamicSource)) {
            studentsLiveData.removeSource(currentDynamicSource);
            addedSources.remove(currentDynamicSource);
        }

        // 添加新的动态源（如果它不是基础源）
        if (newSource != allStudentsLiveData) {
            currentDynamicSource = newSource;
            // 直接添加新源而不检查是否已存在，因为我们已经移除了旧源
            if (currentDynamicSource != null && !addedSources.contains(currentDynamicSource)) {
                studentsLiveData.addSource(currentDynamicSource, students -> {
                    if (students != null) {
                        studentsLiveData.setValue(students);
                    }
                });
                addedSources.add(currentDynamicSource);
            }
        } else {
            currentDynamicSource = null;
            // 对于基础源，确保MediatorLiveData能正确响应变化
            // 因为allStudentsLiveData已经在initializeLiveData中添加了源
            // 这里不需要做任何额外的操作，MediatorLiveData会自动响应
        }
    }

    private void clearAllSources() {
        for (LiveData<?> source : addedSources) {
            studentsLiveData.removeSource(source);
        }
        addedSources.clear();
        currentDynamicSource = null;
    }

    // ========== 业务方法 ==========

    public void addStudent(String name, String school, String className, String subjectType,
                           int graduationYear, String avatarUrl) {
        if (!validateStudentInput(name, school)) {
            return;
        }

        String userId = getCurrentUserId();
        Student student = new Student(name, school, className, subjectType, graduationYear, userId);

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            student.setAvatarUrl(avatarUrl);
        }

        studentRepository.insertStudent(student, new StudentRepository.OnResultCallback<Long>() {
            @Override
            public void onSuccess(Long result) {
                toastMessage.postValue("添加考生成功");
            }

            @Override
            public void onError(Exception error) {
                toastMessage.postValue("添加考生失败: " + error.getMessage());
            }
        });
    }

    public void updateStudent(Student student) {
        if (!validateStudentInput(student.getName(), student.getSchool())) {
            return;
        }

        studentRepository.updateStudent(student, new StudentRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                toastMessage.postValue("更新考生信息成功");
            }

            @Override
            public void onError(Exception error) {
                toastMessage.postValue("更新考生信息失败: " + error.getMessage());
            }
        });
    }

    public void deleteStudent(Student student) {
        studentRepository.deleteStudent(student, new StudentRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                toastMessage.postValue("删除考生成功");
            }

            @Override
            public void onError(Exception error) {
                toastMessage.postValue("删除考生失败: " + error.getMessage());
            }
        });
    }

    public void toggleFollowStatus(Student student) {
        boolean newStatus = !student.isFollowed();
        studentRepository.toggleFollowStatus(student.getId(), newStatus, new StudentRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                student.setFollowed(newStatus);
                String message = newStatus ? "已关注" : "已取消关注";
                toastMessage.postValue(message);
            }

            @Override
            public void onError(Exception error) {
                toastMessage.postValue("操作失败: " + error.getMessage());
            }
        });
    }

    public void searchStudents(String query) {
        searchQuery.setValue(query);
    }

    public void showFollowedStudents(boolean showOnly) {
        showFollowedOnly.setValue(showOnly);
    }

    public void setFilters(String school, String className, String subjectType) {
        filterSchool.setValue(school);
        filterClass.setValue(className);
        filterSubjectType.setValue(subjectType);
        applyCurrentFilter();
    }

    public void showAddStudentDialog() {
        showAddDialog.setValue(true);
    }

    public void editStudent(Student student) {
        editingStudent.setValue(student);
    }

    private boolean validateStudentInput(String name, String school) {
        if (name == null || name.trim().isEmpty()) {
            toastMessage.setValue("请输入姓名");
            return false;
        }

        if (name.trim().length() < 2 || name.trim().length() > 10) {
            toastMessage.setValue("姓名长度应在2-10个字符之间");
            return false;
        }

        if (school == null || school.trim().isEmpty()) {
            toastMessage.setValue("请输入学校");
            return false;
        }

        return true;
    }

    private String getCurrentUserId() {
        if (userSessionManager == null || !userSessionManager.isLoggedIn()) {
            return null;
        }

        long userId = userSessionManager.getCurrentUserId();
        return userId > 0 ? String.valueOf(userId) : null;
    }

    // ========== Getter方法 ==========

    public LiveData<List<Student>> getStudents() {
        return studentsLiveData;
    }

    public LiveData<Integer> getStudentCount() {
        return studentCountLiveData;
    }

    public LiveData<List<String>> getSchools() {
        return schoolsLiveData;
    }

    public LiveData<List<String>> getClasses() {
        return classesLiveData;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<Boolean> getShowAddDialog() {
        return showAddDialog;
    }

    public LiveData<Student> getEditingStudent() {
        return editingStudent;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearAllSources();
        isInitialized = false;
    }
}
