package com.mlinyun.gaokaoblessing.data.repository;

import android.content.Context;

import com.mlinyun.gaokaoblessing.data.database.AppDatabase;
import com.mlinyun.gaokaoblessing.data.entity.BlessingTemplateEntity;
import com.mlinyun.gaokaoblessing.data.repository.impl.BlessingTemplateRepositoryImpl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 数据初始化器 - 初始化默认祈福模板
 */
public class DataInitializer {

    private final BlessingTemplateRepository templateRepository;

    public DataInitializer(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.templateRepository = new BlessingTemplateRepositoryImpl(database.blessingTemplateDao());
    }

    /**
     * 初始化默认祈福模板
     */
    public CompletableFuture<Void> initializeDefaultTemplates() {
        return templateRepository.initializeDefaultTemplates();
    }

    private List<BlessingTemplateEntity> createDefaultTemplates() {
        return Arrays.asList(
                // 学业祝福类
                new BlessingTemplateEntity(
                        "愿你在高考中发挥出最佳水平，金榜题名，前程似锦！",
                        "学业",
                        4.8f,
                        1250,
                        "高考必胜",
                        "golden"
                ),
                new BlessingTemplateEntity(
                        "十年寒窗苦读，今朝金榜题名。相信你一定能考出理想的成绩！",
                        "学业",
                        4.9f,
                        2100,
                        "十年磨剑",
                        "star"
                ),
                new BlessingTemplateEntity(
                        "愿你笔下生花，思路清晰，每一道题都是你的强项！",
                        "学业",
                        4.7f,
                        980,
                        "笔下生花",
                        "rainbow"
                ),

                // 健康平安类
                new BlessingTemplateEntity(
                        "愿你身体健康，心情愉悦，以最佳状态迎接高考！",
                        "健康",
                        4.6f,
                        850,
                        "身心健康",
                        "flower"
                ),
                new BlessingTemplateEntity(
                        "祝你高考期间一切顺利，平平安安，健健康康！",
                        "平安",
                        4.5f,
                        720,
                        "平安顺利",
                        "golden"
                ),

                // 鼓励支持类
                new BlessingTemplateEntity(
                        "相信自己，你是最棒的！加油，未来的大学生！",
                        "鼓励",
                        4.8f,
                        1500,
                        "你最棒",
                        "star"
                ),
                new BlessingTemplateEntity(
                        "不管结果如何，你已经很优秀了。相信自己，勇敢前行！",
                        "鼓励",
                        4.7f,
                        1200,
                        "勇敢前行",
                        "rainbow"
                ),
                new BlessingTemplateEntity(
                        "每一份努力都不会白费，愿你的付出都能得到回报！",
                        "鼓励",
                        4.9f,
                        1800,
                        "努力回报",
                        "golden"
                ),

                // 梦想实现类
                new BlessingTemplateEntity(
                        "愿你所有的梦想都能成真，心想事成，学业有成！",
                        "成功",
                        4.8f,
                        1350,
                        "梦想成真",
                        "star"
                ),
                new BlessingTemplateEntity(
                        "愿你踏进理想的大学校园，开启人生新的篇章！",
                        "成功",
                        4.9f,
                        1950,
                        "理想大学",
                        "golden"
                ),

                // 考试顺利类
                new BlessingTemplateEntity(
                        "愿你在考场上文思如泉涌，下笔如有神！",
                        "学业",
                        4.7f,
                        1100,
                        "文思泉涌",
                        "rainbow"
                ),
                new BlessingTemplateEntity(
                        "愿你在每一场考试中都能超常发挥，取得优异成绩！",
                        "学业",
                        4.8f,
                        1400,
                        "超常发挥",
                        "star"
                ),

                // 心态调整类
                new BlessingTemplateEntity(
                        "保持平常心，相信自己的实力，你一定可以的！",
                        "鼓励",
                        4.6f,
                        900,
                        "平常心",
                        "flower"
                ),
                new BlessingTemplateEntity(
                        "放松心情，沉着应对，你的努力一定会有收获！",
                        "鼓励",
                        4.7f,
                        1050,
                        "沉着应对",
                        "golden"
                ),

                // 家庭支持类
                new BlessingTemplateEntity(
                        "家人永远是你最坚强的后盾，勇敢地去追求你的梦想吧！",
                        "家庭",
                        4.9f,
                        1650,
                        "家庭支持",
                        "star"
                ),
                new BlessingTemplateEntity(
                        "愿家庭的温暖给你力量，让你在高考路上勇敢前行！",
                        "家庭",
                        4.8f,
                        1300,
                        "家庭温暖",
                        "rainbow"
                )
        );
    }
}
