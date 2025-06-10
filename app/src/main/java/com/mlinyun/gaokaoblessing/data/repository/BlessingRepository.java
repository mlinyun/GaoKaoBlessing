package com.mlinyun.gaokaoblessing.data.repository;

import com.mlinyun.gaokaoblessing.data.entity.BlessingEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 祈福数据仓库接口
 */
public interface BlessingRepository {

    /**
     * 创建祈福记录
     */
    CompletableFuture<Long> createBlessing(BlessingEntity blessing);

    /**
     * 获取所有祈福记录
     */
    CompletableFuture<List<BlessingEntity>> getAllBlessings();

    /**
     * 获取公开的祈福记录（祈福墙）
     */
    CompletableFuture<List<BlessingEntity>> getPublicBlessings(int limit, int offset);

    /**
     * 根据学生ID获取祈福记录
     */
    CompletableFuture<List<BlessingEntity>> getBlessingsByStudentId(long studentId);

    /**
     * 根据作者ID获取祈福记录
     */
    CompletableFuture<List<BlessingEntity>> getBlessingsByAuthorId(long authorId);

    /**
     * 根据分类获取祈福记录
     */
    CompletableFuture<List<BlessingEntity>> getBlessingsByCategory(String category);

    /**
     * 搜索祈福记录
     */
    CompletableFuture<List<BlessingEntity>> searchBlessings(String keyword);

    /**
     * 获取最近的祈福记录
     */
    CompletableFuture<List<BlessingEntity>> getRecentBlessings(int limit);

    /**
     * 获取热门祈福记录
     */
    CompletableFuture<List<BlessingEntity>> getPopularBlessings(int limit);

    /**
     * 更新点赞数
     */
    CompletableFuture<Void> updateLikeCount(long blessingId, int increment);

    /**
     * 更新评论数
     */
    CompletableFuture<Void> updateCommentCount(long blessingId, int increment);

    /**
     * 删除祈福记录
     */
    CompletableFuture<Void> deleteBlessing(long blessingId);

    /**
     * 获取祈福统计数据
     */
    CompletableFuture<Integer> getBlessingCountByAuthor(long authorId);

    /**
     * 获取学生的祈福数量
     */
    CompletableFuture<Integer> getBlessingCountByStudent(long studentId);
}
