package com.mlinyun.gaokaoblessing.base;

import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository基类
 * 提供数据层基础功能和线程管理
 */
public abstract class BaseRepository {

    // 线程池，用于执行后台任务
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * 在后台线程执行任务
     */
    protected void executeInBackground(Runnable task) {
        executor.execute(task);
    }

    /**
     * 创建Resource包装类，用于封装网络请求状态
     */
    public static class Resource<T> {
        public enum Status {
            SUCCESS, ERROR, LOADING
        }

        private final Status status;
        private final T data;
        private final String message;

        private Resource(Status status, T data, String message) {
            this.status = status;
            this.data = data;
            this.message = message;
        }

        public static <T> Resource<T> success(T data) {
            return new Resource<>(Status.SUCCESS, data, null);
        }

        public static <T> Resource<T> error(String message, T data) {
            return new Resource<>(Status.ERROR, data, message);
        }

        public static <T> Resource<T> loading(T data) {
            return new Resource<>(Status.LOADING, data, null);
        }

        public Status getStatus() {
            return status;
        }

        public T getData() {
            return data;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * 创建LiveData包装的Resource
     */
    protected <T> MutableLiveData<Resource<T>> createResourceLiveData() {
        return new MutableLiveData<>();
    }

    /**
     * 在Resource中设置加载状态
     * 使用postValue()确保线程安全
     */
    protected <T> void setLoading(MutableLiveData<Resource<T>> liveData) {
        liveData.postValue(Resource.loading(null));
    }

    /**
     * 在Resource中设置成功状态
     * 使用postValue()确保线程安全
     */
    protected <T> void setSuccess(MutableLiveData<Resource<T>> liveData, T data) {
        liveData.postValue(Resource.success(data));
    }

    /**
     * 在Resource中设置错误状态
     * 使用postValue()确保线程安全
     */
    protected <T> void setError(MutableLiveData<Resource<T>> liveData, String message) {
        liveData.postValue(Resource.error(message, null));
    }

    /**
     * 在Resource中设置错误状态
     */
    protected <T> void setError(MutableLiveData<Resource<T>> liveData, Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null || message.isEmpty()) {
            message = "未知错误";
        }
        setError(liveData, message);
    }
}
