package com.mlinyun.gaokaoblessing.ui.blessing.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mlinyun.gaokaoblessing.data.entity.Student;
import com.mlinyun.gaokaoblessing.databinding.ItemStudentSelectionBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生选择适配器
 */
public class StudentSelectionAdapter extends RecyclerView.Adapter<StudentSelectionAdapter.StudentViewHolder> {

    private List<Student> students = new ArrayList<>();
    private Student selectedStudent;
    private OnStudentSelectedListener listener;

    public interface OnStudentSelectedListener {
        void onStudentSelected(Student student);
    }

    public void setOnStudentSelectedListener(OnStudentSelectedListener listener) {
        this.listener = listener;
    }

    public void updateStudents(List<Student> students) {
        this.students = students != null ? students : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStudentSelectionBinding binding = ItemStudentSelectionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new StudentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.bind(student, student.equals(selectedStudent));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private final ItemStudentSelectionBinding binding;

        public StudentViewHolder(ItemStudentSelectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Student student, boolean isSelected) {
            // 设置学生信息
            binding.tvStudentName.setText(student.getName());
            binding.tvStudentInfo.setText(String.format("%s · %s",
                    student.getSchool(), student.getGrade()));

            // 设置头像（使用姓名首字母）
            String avatarText = student.getName().substring(0, 1);
            binding.tvStudentAvatar.setText(avatarText);

            // 设置选中状态
            binding.cardStudent.setSelected(isSelected);
            binding.rbSelected.setChecked(isSelected);

            // 设置点击事件
            binding.getRoot().setOnClickListener(v -> {
                if (selectedStudent != null && selectedStudent.equals(student)) {
                    // 取消选择
                    selectedStudent = null;
                } else {
                    // 选择学生
                    selectedStudent = student;
                }

                notifyDataSetChanged();

                if (listener != null) {
                    listener.onStudentSelected(selectedStudent);
                }
            });

            // 设置考试倒计时
            long daysUntilExam = calculateDaysUntilExam(student);
            if (daysUntilExam > 0) {
                binding.tvExamCountdown.setText(String.format("距离高考还有 %d 天", daysUntilExam));
                binding.tvExamCountdown.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.tvExamCountdown.setVisibility(android.view.View.GONE);
            }
        }

        private long calculateDaysUntilExam(Student student) {
            // 简化计算，假设高考日期为每年6月7日
            java.util.Calendar examDate = java.util.Calendar.getInstance();
            examDate.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
            examDate.set(java.util.Calendar.DAY_OF_MONTH, 7);

            // 如果今年的考试已过，计算明年的
            java.util.Calendar today = java.util.Calendar.getInstance();
            if (examDate.before(today)) {
                examDate.add(java.util.Calendar.YEAR, 1);
            }

            long diffInMs = examDate.getTimeInMillis() - today.getTimeInMillis();
            return diffInMs / (1000 * 60 * 60 * 24);
        }
    }
}
