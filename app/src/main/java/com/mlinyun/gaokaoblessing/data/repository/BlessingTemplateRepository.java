package com.mlinyun.gaokaoblessing.data.repository;

import com.mlinyun.gaokaoblessing.data.entity.BlessingTemplateEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 祈福模板数据仓库接口
 */
public interface BlessingTemplateRepository {

    /**
     * 获取所有祈福模板
     */
    CompletableFuture<List<BlessingTemplateEntity>> getAllTemplates();

    /**
     * 根据分类获取祈福模板
     */
    CompletableFuture<List<BlessingTemplateEntity>> getTemplatesByCategory(String category);

    /**
     * 获取默认祈福模板
     */
    CompletableFuture<List<BlessingTemplateEntity>> getDefaultTemplates(int limit);

    /**
     * 根据ID获取祈福模板
     */
    CompletableFuture<BlessingTemplateEntity> getTemplateById(long templateId);

    /**
     * 搜索祈福模板
     */
    CompletableFuture<List<BlessingTemplateEntity>> searchTemplates(String keyword);

    /**
     * 获取热门祈福模板
     */
    CompletableFuture<List<BlessingTemplateEntity>> getPopularTemplates(int limit);

    /**
     * 增加模板使用次数
     */
    CompletableFuture<Void> incrementUsageCount(long templateId);

    /**
     * 获取模板分类列表
     */
    CompletableFuture<List<String>> getTemplateCategories();

    /**
     * 初始化默认模板
     */
    CompletableFuture<Void> initializeDefaultTemplates();
}
