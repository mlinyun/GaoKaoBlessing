package com.mlinyun.gaokaoblessing.data.repository.impl;

import com.mlinyun.gaokaoblessing.data.dao.BlessingTemplateDao;
import com.mlinyun.gaokaoblessing.data.entity.BlessingTemplateEntity;
import com.mlinyun.gaokaoblessing.data.repository.BlessingTemplateRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 祈福模板数据仓库实现类
 */
public class BlessingTemplateRepositoryImpl implements BlessingTemplateRepository {

    private final BlessingTemplateDao templateDao;
    private final Executor executor;

    public BlessingTemplateRepositoryImpl(BlessingTemplateDao templateDao) {
        this.templateDao = templateDao;
        this.executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public CompletableFuture<List<BlessingTemplateEntity>> getAllTemplates() {
        return CompletableFuture.supplyAsync(() -> {
            return templateDao.getAllTemplates();
        }, executor);
    }

    @Override
    public CompletableFuture<List<BlessingTemplateEntity>> getTemplatesByCategory(String category) {
        return CompletableFuture.supplyAsync(() -> {
            return templateDao.getTemplatesByCategory(category);
        }, executor);
    }

    @Override
    public CompletableFuture<List<BlessingTemplateEntity>> getDefaultTemplates(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            return templateDao.getDefaultTemplates(limit);
        }, executor);
    }

    @Override
    public CompletableFuture<BlessingTemplateEntity> getTemplateById(long templateId) {
        return CompletableFuture.supplyAsync(() -> {
            return templateDao.getTemplateById(templateId);
        }, executor);
    }

    @Override
    public CompletableFuture<List<BlessingTemplateEntity>> searchTemplates(String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            return templateDao.searchTemplates(keyword);
        }, executor);
    }

    @Override
    public CompletableFuture<List<BlessingTemplateEntity>> getPopularTemplates(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            return templateDao.getPopularTemplates(limit);
        }, executor);
    }

    @Override
    public CompletableFuture<Void> incrementUsageCount(long templateId) {
        return CompletableFuture.runAsync(() -> {
            templateDao.incrementUsageCount(templateId);
        }, executor);
    }

    @Override
    public CompletableFuture<List<String>> getTemplateCategories() {
        return CompletableFuture.supplyAsync(() -> {
            return templateDao.getTemplateCategories();
        }, executor);
    }

    @Override
    public CompletableFuture<Void> initializeDefaultTemplates() {
        return CompletableFuture.runAsync(() -> {
            List<BlessingTemplateEntity> defaultTemplates = createDefaultTemplates();
            templateDao.insertTemplates(defaultTemplates);
        }, executor);
    }

    /**
     * 创建默认祈福模板
     */
    private List<BlessingTemplateEntity> createDefaultTemplates() {
        List<BlessingTemplateEntity> templates = new ArrayList<>();

        // 学业祈福模板
        templates.add(new BlessingTemplateEntity("学业", "愿你在高考中超常发挥，考出理想的成绩！", "高考,学业,成绩"));
        templates.add(new BlessingTemplateEntity("学业", "愿你笔下生花，答题如有神助！", "高考,智慧,答题"));
        templates.add(new BlessingTemplateEntity("学业", "愿你金榜题名，前程似锦！", "金榜题名,前程,梦想"));
        templates.add(new BlessingTemplateEntity("学业", "愿你心想事成，学业有成！", "心想事成,学业,成功"));

        // 健康祈福模板
        templates.add(new BlessingTemplateEntity("健康", "愿你身体健康，精神饱满迎接挑战！", "健康,精神,挑战"));
        templates.add(new BlessingTemplateEntity("健康", "愿你考试期间身体安康，状态最佳！", "身体,状态,考试"));
        templates.add(new BlessingTemplateEntity("健康", "愿你睡眠充足，精力充沛！", "睡眠,精力,充沛"));

        // 平安祈福模板
        templates.add(new BlessingTemplateEntity("平安", "愿你一路平安，顺利到达考场！", "平安,顺利,考场"));
        templates.add(new BlessingTemplateEntity("平安", "愿你考试平安顺利，万事如意！", "平安,顺利,万事如意"));
        templates.add(new BlessingTemplateEntity("平安", "愿你心态平和，发挥稳定！", "心态,平和,稳定"));

        // 成功祈福模板
        templates.add(new BlessingTemplateEntity("成功", "愿你梦想成真，考入理想大学！", "梦想,成真,大学"));
        templates.add(new BlessingTemplateEntity("成功", "愿你所有努力都有回报，成功在望！", "努力,回报,成功"));
        templates.add(new BlessingTemplateEntity("成功", "愿你信心满满，勇敢追梦！", "信心,勇敢,追梦"));

        // 鼓励祈福模板
        templates.add(new BlessingTemplateEntity("鼓励", "相信自己，你一定可以的！", "相信,自己,可以"));
        templates.add(new BlessingTemplateEntity("鼓励", "加油！你是最棒的！", "加油,最棒,鼓励"));
        templates.add(new BlessingTemplateEntity("鼓励", "坚持到底，胜利就在前方！", "坚持,胜利,前方"));

        return templates;
    }
}
