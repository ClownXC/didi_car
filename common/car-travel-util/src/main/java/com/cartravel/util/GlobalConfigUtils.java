package com.cartravel.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件加载类
 */
public class GlobalConfigUtils {
    static Properties properties = new Properties();

    static {
        try {
            // 使用ClassLoader加载properties配置文件生成对应的输入流
            InputStream in = GlobalConfigUtils.class.getClassLoader()
                    .getResourceAsStream("config.properties");
            // 使用properties对象加载输入流
            properties.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取key对应的value值
     *
     * @param key
     * @return
     */
    public static String getProp(String key) {
        return (String) properties.get(key);
    }

    public static void main(String[] args) {
        String config = GlobalConfigUtils.getProp("jedis.pool.host.dev");
        System.out.println("config:" + config);
    }

}
