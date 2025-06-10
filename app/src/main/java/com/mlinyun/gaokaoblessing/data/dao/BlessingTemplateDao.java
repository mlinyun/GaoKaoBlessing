package com.mlinyun.gaokaoblessing.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mlinyun.gaokaoblessing.data.entity.BlessingTemplateEntity;

import java.util.List;

/**
 * 祈福模板数据访问对象
 */
@Dao
public interface BlessingTemplateDao {

    /**
     * 插入祈福模板
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTemplate(BlessingTemplateEntity template);

    /**
     * 批量插入祈福模板
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTemplates(List<BlessingTemplateEntity> templates);

    /**
     * 更新祈福模板
     */
    @Update
    void updateTemplate(BlessingTemplateEntity template);

    /**
     * 删除祈福模板
     */
    @Delete
    void deleteTemplate(BlessingTemplateEntity template);

    /**
     * 根据ID删除祈福模板
     */
    @Query("DELETE FROM blessing_template WHERE id = :templateId")
    void deleteTemplateById(long templateId);

    /**
     * 获取所有祈福模板
     */
    @Query("SELECT * FROM blessing_template ORDER BY usage_count DESC, rating DESC")
    List<BlessingTemplateEntity> getAllTemplates();

    /**
     * 根据分类获取祈福模板
     */
    @Query("SELECT * FROM blessing_template WHERE category = :category ORDER BY usage_count DESC, rating DESC")
    List<BlessingTemplateEntity> getTemplatesByCategory(String category);

    /**
     * 获取默认祈福模板
     */
    @Query("SELECT * FROM blessing_template WHERE is_default = 1 ORDER BY usage_count DESC LIMIT :limit")
    List<BlessingTemplateEntity> getDefaultTemplates(int limit);

    /**
     * 根据ID获取祈福模板
     */
    @Query("SELECT * FROM blessing_template WHERE id = :templateId")
    BlessingTemplateEntity getTemplateById(long templateId);

    /**
     * 搜索祈福模板
     */
    @Query("SELECT * FROM blessing_template WHERE content LIKE '%' || :keyword || '%' OR tags LIKE '%' || :keyword || '%' ORDER BY usage_count DESC")
    List<BlessingTemplateEntity> searchTemplates(String keyword);

    /**
     * 获取热门祈福模板
     */
    @Query("SELECT * FROM blessing_template ORDER BY usage_count DESC LIMIT :limit")
    List<BlessingTemplateEntity> getPopularTemplates(int limit);

    /**
     * 增加模板使用次数
     */
    @Query("UPDATE blessing_template SET usage_count = usage_count + 1 WHERE id = :templateId")
    void incrementUsageCount(long templateId);

    /**
     * 获取模板分类列表
     */
    @Query("SELECT DISTINCT category FROM blessing_template WHERE category IS NOT NULL AND category != ''")
    List<String> getTemplateCategories();

    /**
     * 清空所有祈福模板
     */
    @Query("DELETE FROM blessing_template")
    void deleteAllTemplates();
}
