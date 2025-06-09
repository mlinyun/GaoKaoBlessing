package com.mlinyun.gaokaoblessing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 图片处理工具类
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 800;
    private static final int QUALITY = 85;

    /**
     * 从Uri获取并压缩图片，保存到应用内部存储
     */
    public static String saveImageFromUri(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return null;
            }

            // 解码图片
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bitmap == null) {
                return null;
            }

            // 压缩和旋转图片
            bitmap = compressAndRotateBitmap(context, bitmap, uri);

            // 保存到内部存储
            return saveBitmapToInternalStorage(context, bitmap);

        } catch (Exception e) {
            Log.e(TAG, "保存图片失败", e);
            return null;
        }
    }

    /**
     * 压缩和旋转图片
     */
    private static Bitmap compressAndRotateBitmap(Context context, Bitmap bitmap, Uri uri) {
        try {
            // 获取图片旋转角度
            int rotation = getImageRotation(context, uri);

            // 计算压缩比例
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            float scaleWidth = 1.0f;
            float scaleHeight = 1.0f;

            if (width > MAX_WIDTH) {
                scaleWidth = (float) MAX_WIDTH / width;
            }
            if (height > MAX_HEIGHT) {
                scaleHeight = (float) MAX_HEIGHT / height;
            }

            float scale = Math.min(scaleWidth, scaleHeight);

            // 创建变换矩阵
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            // 应用旋转
            if (rotation != 0) {
                matrix.postRotate(rotation);
            }

            // 创建新的bitmap
            Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

            // 回收原bitmap
            if (bitmap != scaledBitmap) {
                bitmap.recycle();
            }

            return scaledBitmap;

        } catch (Exception e) {
            Log.e(TAG, "压缩图片失败", e);
            return bitmap;
        }
    }

    /**
     * 获取图片旋转角度
     */
    private static int getImageRotation(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return 0;
            }

            ExifInterface exif = new ExifInterface(inputStream);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            inputStream.close();

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            Log.e(TAG, "获取图片旋转角度失败", e);
            return 0;
        }
    }

    /**
     * 保存Bitmap到内部存储
     */
    private static String saveBitmapToInternalStorage(Context context, Bitmap bitmap) {
        try {
            // 创建头像目录
            File avatarDir = new File(context.getFilesDir(), "avatars");
            if (!avatarDir.exists()) {
                avatarDir.mkdirs();
            }

            // 生成唯一文件名
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "avatar_" + timeStamp + ".jpg";
            File imageFile = new File(avatarDir, fileName);

            // 保存图片
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, fos);
            fos.close();

            // 回收bitmap
            bitmap.recycle();

            return imageFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e(TAG, "保存Bitmap失败", e);
            return null;
        }
    }

    /**
     * 删除头像文件
     */
    public static boolean deleteAvatarFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        try {
            File file = new File(filePath);
            return file.exists() && file.delete();
        } catch (Exception e) {
            Log.e(TAG, "删除头像文件失败", e);
            return false;
        }
    }

    /**
     * 检查文件是否存在
     */
    public static boolean isFileExists(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * 获取文件大小（KB）
     */
    public static long getFileSizeKB(String filePath) {
        if (!isFileExists(filePath)) {
            return 0;
        }

        File file = new File(filePath);
        return file.length() / 1024;
    }
}
