package com.cartravel.tools;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zxc
 */
/*加载资源文件*/
public class ResourceLoader {
    private static ResourceLoader loader = new ResourceLoader();
    private static Map<String, Properties> loaderMap = new HashMap<String, Properties>();

    private ResourceLoader() {
    }

    public static ResourceLoader getInstance() {
        return loader;
    }

    /**
     * 根据配置文件名称加载配置文件
     * @param fileName 文件名
     * @return  Properties对象
     * @throws Exception
     */
    public Properties getPropFromProperties(String fileName) throws Exception {

        Properties prop = loaderMap.get(fileName);
        if (prop != null) {
            return prop;
        }
        String filePath = null;
//        String configPath = System.getProperty("resourses");
        //部署到集群上，如果配置文件放在jar包外面，那么可以用下面的方式进行配置文件查找
//        String configPath = System.getProperty("user.dir");
//
//        if (configPath == null) {
//            filePath = this.getClass().getClassLoader().getResource(fileName).getPath();
//        } else {
//            filePath = configPath + "/" + fileName;
//        }
        prop = new Properties();
        //部署到集群上或者本地，如果配置文件在jar包内部，那么使用下面的方式
        InputStream inputstream = getClass().getResourceAsStream("/"+fileName);
        prop.load(inputstream);
//        prop.load(new FileInputStream(new File(filePath)));

        loaderMap.put(fileName, prop);
        return prop;
    }
}
