package com.fdemo.myhibernate.testmain;


import com.fdemo.myhibernate.entity.SysUserEntity;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author fengxianbin
 * @description: 测试jdbc
 * @create 2020-12-21 13:47
 */
public class JdbcHelloWord {


    public static void main(String[] args) {
        Connection con=null;
        try {
            //获取JDBC连接
            con=getConnection5();
            //通过连接进行增删改查
            //新增
            String insertSql="insert into sys_user (id,name,sex,age) value (REPLACE(UUID(), '-', ''),?,?,?)";
            //execute(con,insertSql,"张小斐2","女",14);

            //修改
            String updateSql="update sys_user set age=? ";
            //execute(con,updateSql,16);

            //查询
            String selecetSql="select * from sys_user ";
            List<SysUserEntity> entitys=getList(SysUserEntity.class,con,selecetSql);
            if(entitys!=null&&entitys.size()>0){
                entitys.forEach(entity-> System.out.println(entity));
            }


            //删除
            String deleteSql="delete from sys_user";
            //execute(con,deleteSql);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(con,null,null);
        }

    }

    public static <T> List<T> getList(Class<T> clazz,Connection connection, String sql, Object ... args){
        //通用的增、删、改操作（体现一：增、删、改 ； 体现二：针对于不同的表）
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<T> reArrt=new ArrayList<>();
        try {
            //1.预编译sql语句，得到PreparedStatement对象
            ps = connection.prepareStatement(sql);
            //2.填充占位符
            for(int i = 0;i < args.length;i++){
                ps.setObject(i + 1, args[i]);
            }
            // 3.执行executeQuery(),得到结果集：ResultSet
            rs = ps.executeQuery();
            // 4.得到结果集的元数据：ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    // 获取列值
                    Object columnVal = rs.getObject(i + 1);
                    // 获取列的别名:列的别名，使用类的属性名充当
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    // 6.2使用反射，给对象的相应属性赋值
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, columnVal);
                }
                reArrt.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            close(connection,ps,rs);
        }
        return reArrt;
    }



    public static void execute(Connection connection,String sql,Object ... args){
        //通用的增、删、改操作（体现一：增、删、改 ； 体现二：针对于不同的表）
        PreparedStatement ps = null;
        try {
            //1.获取PreparedStatement的实例 (或：预编译sql语句)
            ps = connection.prepareStatement(sql);
            //2.填充占位符
            for(int i = 0;i < args.length;i++){
                ps.setObject(i + 1, args[i]);
            }
            //3.执行sql语句
            ps.execute();
            System.out.println("执行成功！sql="+sql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            close(connection,ps,null);
        }
    }



    public static void close(Connection connection,PreparedStatement ps,ResultSet rs){
        try {
            if(rs!=null){
                rs.close();
            }
            if(ps!=null){
                ps.close();
            }
            if(connection!=null){
                connection.close();
            }
        }catch (Exception closeEx){
            closeEx.printStackTrace();
        }
    }

    public static Connection getConnection(){
        Connection connection=null;
        try {
            //1 实例化 java的驱动类（java.sql.Driver）接口
            Driver driver=new com.mysql.jdbc.Driver();

            //2 提供url，指明具体操作的数据库
            String url ="jdbc:mysql://127.0.0.1:3306/fmyfristData";

            //3 指定用户名密码
            String user="root";
            String password="root";
            Properties properties=new Properties();
            properties.put("user",user);
            properties.put("password",password);

            //4 获取链接
            connection= driver.connect(url,properties);
            System.out.println("con====>"+connection);
            System.out.println("连接成功！！");
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection getConnection2() {
        Connection connection=null;
        try {
            //1.实例化Driver
            String driverClass = "com.mysql.jdbc.Driver";
            Class clazz = Class.forName(driverClass);
            Driver driver = (Driver) clazz.newInstance();

            //2 提供url，指明具体操作的数据库
            String url ="jdbc:mysql://127.0.0.1:3306/fmyfristData";

            //3 指定用户名密码
            String user="root";
            String password="root";
            Properties properties=new Properties();
            properties.put("user",user);
            properties.put("password",password);

            //4 获取链接
            connection= driver.connect(url,properties);
            System.out.println("con====>"+connection);
            System.out.println("连接成功！！");
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection getConnection3() {
        Connection connection=null;
        try {
            //1.设置数据库的参数

            String user="root";
            String password="root";
            String url ="jdbc:mysql://127.0.0.1:3306/fmyfristData";

            //2.反射获取对象
            String driverClass = "com.mysql.jdbc.Driver";
            Class clazz = Class.forName(driverClass);
            Driver driver = (Driver) clazz.newInstance();

            //3.注册驱动
            DriverManager.registerDriver(driver);

            //4.获取连接
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("con====>"+connection);
            System.out.println("连接成功！！");
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }


    public static Connection getConnection4(){
        Connection connection=null;
        try {

            //1.设置数据库的参数
            String user="root";
            String password="root";
            String url ="jdbc:mysql://127.0.0.1:3306/fmyfristData";

            //2.反射获取对象
            String driverClass = "com.mysql.jdbc.Driver";
            Class.forName(driverClass);

            //3.获取连接 （由于在实例化mysql驱动对象时静态块已经做了注册驱动的操作）
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("con====>"+connection);
            System.out.println("连接成功！！");
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection getConnection5(){
        Connection connection=null;
        try {

            //1.加载配置文件
            InputStream in=JdbcHelloWord.class.getClassLoader().getResourceAsStream("jdbc.properties");
            Properties properties=new Properties();
            properties.load(in);

            String user=properties.getProperty("user");
            String password=properties.getProperty("password");
            String url =properties.getProperty("url");

            //2.反射获取对象
            String driverClass = properties.getProperty("driverClass");
            Class.forName(driverClass);

            //3.获取连接 （由于在实例化mysql驱动对象时静态块已经做了注册驱动的操作）
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("con====>"+connection);
            System.out.println("连接成功！！");
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }


}
