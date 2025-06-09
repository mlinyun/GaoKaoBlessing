package com.mlinyun.gaokaoblessing.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Android 15权限管理工具类
 * 适配不同Android版本的权限要求
 */
public class PermissionUtils {

    /**
     * 获取读取图片所需的权限（根据Android版本）
     */
    public static String[] getImagePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+ (API 34+)
            return new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33)
            return new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else { // Android 12及以下
            return new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }
    }

    /**
     * 获取相机权限
     */
    public static String[] getCameraPermissions() {
        return new String[]{
                Manifest.permission.CAMERA
        };
    }

    /**
     * 检查是否有读取图片的权限
     */
    public static boolean hasImagePermissions(Context context) {
        String[] permissions = getImagePermissions();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查是否有相机权限
     */
    public static boolean hasCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取缺失的图片权限
     */
    public static String[] getMissingImagePermissions(Context context) {
        String[] permissions = getImagePermissions();
        List<String> missingPermissions = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        return missingPermissions.toArray(new String[0]);
    }

    /**
     * 获取缺失的相机权限
     */
    public static String[] getMissingCameraPermissions(Context context) {
        List<String> missingPermissions = new ArrayList<>();

        if (!hasCameraPermission(context)) {
            missingPermissions.add(Manifest.permission.CAMERA);
        }

        return missingPermissions.toArray(new String[0]);
    }

    /**
     * 获取用户友好的权限说明
     */
    public static String getPermissionRationale(String permission) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                return "需要相机权限来拍摄头像照片";
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return "需要存储权限来访问相册中的照片";
            case Manifest.permission.READ_MEDIA_IMAGES:
                return "需要照片权限来选择头像";
            case Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED:
                return "需要精选照片权限来访问您选择的照片";
            default:
                return "此功能需要相应权限才能正常工作";
        }
    }

    /**
     * 检查是否为Android 14+
     */
    public static boolean isAndroid14Plus() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE;
    }

    /**
     * 检查是否为Android 13+
     */
    public static boolean isAndroid13Plus() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }
}
