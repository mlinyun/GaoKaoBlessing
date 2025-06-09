package com.mlinyun.gaokaoblessing.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mlinyun.gaokaoblessing.data.dao.StudentDao;
import com.mlinyun.gaokaoblessing.data.database.AppDatabase;
import com.mlinyun.gaokaoblessing.data.entity.Student;
import com.mlinyun.gaokaoblessing.data.repository.StudentRepository;
import com.mlinyun.gaokaoblessing.manager.UserSessionManager;
import com.mlinyun.gaokaoblessing.utils.ImageUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 学生管理系统集成测试
 * 验证完整的学生CRUD操作、图片处理、用户会话管理等功能
 */
@RunWith(AndroidJUnit4.class)
public class StudentManagementIntegrationTest {

    private Context context;
    private AppDatabase database;
    private StudentDao studentDao;
    private StudentRepository studentRepository;
    private UserSessionManager sessionManager;

    private static final String TEST_USER_ID = "test_user_123";
    private static final int TIMEOUT_SECONDS = 5;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // 初始化数据库（使用内存数据库进行测试）
        database = AppDatabase.getTestInstance(context);
        studentDao = database.studentDao();
        studentRepository = new StudentRepository(studentDao);

        // 初始化用户会话管理器
        sessionManager = UserSessionManager.getInstance(context);

