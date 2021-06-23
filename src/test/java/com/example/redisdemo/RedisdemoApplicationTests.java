package com.example.redisdemo;

import com.example.redisdemo.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.util.SerializationUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisdemoApplicationTests {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Test
    public void contextLoads() {
    }
    /**
     * 序列化、反序列化
     */
    @Test
    public void testServ() {
        User user=new User();
        user.setId("1");
        user.setName("xxxx");
        user.setPassword("123456");
        ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
        stringObjectValueOperations.set("user",user);
        User user1 = (User) stringObjectValueOperations.get("user");
        System.out.println(user1);
    }

    /**
     * 操作String
     */
    @Test
    public void testString() {

        ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
        //设置单条值
        stringObjectValueOperations.set("food","milk");
        //获取单条值
        String food = (String) stringObjectValueOperations.get("food");
        System.out.println(food);
        //设置多条值
        Map<String, String> map = new HashMap<>();
        map.put("age", "116");
        map.put("addr", "jiangning");
        stringObjectValueOperations.multiSet(map);
        List<String> list=new ArrayList<>();
        list.add("food");
        list.add("age");
        list.add("addr");
        List<Object> objects = stringObjectValueOperations.multiGet(list);
        objects.forEach(System.out::println);
        //层级目录
        stringObjectValueOperations.set("user:02:cart:item","apple");
        Object o = stringObjectValueOperations.get("user:02:cart:item");
        System.out.println(o);
        redisTemplate.delete("food");
    }

    /**
     * 操作hash
     */
    @Test
    public void testHash() {

        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        //设置单条值
        hashOperations.put("student","name","xgd");
        //获取单条值
        String name = (String) hashOperations.get("student","name");
        System.out.println(name);
        //设置多条值
        Map<String, String> map = new HashMap<>();
        map.put("age", "116");
        map.put("addr", "jiangning");
        hashOperations.putAll("student",map);
        List<Object> list=new ArrayList<>();
        list.add("name");
        list.add("age");
        list.add("addr");
        List<Object> objects = hashOperations.multiGet("student",list);
        objects.forEach(System.out::println);
        //层级目录
        Map<Object, Object> student = hashOperations.entries("student");
        student.forEach((k,v)->System.out.println(v));
        hashOperations.delete("student","age");
    }
    /**
     * 操作list
     */
    @Test
    public void testList() {

        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        listOperations.leftPush("students","w5");
        listOperations.leftPush("students","z6");
        listOperations.leftPush("students","t7");
        listOperations.rightPush("students","l4");
        listOperations.rightPush("students","z3");
        listOperations.rightPush("students","l4","z9");//在l4右边添加z9,如果l4不存在，z9不会插入
        List<Object> students1 = listOperations.range("students", 0, 5);
        students1.forEach(System.out::println);
        Long aLong = listOperations.size("students");
        System.out.println(aLong);
        listOperations.remove("students",1,"z3");
        //左弹出--相当于删除
        Object students = listOperations.leftPop("students");
        System.out.println(students);
        //右弹出--相当于删除
        Object students2 = listOperations.rightPop("students");
        System.out.println(students2);
        //消息中间件leftPush与rightPop 消息队列
    }
    /**
     * 操作set
     */
    @Test
    public void testSet() {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        String[] strings=new String[]{"aa","bb","cc","dd","ee"};
        setOperations.add("class",strings);
        Long aLong = setOperations.size("class");
        System.out.println(aLong);
        Set<Object> aClass = setOperations.members("class");
        aClass.forEach(System.out::println);
        setOperations.remove("class","dd");
        Set<Object> aClass2 = setOperations.members("class");
        aClass2.forEach(System.out::println);
    }
    /**
     * 操作sorted set
     */
    @Test
    public void testSortedSet() {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add("score","zhangfei",6D);
        zSetOperations.add("score","zhangfei2",7D);
        zSetOperations.add("score","zhangfei",6D);
        zSetOperations.add("score","zhangfei6",9D);
        Long aLong = zSetOperations.size("score");
        System.out.println(aLong);
        Set<Object> ascore = zSetOperations.range("score",0,5);
        ascore.forEach(System.out::println);
        zSetOperations.remove("score","zhangfei6");
        Set<Object> ascore2 = zSetOperations.range("score",0,5);
        ascore2.forEach(System.out::println);
    }

    /**
     * 操作失效时间
     */
    @Test
    public void testExpire() {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        opsForValue.set("code","10010",10, TimeUnit.SECONDS);
        //给已经存在key设置失效时间
        redisTemplate.expire("score",10,TimeUnit.SECONDS);
//        Long aLong = redisTemplate.getExpire("code");
//        System.out.println(aLong);
        //nx 不存在key，设置
        opsForValue.setIfAbsent("world","china",10,TimeUnit.SECONDS);
        //px 存在key，设置
        opsForValue.setIfPresent("world","china",10,TimeUnit.SECONDS);
    }
}
