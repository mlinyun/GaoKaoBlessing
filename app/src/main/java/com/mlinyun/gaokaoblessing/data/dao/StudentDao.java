package com.mlinyun.gaokaoblessing.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mlinyun.gaokaoblessing.data.entity.Student;

import java.util.List;

/**
 * 学生数据访问对象
 */
@Dao
public interface StudentDao {

    /**
     * 插入学生
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertStudent(Student student);

    /**
     * 更新学生
     */
    @Update
    int updateStudent(Student student);

    /**
     * 删除学生
     */
    @Delete
    int deleteStudent(Student student);

    /**
     * 根据学生ID查询学生
     */
    @Query("SELECT * FROM student WHERE student_id = :studentId LIMIT 1")
    LiveData<Student> getStudentByStudentId(String studentId);

    /**
     * 根据用户ID获取学生列表
     */
    @Query("SELECT * FROM student WHERE owner_user_id = :ownerUserId ORDER BY created_at DESC")
    LiveData<List<Student>> getStudentsByOwner(String ownerUserId);

    /**
     * 获取所有学生
     */
    @Query("SELECT * FROM student ORDER BY created_at DESC")
    LiveData<List<Student>> getAllStudents();

    /**
     * 根据学校查询学生
     */
    @Query("SELECT * FROM student WHERE school LIKE '%' || :school || '%' ORDER BY created_at DESC")
    LiveData<List<Student>> getStudentsBySchool(String school);

    /**
     * 根据班级查询学生
     */
    @Query("SELECT * FROM student WHERE owner_user_id = :userId AND class_name = :className ORDER BY created_at DESC")
    LiveData<List<Student>> getStudentsByClass(String userId, String className);

    /**
     * 根据科目类型查询学生
     */
    @Query("SELECT * FROM student WHERE owner_user_id = :userId AND subject_type = :subjectType ORDER BY created_at DESC")
    LiveData<List<Student>> getStudentsBySubjectType(String userId, String subjectType);

    /**
     * 更新学生祝福数量
     */
    @Query("UPDATE student SET blessing_count = :count, updated_at = :updatedAt WHERE student_id = :studentId")
    int updateStudentBlessingCount(String studentId, int count, long updatedAt);

    /**
     * 增加学生祝福数量
     */
    @Query("UPDATE student SET blessing_count = blessing_count + 1, updated_at = :updatedAt WHERE student_id = :studentId")
    int increaseBlessingCount(String studentId, long updatedAt);

    /**
     * 搜索学生（按姓名）
     */
    @Query("SELECT * FROM student WHERE owner_user_id = :userId AND name LIKE '%' || :name || '%' ORDER BY created_at DESC")
    LiveData<List<Student>> searchStudentsByName(String userId, String name);

    /**
     * 获取关注的学生
     */
    @Query("SELECT * FROM student WHERE owner_user_id = :userId AND is_followed = 1 ORDER BY created_at DESC")
    LiveData<List<Student>> getFollowedStudents(String userId);

    /**
     * 更新关注状态
     */
    @Query("UPDATE student SET is_followed = :isFollowed, updated_at = :updateTime WHERE id = :studentId")
    void updateFollowStatus(int studentId, boolean isFollowed, long updateTime);

    /**
     * 获取所有学校列表
     */
    @Query("SELECT DISTINCT school FROM student WHERE owner_user_id = :userId AND school IS NOT NULL ORDER BY school")
    LiveData<List<String>> getAllSchools(String userId);

    /**
     * 获取所有班级列表
     */
    @Query("SELECT DISTINCT class_name FROM student WHERE owner_user_id = :userId AND class_name IS NOT NULL ORDER BY class_name")
    LiveData<List<String>> getAllClasses(String userId);

    /**
     * 获取学生总数
     */
    @Query("SELECT COUNT(*) FROM student WHERE owner_user_id = :userId")
    LiveData<Integer> getStudentCount(String userId);

    /**
     * 复合搜索
     */
    @Query("SELECT * FROM student WHERE owner_user_id = :userId " +
            "AND (:name IS NULL OR name LIKE '%' || :name || '%') " +
            "AND (:school IS NULL OR school = :school) " +
            "AND (:className IS NULL OR class_name = :className) " +
            "AND (:subjectType IS NULL OR subject_type = :subjectType) " +
            "ORDER BY created_at DESC")
    LiveData<List<Student>> searchStudentsWithFilters(String userId, String name, String school, String className, String subjectType);

    /**
     * 获取热门学生（按祝福数量排序）
     */
    @Query("SELECT * FROM student ORDER BY blessing_count DESC LIMIT :limit")
    LiveData<List<Student>> getPopularStudents(int limit);

    /**
     * 根据目标大学查询学生
     */
    @Query("SELECT * FROM student WHERE target_university LIKE '%' || :university || '%' ORDER BY created_at DESC")
    LiveData<List<Student>> getStudentsByTargetUniversity(String university);

    /**
     * 清空所有学生数据
     */
    @Query("DELETE FROM student")
    int deleteAllStudents();
}
