package framework.context.support;

import framework.beans.BeanDefinition;
import util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author jim
 * @create 2018-04-25 19:40
 **/
public class BeanDefinitionReader {
    private  Properties config = new Properties();
    private List<String> registerBeanClasses = new ArrayList();
    private final String SCAN_PACKAGE = "scanPackage";
    public BeanDefinitionReader(String ... locations) {
        //在Spring中是通过Reader去查找和定位对不对
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != is){is.close();}
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }
    public List<String> loadBeanDefinitions() {
        return registerBeanClasses;
    }
    public Properties getConfig(){return config;}
    private void doScanner(String packageName){
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.","/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()){
            if(file.isDirectory()){
                doScanner(packageName + "." +file.getName());
            }else {
                registerBeanClasses.add(packageName + "." + file.getName().replace(".class",""));
            }
        }
    }
    //注册的步骤-返回BeanDefinition对象
    public BeanDefinition registerBean(String className){
        if(this.registerBeanClasses.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(StringUtils.lowerFirstCase(className.substring(className.lastIndexOf(".") + 1)));
            return beanDefinition;
        }
        return null;
    }

}
