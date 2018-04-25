package framework.context;

import framework.beans.BeanDefinition;
import framework.context.support.BeanDefinitionReader;
import framework.core.BeanFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jim
 * @create 2018-04-25 19:36
 **/
public class ApplicationContext implements BeanFactory {
    private String [] configLocations;
    private BeanDefinitionReader reader;
    //注册的时候将BeanName和BeanDefinition对象放入集合中
    private Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String,BeanDefinition>();

    public ApplicationContext(String ... configLocations) {
        this.configLocations = configLocations;
        refresh();
    }

    public void refresh(){
        //定位
        this.reader = new BeanDefinitionReader(configLocations);
        //加载
        //注册
        //注入
    }
    public Object getBean(String beanName) {
        return null;
    }
}
