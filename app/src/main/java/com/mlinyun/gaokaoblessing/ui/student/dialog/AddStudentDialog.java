package com.mlinyun.gaokaoblessing.ui.student.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mlinyun.gaokaoblessing.R;
import com.mlinyun.gaokaoblessing.data.entity.Student;
import com.mlinyun.gaokaoblessing.utils.ImageUtils;
import com.mlinyun.gaokaoblessing.utils.PermissionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 添加/编辑学生对话框
 */
public class AddStudentDialog extends DialogFragment {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private static final int REQUEST_CAMERA = 102;
    private static final int REQUEST_GALLERY = 103;
    private TextInputEditText etName, etSchool, etClass;
    private TextInputLayout tilName, tilSchool, tilClass, tilGraduationYear;
    private AutoCompleteTextView actGraduationYear;
    private RadioGroup rgSubjectType;
    private ImageView ivAvatarPreview;
    private MaterialButton btnCancel, btnConfirm;

    private String selectedAvatarPath;  // 改为存储本地文件路径
    private Uri tempCameraImageUri;     // 临时相机图片URI
    private Student editingStudent;
    private OnStudentSaveListener saveListener;

    public static AddStudentDialog newInstance() {
        return new AddStudentDialog();
    }

    public static AddStudentDialog newInstance(Student student) {
        AddStudentDialog dialog = new AddStudentDialog();
        dialog.editingStudent = student;
        return dialog;
    }

    public void setOnStudentSaveListener(OnStudentSaveListener listener) {
        this.saveListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_student, null);
        initViews(view);
        setupViews();

