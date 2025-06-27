//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
//import org.example.DemoApplication;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = DemoApplication.class)
////@AutoConfigureMockMvc  //用于自动配置MockMvc,配置后MockMvc类可以直接注入,相当于new MockMvc
//public class MapperTest {
//
//    @Autowired
//    private IUserService userService;
//
//    @Autowired
//    private IUserDao dao;
//
//    @Test
//    public void testSave() {
//        //这是使用原生的save方法
////        User user = new User();
////        user.setUsername("测试");
////        user.setPassword("111");
////        dao.save(user);
//    }
//    @Test
//    public void testSave2() {
//        //使用的是MP中的API的方法
//        User user = new User();
//        user.setUsername("测试222");
//        user.setPassword("1112222");
//        boolean save = userService.save(user);
//        System.out.println(save);
//    }
//    @Test
//    public void testSaveBatch() {
////        User user1 = new User();
////        user1.setUsername("测试1");
////        user1.setPassword("111");
////        user1.setName("批量1");
////        User user2 = new User();
////        user2.setUsername("测试2");
////        user2.setPassword("111");
////        user2.setName("批量2");
////        User user3 = new User();
////        user3.setUsername("测试3");
////        user3.setPassword("111");
////        user3.setName("批量3");
////        User user4 = new User();
////        user4.setUsername("测试4");
////        user4.setPassword("111");
////        user4.setName("批量4");
////        List<User> list = Arrays.asList(user1, user2, user3, user4);
////        boolean save = userService.saveBatch(list);
////        System.out.println(save);
//    }
//
//    @Test
//    public void testSaveOrUpdate(){
//        User user = new User();
//        user.setId(11);
//        user.setUsername("测试");
//        user.setPassword("111");
//        //id存在则修改，不存在则添加
//        boolean b = userService.saveOrUpdate(user);
//        System.out.println(b);
//    }
//    @Test
//    public void testRemove(){
//        //ne：不等于
//        //ge：大于等于
//        //eq：等于
//        QueryWrapper<User> qw = new QueryWrapper<User>().isNull("username");
//        boolean remove = userService.remove(qw);
//        System.out.println(remove);
//    }
//    @Test
//    public void testUpdate(){
//        UpdateWrapper<User> uw = new UpdateWrapper<User>().eq("id", 1).set("username", "测试").set("password", "aa");
//        boolean update = userService.update(uw);
//        System.out.println(update);
//    }
//
//    @Test
//    public void testList(){
////        QueryWrapper<User> qw = new QueryWrapper<User>().eq("password", "111");
////        userService.list(qw).forEach(System.out::println);
//    }
//
//    @Test
//    public void testPage(){
////        Page<User> page = new Page<>(1, 5);
////        Page<User> data = userService.page(page);
////        data.getRecords().forEach(System.out::println);
////        System.out.println(data.getTotal());
//    }
//}