package wangzh.single_login.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 生成Dao对象的工场
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/6/30
 */
public class DaoFactory {

    private static ApplicationContext sContext;

    // ***************************** 懒汉式单例模式，开始 *****************************
    private DaoFactory() {
        // 初始化Spring环境
        sContext = new ClassPathXmlApplicationContext("application-been.xml");
    }

    private static final class Holder {
        private static final DaoFactory sInstance = new DaoFactory();
    }

    public static DaoFactory getInstance() {
        return Holder.sInstance;
    }
    // ***************************** 懒汉式单例模式，结束 *****************************

    public <T> T getSpringBean(Class<T> beanClass, String beanId) {
        return sContext.getBean("userJDBCTemplate", beanClass);
    }

}
