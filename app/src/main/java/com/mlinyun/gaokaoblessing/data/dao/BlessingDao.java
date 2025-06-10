package com.mlinyun.gaokaoblessing.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mlinyun.gaokaoblessing.data.entity.BlessingEntity;

import java.util.List;

/**
 * 祈福数据访问对象
 */
@Dao
public interface BlessingDao {

    /**
     * 插入祈福记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertBlessing(BlessingEntity blessing);

    /**
     * 批量插入祈福记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBlessings(List<BlessingEntity> blessings);

    /**
     * 更新祈福记录
     */
    @Update
    void updateBlessing(BlessingEntity blessing);

    /**
     * 删除祈福记录
     */
    @Delete
    void deleteBlessing(BlessingEntity blessing);

    /**
     * 根据ID删除祈福记录
     */
    @Query("DELETE FROM blessing WHERE id = :blessingId")
    void deleteBlessingById(long blessingId);

    /**
     * 获取所有祈福记录，按创建时间倒序
     */
    @Query("SELECT * FROM blessing ORDER BY created_at DESC")
    List<BlessingEntity> getAllBlessings();

    /**
     * 获取公开的祈福记录（祈福墙）
     */
    @Query("SELECT * FROM blessing WHERE is_public = 1 ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    List<BlessingEntity> getPublicBlessings(int limit, int offset);

    /**
     * 根据ID获取祈福记录
     */
    @Query("SELECT * FROM blessing WHERE id = :blessingId")
    BlessingEntity getBlessingById(long blessingId);

    /**
     * 根据学生ID获取祈福记录
     */
    @Query("SELECT * FROM blessing WHERE student_id = :studentId ORDER BY created_at DESC")
    List<BlessingEntity> getBlessingsByStudentId(long studentId);

    /**
     * 根据作者ID获取祈福记录
     */
    @Query("SELECT * FROM blessing WHERE author_id = :authorId ORDER BY created_at DESC")
    List<BlessingEntity> getBlessingsByAuthorId(long authorId);

    /**
     * 根据分类获取祈福记录
     */
    @Query("SELECT * FROM blessing WHERE template_category = :category AND is_public = 1 ORDER BY created_at DESC")
    List<BlessingEntity> getBlessingsByCategory(String category);

    /**
     * 搜索祈福记录
     */
    @Query("SELECT * FROM blessing WHERE content LIKE '%' || :keyword || '%' AND is_public = 1 ORDER BY created_at DESC")
    List<BlessingEntity> searchBlessings(String keyword);

    /**
     * 获取最近的祈福记录
     */
    @Query("SELECT * FROM blessing ORDER BY created_at DESC LIMIT :limit")
    List<BlessingEntity> getRecentBlessings(int limit);

    /**
     * 获取热门祈福记录（按点赞数排序）
     */
    @Query("SELECT * FROM blessing WHERE is_public = 1 ORDER BY like_count DESC, created_at DESC LIMIT :limit")
    List<BlessingEntity> getPopularBlessings(int limit);

    /**
     * 更新点赞数
     */
    @Query("UPDATE blessing SET like_count = like_count + :increment WHERE id = :blessingId")
    void updateLikeCount(long blessingId, int increment);

    /**
     * 更新评论数
     */
    @Query("UPDATE blessing SET comment_count = comment_count + :increment WHERE id = :blessingId")
    void updateCommentCount(long blessingId, int increment);

    /**
     * 获取祈福统计数据
     */
    @Query("SELECT COUNT(*) FROM blessing WHERE author_id = :authorId")
    int getBlessingCountByAuthor(long authorId);

    /**
     * 获取学生的祈福数量
     */
    @Query("SELECT COUNT(*) FROM blessing WHERE student_id = :studentId")
    int getBlessingCountByStudent(long studentId);

    /**
     * 清空所有祈福记录
     */
    @Query("DELETE FROM blessing")
    void deleteAllBlessings();
}
