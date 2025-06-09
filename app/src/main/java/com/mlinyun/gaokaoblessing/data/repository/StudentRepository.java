package com.mlinyun.gaokaoblessing.data.repository;

import androidx.lifecycle.LiveData;

import com.mlinyun.gaokaoblessing.data.dao.StudentDao;
import com.mlinyun.gaokaoblessing.data.entity.Student;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 学生数据仓库
 */
public class StudentRepository {

    private final StudentDao studentDao;
    private final Executor executor;

    public StudentRepository(StudentDao studentDao) {
        this.studentDao = studentDao;
        this.executor = Executors.newFixedThreadPool(4);
    }

    /**
     * 添加学生
     */
    public void insertStudent(Student student, OnResultCallback<Long> callback) {
        executor.execute(() -> {
            try {
                // 确保时间戳设置正确
                student.setCreatedAt(System.currentTimeMillis());
                student.setUpdatedAt(System.currentTimeMillis());

                // 确保学生ID已生成
                if (student.getStudentId() == null || student.getStudentId().isEmpty()) {
                    String studentId = "STU" + System.currentTimeMillis();
                    student.setStudentId(studentId);
                }

                long id = studentDao.insertStudent(student);

                // 设置生成的ID
                student.setId((int) id);

                if (callback != null) {
                    callback.onSuccess(id);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * 更新学生
     */
    public void updateStudent(Student student, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                student.setUpdatedAt(System.currentTimeMillis());
                studentDao.updateStudent(student);
                if (callback != null) {
                    callback.onSuccess(null);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * 删除学生
     */
    public void deleteStudent(Student student, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                studentDao.deleteStudent(student);
                if (callback != null) {
                    callback.onSuccess(null);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * 获取用户的所有学生
     */
    public LiveData<List<Student>> getAllStudents(String userId) {
        return studentDao.getStudentsByOwner(userId);
    }

    /**
     * 搜索学生
     */
    public LiveData<List<Student>> searchStudents(String userId, String name) {
        return studentDao.searchStudentsByName(userId, name);
    }

    /**
     * 按学校筛选
     */
    public LiveData<List<Student>> getStudentsBySchool(String userId, String school) {
        return studentDao.getStudentsBySchool(school);
    }

    /**
     * 按班级筛选
     */
    public LiveData<List<Student>> getStudentsByClass(String userId, String className) {
        return studentDao.getStudentsByClass(userId, className);
    }

    /**
     * 按科目类型筛选
     */
    public LiveData<List<Student>> getStudentsBySubjectType(String userId, String subjectType) {
        return studentDao.getStudentsBySubjectType(userId, subjectType);
    }

    /**
     * 获取关注的学生
     */
    public LiveData<List<Student>> getFollowedStudents(String userId) {
        return studentDao.getFollowedStudents(userId);
    }

    /**
     * 复合搜索
     */
    public LiveData<List<Student>> searchWithFilters(String userId, String name, String school, String className, String subjectType) {
        return studentDao.searchStudentsWithFilters(userId, name, school, className, subjectType);
    }

    /**
     * 切换关注状态
     */
    public void toggleFollowStatus(int studentId, boolean isFollowed, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                studentDao.updateFollowStatus(studentId, isFollowed, System.currentTimeMillis());
                if (callback != null) {
                    callback.onSuccess(null);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * 增加祝福次数
     */
    public void increaseBlessingCount(String studentId, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                studentDao.increaseBlessingCount(studentId, System.currentTimeMillis());
                if (callback != null) {
                    callback.onSuccess(null);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * 获取所有学校
     */
    public LiveData<List<String>> getAllSchools(String userId) {
        return studentDao.getAllSchools(userId);
    }

    /**
     * 获取所有班级
     */
    public LiveData<List<String>> getAllClasses(String userId) {
        return studentDao.getAllClasses(userId);
    }

    /**
     * 获取学生总数
     */
    public LiveData<Integer> getStudentCount(String userId) {
        return studentDao.getStudentCount(userId);
    }

    /**
     * 回调接口
     */
    public interface OnResultCallback<T> {
        void onSuccess(T result);

        void onError(Exception error);
    }
}
