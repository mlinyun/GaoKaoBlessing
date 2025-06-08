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
     * 根据年级查询学生
     */
    @Query("SELECT * FROM student WHERE grade = :grade ORDER BY created_at DESC")
    LiveData<List<Student>> getStudentsByGrade(String grade);

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
    @Query("SELECT * FROM student WHERE name LIKE '%' || :name || '%' ORDER BY blessing_count DESC")
    LiveData<List<Student>> searchStudentsByName(String name);

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