        // 清理测试数据
        cleanupTestData();
    }

    @After
    public void tearDown() {
        cleanupTestData();
        if (database != null) {
            database.close();
        }
    }

    /**
     * 测试学生完整的CRUD操作流程
     */
    @Test
    public void testStudentCRUDOperations() throws InterruptedException {
        System.out.println("🧪 开始测试学生CRUD操作...");

        // 1. 创建测试学生
        Student testStudent = createTestStudent();

        // 2. 测试添加学生
        CountDownLatch insertLatch = new CountDownLatch(1);
        final Student[] insertedStudent = new Student[1];

        studentRepository.insertStudent(testStudent, new StudentRepository.OnResultCallback<Long>() {
            @Override
            public void onSuccess(Long result) {
                assertTrue("学生ID应该大于0", result > 0);
                testStudent.setId(result.intValue());
                insertedStudent[0] = testStudent;
                insertLatch.countDown();
                System.out.println("✅ 学生添加成功，ID: " + result);
            }

            @Override
            public void onError(Exception error) {
                fail("添加学生失败: " + error.getMessage());
                insertLatch.countDown();
            }
        });

        insertLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertNotNull("学生应该成功插入", insertedStudent[0]);

        // 3. 测试查询学生
        CountDownLatch queryLatch = new CountDownLatch(1);
        final List<Student>[] queryResult = new List[1];

        LiveData<List<Student>> studentsLiveData = studentRepository.getAllStudents(TEST_USER_ID);
        studentsLiveData.observeForever(new Observer<List<Student>>() {
            @Override
            public void onChanged(List<Student> students) {
                queryResult[0] = students;
                queryLatch.countDown();
                studentsLiveData.removeObserver(this);
            }
        });

        queryLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertNotNull("查询结果不应为空", queryResult[0]);
        assertEquals("应该有1个学生", 1, queryResult[0].size());

        Student queriedStudent = queryResult[0].get(0);
        assertEquals("学生姓名应该匹配", testStudent.getName(), queriedStudent.getName());
        assertEquals("学校应该匹配", testStudent.getSchool(), queriedStudent.getSchool());
        System.out.println("✅ 学生查询成功: " + queriedStudent.getName());

        // 4. 测试更新学生
        queriedStudent.setSchool("更新后的学校");
        queriedStudent.setFollowed(true);

        CountDownLatch updateLatch = new CountDownLatch(1);
        studentRepository.updateStudent(queriedStudent, new StudentRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                updateLatch.countDown();
                System.out.println("✅ 学生更新成功");
            }

            @Override
            public void onError(Exception error) {
                fail("更新学生失败: " + error.getMessage());
                updateLatch.countDown();
            }
        });

        updateLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // 5. 验证更新结果
        CountDownLatch verifyUpdateLatch = new CountDownLatch(1);
        final List<Student>[] updateResult = new List[1];

        LiveData<List<Student>> updatedStudentsLiveData = studentRepository.getAllStudents(TEST_USER_ID);
        updatedStudentsLiveData.observeForever(new Observer<List<Student>>() {
            @Override
            public void onChanged(List<Student> students) {
                updateResult[0] = students;
                verifyUpdateLatch.countDown();
                updatedStudentsLiveData.removeObserver(this);
            }
        });

        verifyUpdateLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        Student updatedStudent = updateResult[0].get(0);
        assertEquals("学校应该已更新", "更新后的学校", updatedStudent.getSchool());
        assertTrue("关注状态应该为true", updatedStudent.isFollowed());

        // 6. 测试删除学生
        CountDownLatch deleteLatch = new CountDownLatch(1);
        studentRepository.deleteStudent(updatedStudent, new StudentRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                deleteLatch.countDown();
                System.out.println("✅ 学生删除成功");
            }

            @Override
            public void onError(Exception error) {
                fail("删除学生失败: " + error.getMessage());
                deleteLatch.countDown();
            }
        });

        deleteLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // 7. 验证删除结果
        CountDownLatch verifyDeleteLatch = new CountDownLatch(1);
        final List<Student>[] deleteResult = new List[1];

        LiveData<List<Student>> deletedStudentsLiveData = studentRepository.getAllStudents(TEST_USER_ID);
        deletedStudentsLiveData.observeForever(new Observer<List<Student>>() {
            @Override
            public void onChanged(List<Student> students) {
                deleteResult[0] = students;
                verifyDeleteLatch.countDown();
                deletedStudentsLiveData.removeObserver(this);
            }
        });

        verifyDeleteLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertEquals("删除后应该没有学生", 0, deleteResult[0].size());

        System.out.println("✅ 学生CRUD操作测试完成！");
    }

    /**
     * 测试学生搜索和筛选功能
     */
    @Test
    public void testStudentSearchAndFilter() throws InterruptedException {
        System.out.println("🧪 开始测试学生搜索和筛选...");

        // 创建多个测试学生
        Student student1 = createTestStudent("张三", "北京一中", "高三1班", "理科", 2024);
        Student student2 = createTestStudent("李四", "上海中学", "高三2班", "文科", 2024);
        Student student3 = createTestStudent("王五", "北京一中", "高三3班", "理科", 2025);

        // 添加学生
        addStudentSync(student1);
        addStudentSync(student2);
        addStudentSync(student3);

        // 测试按学校筛选
        CountDownLatch schoolFilterLatch = new CountDownLatch(1);
        final List<Student>[] schoolFilterResult = new List[1];

        LiveData<List<Student>> schoolStudents = studentRepository.getStudentsBySchool(TEST_USER_ID, "北京一中");
        schoolStudents.observeForever(new Observer<List<Student>>() {
            @Override
            public void onChanged(List<Student> students) {
                schoolFilterResult[0] = students;
                schoolFilterLatch.countDown();
                schoolStudents.removeObserver(this);
            }
        });

        schoolFilterLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertEquals("北京一中应该有2个学生", 2, schoolFilterResult[0].size());
        System.out.println("✅ 按学校筛选测试通过");

        // 测试按科目筛选
        CountDownLatch subjectFilterLatch = new CountDownLatch(1);
        final List<Student>[] subjectFilterResult = new List[1];

        LiveData<List<Student>> subjectStudents = studentRepository.getStudentsBySubjectType(TEST_USER_ID, "理科");
        subjectStudents.observeForever(new Observer<List<Student>>() {
            @Override
            public void onChanged(List<Student> students) {
                subjectFilterResult[0] = students;
                subjectFilterLatch.countDown();
                subjectStudents.removeObserver(this);
            }
        });

        subjectFilterLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertEquals("理科学生应该有2个", 2, subjectFilterResult[0].size());
        System.out.println("✅ 按科目筛选测试通过");

        // 测试搜索功能
        CountDownLatch searchLatch = new CountDownLatch(1);
        final List<Student>[] searchResult = new List[1];

        LiveData<List<Student>> searchStudents = studentRepository.searchStudents(TEST_USER_ID, "张");
        searchStudents.observeForever(new Observer<List<Student>>() {
            @Override
            public void onChanged(List<Student> students) {
                searchResult[0] = students;
                searchLatch.countDown();
                searchStudents.removeObserver(this);
            }
        });

        searchLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertEquals("搜索'张'应该找到1个学生", 1, searchResult[0].size());
        assertEquals("搜索结果应该是张三", "张三", searchResult[0].get(0).getName());
        System.out.println("✅ 搜索功能测试通过");

        System.out.println("✅ 学生搜索和筛选测试完成！");
    }

    /**
     * 测试图片处理功能
     */
    @Test
    public void testImageProcessing() {
        System.out.println("🧪 开始测试图片处理功能...");

        // 测试文件存在检查
        String nonExistentPath = "/non/existent/path.jpg";
        assertFalse("不存在的文件应该返回false", ImageUtils.isFileExists(nonExistentPath));

        // 测试文件大小计算
        long size = ImageUtils.getFileSizeKB(nonExistentPath);
        assertEquals("不存在文件的大小应该为0", 0, size);

        // 测试删除不存在的文件
        boolean deleteResult = ImageUtils.deleteAvatarFile(nonExistentPath);
        assertFalse("删除不存在的文件应该返回false", deleteResult);

        System.out.println("✅ 图片处理功能测试完成！");
    }

    /**
     * 测试用户会话管理
     */
    @Test
    public void testUserSessionManagement() {
        System.out.println("🧪 开始测试用户会话管理...");

        // 清除当前会话
        sessionManager.clearSession();
        assertFalse("清除会话后应该未登录", sessionManager.isLoggedIn());
        assertEquals("清除会话后用户ID应该为-1", -1, sessionManager.getCurrentUserId());

        // 模拟用户登录
        com.mlinyun.gaokaoblessing.data.model.User testUser = new com.mlinyun.gaokaoblessing.data.model.User();
        testUser.setId(123);
        testUser.setUsername("testuser");

        sessionManager.setCurrentUser(testUser);
        assertTrue("设置用户后应该已登录", sessionManager.isLoggedIn());
        assertEquals("用户ID应该匹配", 123, sessionManager.getCurrentUserId());
        assertEquals("用户名应该匹配", "testuser", sessionManager.getCurrentUsername());

        System.out.println("✅ 用户会话管理测试完成！");
    }

    /**
     * 性能测试：批量操作
     */
    @Test
    public void testBatchOperations() throws InterruptedException {
        System.out.println("🧪 开始测试批量操作性能...");

        long startTime = System.currentTimeMillis();

        // 批量添加100个学生
        CountDownLatch batchLatch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            Student student = createTestStudent("学生" + i, "学校" + (i % 10), "班级" + (i % 5),
                    i % 2 == 0 ? "理科" : "文科", 2024 + (i % 3));

            studentRepository.insertStudent(student, new StudentRepository.OnResultCallback<Long>() {
                @Override
                public void onSuccess(Long result) {
                    batchLatch.countDown();
                }

                @Override
                public void onError(Exception error) {
                    batchLatch.countDown();
                }
            });
        }

        batchLatch.await(30, TimeUnit.SECONDS); // 增加超时时间

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("✅ 批量添加100个学生耗时: " + duration + "ms");
        assertTrue("批量操作应该在30秒内完成", duration < 30000);

        // 验证数据
        CountDownLatch countLatch = new CountDownLatch(1);
        final Integer[] count = new Integer[1];

        LiveData<Integer> countLiveData = studentRepository.getStudentCount(TEST_USER_ID);
        countLiveData.observeForever(new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                count[0] = integer;
                countLatch.countDown();
                countLiveData.removeObserver(this);
            }
        });

        countLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertEquals("应该有100个学生", 100, count[0].intValue());

        System.out.println("✅ 批量操作性能测试完成！");
    }

    // 辅助方法

    private Student createTestStudent() {
        return createTestStudent("测试学生", "测试学校", "测试班级", "理科", 2024);
    }

    private Student createTestStudent(String name, String school, String className, String subjectType, int graduationYear) {
        return new Student(name, school, className, subjectType, graduationYear, TEST_USER_ID);
    }

    private void addStudentSync(Student student) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        studentRepository.insertStudent(student, new StudentRepository.OnResultCallback<Long>() {
            @Override
            public void onSuccess(Long result) {
                student.setId(result.intValue());
                latch.countDown();
            }

            @Override
            public void onError(Exception error) {
                latch.countDown();
            }
        });
        latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private void cleanupTestData() {
        try {
            if (studentDao != null) {
                studentDao.deleteAllStudents();
            }
        } catch (Exception e) {
            // 忽略清理错误
        }
    }

    /**
     * 运行所有测试的主方法
     */
    public static void runAllTests() {
        System.out.println("🚀 开始学生管理系统集成测试...");
        System.out.println("========================================");

        StudentManagementIntegrationTest test = new StudentManagementIntegrationTest();

        try {
            test.setUp();

            test.testStudentCRUDOperations();
            test.testStudentSearchAndFilter();
            test.testImageProcessing();
            test.testUserSessionManagement();
            test.testBatchOperations();

            System.out.println("========================================");
            System.out.println("✅ 所有测试通过！学生管理系统运行正常。");

        } catch (Exception e) {
            System.out.println("❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}
