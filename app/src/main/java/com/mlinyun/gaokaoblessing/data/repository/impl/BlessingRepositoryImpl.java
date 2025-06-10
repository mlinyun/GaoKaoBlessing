package com.mlinyun.gaokaoblessing.data.repository.impl;

import com.mlinyun.gaokaoblessing.data.dao.BlessingDao;
import com.mlinyun.gaokaoblessing.data.entity.BlessingEntity;
import com.mlinyun.gaokaoblessing.data.repository.BlessingRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 祈福数据仓库实现类
 */
public class BlessingRepositoryImpl implements BlessingRepository {

    private final BlessingDao blessingDao;
    private final Executor executor;

    public BlessingRepositoryImpl(BlessingDao blessingDao) {
        this.blessingDao = blessingDao;
        this.executor = Executors.newFixedThreadPool(4);
    }

    @Override
    public CompletableFuture<Long> createBlessing(BlessingEntity blessing) {
        return CompletableFuture.supplyAsync(() -> {
            return blessingDao.insertBlessing(blessing);
        }, executor);
    }

    @Override
    public CompletableFuture<List<BlessingEntity>> getAllBlessings() {
        return CompletableFuture.supplyAsync(() -> {
            return blessingDao.getAllBlessings();
        }, executor);
    }

    @Override
    public CompletableFuture<List<BlessingEntity>> getPublicBlessings(int limit, int offset) {
        return CompletableFuture.supplyAsync(() -> {
            return blessingDao.getPublicBlessings(limit, offset);
        }, executor);
    }

    @Override
    public CompletableFuture<List<BlessingEntity>> getBlessingsByStudentId(long studentId) {
        return CompletableFuture.supplyAsync(() -> {
            return blessingDao.getBlessingsByStudentId(studentId);
        }, executor);
    }

    @Override
    public CompletableFuture<List<BlessingEntity>> getBlessingsByAuthorId(long authorId) {
        return CompletableFuture.supplyAsync(() -> {
            return blessingDao.getBlessingsByAuthorId(authorId);
        }, executor);
    }

    @Override
    public CompletableFuture<List<BlessingEntity>> getBlessingsByCategory(String category) {
        return CompletableFuture.supplyAsync(() -> {
            return blessingDao.getBlessingsByCategory(category);
        }, executor);
    }

    @Override
    public CompletableFuture<List<BlessingEntity>> searchBlessings(String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            return blessingDao.searchBlessings(keyword);
        }, executor);
    }

    @Override
    public CompletableFuture<List<BlessingEntity>> getRecentBlessings(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            return blessingDao.getRecentBlessings(limit);
        }, executor);
    }

    @Override
    public CompletableFuture<List<BlessingEntity>> getPopularBlessings(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            return blessingDao.getPopularBlessings(limit);
        }, executor);
    }

    @Override
    public CompletableFuture<Void> updateLikeCount(long blessingId, int increment) {
        return CompletableFuture.runAsync(() -> {
            blessingDao.updateLikeCount(blessingId, increment);
        }, executor);
    }

    @Override
    public CompletableFuture<Void> updateCommentCount(long blessingId, int increment) {
        return CompletableFuture.runAsync(() -> {
            blessingDao.updateCommentCount(blessingId, increment);
        }, executor);
    }

    @Override
    public CompletableFuture<Void> deleteBlessing(long blessingId) {
        return CompletableFuture.runAsync(() -> {
            blessingDao.deleteBlessingById(blessingId);
        }, executor);
    }

    @Override
    public CompletableFuture<Integer> getBlessingCountByAuthor(long authorId) {
        return CompletableFuture.supplyAsync(() -> {
            return blessingDao.getBlessingCountByAuthor(authorId);
        }, executor);
    }

    @Override
    public CompletableFuture<Integer> getBlessingCountByStudent(long studentId) {
        return CompletableFuture.supplyAsync(() -> {
            return blessingDao.getBlessingCountByStudent(studentId);
        }, executor);
    }
}