        if (editingStudent != null) {
            populateEditData();
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_rounded);
        return dialog;
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.et_name);
        etSchool = view.findViewById(R.id.et_school);
        etClass = view.findViewById(R.id.et_class);
        tilName = view.findViewById(R.id.til_name);
        tilSchool = view.findViewById(R.id.til_school);
        tilClass = view.findViewById(R.id.til_class);
        tilGraduationYear = view.findViewById(R.id.til_graduation_year);
        actGraduationYear = view.findViewById(R.id.act_graduation_year);
        rgSubjectType = view.findViewById(R.id.rg_subject_type);
        ivAvatarPreview = view.findViewById(R.id.iv_avatar_preview);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnConfirm = view.findViewById(R.id.btn_confirm);
    }

    private void setupViews() {
        // 设置高考年份选项
        setupGraduationYearDropdown();

        // 设置头像点击事件
        ivAvatarPreview.setOnClickListener(v -> showAvatarPickerDialog());

        // 设置输入验证
        setupInputValidation();

        // 设置按钮点击事件
        btnCancel.setOnClickListener(v -> dismiss());
        btnConfirm.setOnClickListener(v -> saveStudent());

        // 更新标题
        if (editingStudent != null) {
            // 如果有标题TextView，可以设置为"编辑考生"
        }
    }

    private void setupGraduationYearDropdown() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[10]; // 提供10年的选择

        for (int i = 0; i < 10; i++) {
            years[i] = String.valueOf(currentYear + i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, years);
        actGraduationYear.setAdapter(adapter);
        actGraduationYear.setText(String.valueOf(currentYear + 1)); // 默认选择明年
    }

    private void setupInputValidation() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etSchool.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateSchool(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void populateEditData() {
        if (editingStudent == null) return;

        etName.setText(editingStudent.getName());
        etSchool.setText(editingStudent.getSchool());
        etClass.setText(editingStudent.getClassName());
        actGraduationYear.setText(String.valueOf(editingStudent.getGraduationYear()));

        // 设置科目类型
        if ("文科".equals(editingStudent.getSubjectType())) {
            rgSubjectType.check(R.id.rb_liberal_arts);
        } else {
            rgSubjectType.check(R.id.rb_science);
        }
        // 加载头像
        if (editingStudent.getAvatarUrl() != null && !editingStudent.getAvatarUrl().isEmpty()) {
            selectedAvatarPath = editingStudent.getAvatarUrl();
            String imageUri = editingStudent.getAvatarUrl().startsWith("/") ?
                    "file://" + editingStudent.getAvatarUrl() : editingStudent.getAvatarUrl();

            Glide.with(this)
                    .load(imageUri)
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.ic_student_avatar_default)
                    .into(ivAvatarPreview);
        }
    }

    private void showAvatarPickerDialog() {
        String[] options = {"拍照", "从相册选择"};
        new AlertDialog.Builder(requireContext())
                .setTitle("选择头像")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissionAndTakePhoto();
                    } else {
                        checkStoragePermissionAndPickFromGallery();
                    }
                })
                .show();
    }

    private void checkCameraPermissionAndTakePhoto() {
        String[] missingPermissions = PermissionUtils.getMissingCameraPermissions(requireContext());
        if (missingPermissions.length > 0) {
            requestPermissions(missingPermissions, REQUEST_CAMERA_PERMISSION);
        } else {
            takePhoto();
        }
    }

    private void checkStoragePermissionAndPickFromGallery() {
        String[] missingPermissions = PermissionUtils.getMissingImagePermissions(requireContext());
        if (missingPermissions.length > 0) {
            requestPermissions(missingPermissions, REQUEST_STORAGE_PERMISSION);
        } else {
            pickFromGallery();
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            // 创建临时文件用于存储拍照结果
            File tempFile = new File(requireContext().getCacheDir(), "temp_camera_image.jpg");
            tempCameraImageUri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempCameraImageUri);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    private void pickFromGallery() {
        Intent intent;

        if (PermissionUtils.isAndroid13Plus()) {
            // Android 13+ 使用新的照片选择器
            intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            intent.setType("image/*");
        } else {
            // 传统的相册选择
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
        }

        try {
            startActivityForResult(intent, REQUEST_GALLERY);
        } catch (Exception e) {
            // 如果新的选择器不可用，回退到传统方式
            Intent fallbackIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            fallbackIntent.setType("image/*");
            startActivityForResult(fallbackIntent, REQUEST_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean allGranted = true;
        List<String> deniedPermissions = new ArrayList<>();

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                deniedPermissions.add(permissions[i]);
            }
        }

        if (allGranted) {
            // 所有权限都已授予
            switch (requestCode) {
                case REQUEST_CAMERA_PERMISSION:
                    takePhoto();
                    break;
                case REQUEST_STORAGE_PERMISSION:
                    pickFromGallery();
                    break;
            }
        } else {
            // 有权限被拒绝，显示友好的提示信息
            showPermissionDeniedDialog(deniedPermissions, requestCode);
        }
    }

    private void showPermissionDeniedDialog(List<String> deniedPermissions, int requestCode) {
        StringBuilder message = new StringBuilder("为了使用头像功能，需要以下权限：\n\n");

        for (String permission : deniedPermissions) {
            message.append("• ").append(PermissionUtils.getPermissionRationale(permission)).append("\n");
        }

        message.append("\n请在系统设置中手动开启这些权限。");

        new AlertDialog.Builder(requireContext())
                .setTitle("权限说明")
                .setMessage(message.toString())
                .setPositiveButton("重试", (dialog, which) -> {
                    // 重新请求权限
                    switch (requestCode) {
                        case REQUEST_CAMERA_PERMISSION:
                            checkCameraPermissionAndTakePhoto();
                            break;
                        case REQUEST_STORAGE_PERMISSION:
                            checkStoragePermissionAndPickFromGallery();
                            break;
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    if (tempCameraImageUri != null) {
                        handleImageResult(tempCameraImageUri);
                    }
                    break;
                case REQUEST_GALLERY:
                    if (data != null && data.getData() != null) {
                        handleImageResult(data.getData());
                    }
                    break;
            }
        }
    }

    private void handleImageResult(Uri imageUri) {
        // 使用ImageUtils处理和保存图片
        String savedPath = ImageUtils.saveImageFromUri(requireContext(), imageUri);
        if (savedPath != null) {
            selectedAvatarPath = savedPath;
            // 显示预览
            Glide.with(this)
                    .load("file://" + savedPath)
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.ic_student_avatar_default)
                    .into(ivAvatarPreview);

            Toast.makeText(getContext(), "头像设置成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "头像设置失败", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateName(String name) {
        if (name.trim().isEmpty()) {
            tilName.setError("请输入姓名");
            return false;
        } else if (name.trim().length() < 2 || name.trim().length() > 10) {
            tilName.setError("姓名长度应在2-10个字符之间");
            return false;
        } else {
            tilName.setError(null);
            return true;
        }
    }

    private boolean validateSchool(String school) {
        if (school.trim().isEmpty()) {
            tilSchool.setError("请输入学校");
            return false;
        } else {
            tilSchool.setError(null);
            return true;
        }
    }

    private void saveStudent() {
        String name = etName.getText().toString().trim();
        String school = etSchool.getText().toString().trim();
        String className = etClass.getText().toString().trim();
        String graduationYearStr = actGraduationYear.getText().toString();

        // 验证输入
        if (!validateName(name) || !validateSchool(school)) {
            return;
        }

        if (graduationYearStr.isEmpty()) {
            tilGraduationYear.setError("请选择高考年份");
            return;
        }

        int graduationYear;
        try {
            graduationYear = Integer.parseInt(graduationYearStr);
        } catch (NumberFormatException e) {
            tilGraduationYear.setError("年份格式错误");
            return;
        }

        // 获取科目类型
        String subjectType = rgSubjectType.getCheckedRadioButtonId() == R.id.rb_liberal_arts ? "文科" : "理科";

        // 回调保存
        if (saveListener != null) {
            if (editingStudent != null) {
                // 编辑模式
                editingStudent.setName(name);
                editingStudent.setSchool(school);
                editingStudent.setClassName(className);
                editingStudent.setSubjectType(subjectType);
                editingStudent.setGraduationYear(graduationYear);
                if (selectedAvatarPath != null) {
                    editingStudent.setAvatarUrl(selectedAvatarPath);
                }
                saveListener.onStudentUpdate(editingStudent);
            } else {
                // 添加模式
                saveListener.onStudentAdd(name, school, className, subjectType, graduationYear, selectedAvatarPath);
            }
        }

        dismiss();
    }

    /**
     * 学生保存回调接口
     */
    public interface OnStudentSaveListener {
        void onStudentAdd(String name, String school, String className, String subjectType, int graduationYear, String avatarUrl);

        void onStudentUpdate(Student student);
    }
}
