package com.mlinyun.gaokaoblessing.utils;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Room数据库日期类型转换器
 */
public class DateConverter {

    /**
     * 将时间戳转换为Date对象
     */
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    /**
     * 将Date对象转换为时间戳
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
