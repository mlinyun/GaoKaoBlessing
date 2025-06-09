package com.mlinyun.gaokaoblessing.ui.student.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.mlinyun.gaokaoblessing.R;
import com.mlinyun.gaokaoblessing.data.entity.Student;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 学生卡片适配器
 */
public class StudentAdapter extends ListAdapter<Student, StudentAdapter.StudentViewHolder> {

    private OnStudentActionListener actionListener;
    private Context context;

    public StudentAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    public void setOnStudentActionListener(OnStudentActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_card, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = getItem(position);
        holder.bind(student);
    }

    private static final DiffUtil.ItemCallback<Student> DIFF_CALLBACK = new DiffUtil.ItemCallback<Student>() {
        @Override
        public boolean areItemsTheSame(@NonNull Student oldItem, @NonNull Student newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Student oldItem, @NonNull Student newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getSchool().equals(newItem.getSchool()) &&
                    oldItem.getClassName().equals(newItem.getClassName()) &&
                    oldItem.getBlessingCount() == newItem.getBlessingCount() &&
                    oldItem.isFollowed() == newItem.isFollowed();
        }
    };

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivAvatar;
        private TextView tvName;
        private TextView tvSchoolClass;
        private TextView tvSubjectType;
        private TextView tvBlessingCount;
        private TextView tvGraduationYear;
        private ImageView ivFollow;
        private ImageView ivMore;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSchoolClass = itemView.findViewById(R.id.tv_school_class);
            tvSubjectType = itemView.findViewById(R.id.tv_subject_type);
            tvBlessingCount = itemView.findViewById(R.id.tv_blessing_count);
            tvGraduationYear = itemView.findViewById(R.id.tv_graduation_year);
            ivFollow = itemView.findViewById(R.id.iv_follow);
            ivMore = itemView.findViewById(R.id.iv_more);
        }

        public void bind(Student student) {
            // 设置基本信息
            tvName.setText(student.getName());
            tvSchoolClass.setText(String.format("%s · %s", student.getSchool(), student.getClassName()));
            tvSubjectType.setText(student.getSubjectType());
            tvBlessingCount.setText(String.format("%d次祝福", student.getBlessingCount()));
            tvGraduationYear.setText(String.format("%d届", student.getGraduationYear()));
            // 加载头像
            if (student.getAvatarUrl() != null && !student.getAvatarUrl().isEmpty()) {
                String imageUri = student.getAvatarUrl();
                // 如果是本地文件路径，添加file://前缀
                if (imageUri.startsWith("/")) {
                    imageUri = "file://" + imageUri;
                }

                Glide.with(context)
                        .load(imageUri)
                        .transform(new CircleCrop())
                        .placeholder(R.drawable.ic_student_avatar_default)
                        .error(R.drawable.ic_student_avatar_default)
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_student_avatar_default);
            }

            // 设置关注状态
            updateFollowStatus(student.isFollowed());

            // 设置点击事件
            itemView.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onStudentClick(student);
                }
            });

            ivFollow.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onFollowClick(student, !student.isFollowed());
                }
            });

            ivMore.setOnClickListener(v -> showPopupMenu(v, student));
        }

        private void updateFollowStatus(boolean isFollowed) {
            if (isFollowed) {
                ivFollow.setImageResource(R.drawable.ic_favorite);
                ivFollow.setColorFilter(context.getColor(R.color.primary_color));
            } else {
                ivFollow.setImageResource(R.drawable.ic_favorite_border);
                ivFollow.setColorFilter(context.getColor(R.color.text_secondary_dark));
            }
        }

        private void showPopupMenu(View anchor, Student student) {
            PopupMenu popup = new PopupMenu(context, anchor);
            popup.getMenuInflater().inflate(R.menu.menu_student_actions, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (actionListener == null) return false;

                int itemId = item.getItemId();
                if (itemId == R.id.action_edit) {
                    actionListener.onEditClick(student);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    actionListener.onDeleteClick(student);
                    return true;
                } else if (itemId == R.id.action_bless) {
                    actionListener.onBlessClick(student);
                    return true;
                } else if (itemId == R.id.action_view_blessings) {
                    actionListener.onViewBlessingsClick(student);
                    return true;
                }
                return false;
            });

            popup.show();
        }
    }

    /**
     * 学生操作回调接口
     */
    public interface OnStudentActionListener {
        void onStudentClick(Student student);

        void onFollowClick(Student student, boolean isFollowed);

        void onEditClick(Student student);

        void onDeleteClick(Student student);

        void onBlessClick(Student student);

        void onViewBlessingsClick(Student student);
    }
}
