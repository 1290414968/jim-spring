package framework.context;

import framework.beans.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory extends AbstractApplicationContext {
    //注册的时候将BeanName和BeanDefinition对象放入集合中
    protected Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String,BeanDefinition>();
    protected void onRefresh(){
    }
    @Override
    protected void refreshBeanFactory() {

    }
}
