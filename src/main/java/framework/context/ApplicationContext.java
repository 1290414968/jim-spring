package framework.context;

import framework.annotation.Autowired;
import framework.annotation.Controller;
import framework.annotation.Service;
import framework.beans.BeanDefinition;
import framework.beans.BeanPostProcessor;
import framework.beans.BeanWrapper;
import framework.context.support.BeanDefinitionReader;
import framework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.List;
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
    //在依赖注入的时候将Object对象实例化出来放入到集合中
    private Map<String,Object> cacheBeanMap = new ConcurrentHashMap<String,Object>();
    //在依赖注入的时候将BeanWrapper实例化出来放入到集合中
    private Map<String,BeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, BeanWrapper>();

    public ApplicationContext(String ... configLocations) {
        this.configLocations = configLocations;
        refresh();
    }
    public void refresh(){
        //定位 -- 扫描
        this.reader = new BeanDefinitionReader(configLocations);
        //加载
        List<String> registerBeanClasses =  reader.loadBeanDefinitions();
        //注册
        doRegister(registerBeanClasses);
        //依赖注入
        doAutowired();
    }
    private void doAutowired() {
        for(Map.Entry<String,BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                getBean(beanName);
            }
        }
    }

    //实例化BeanWrapper
    public Object getBean(String beanName) {
        BeanDefinition  beanDefinition = this.beanDefinitionMap.get(beanName);
        try{
            //生成通知事件
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();
            Object instance = instantionBean(beanDefinition);
            if(null == instance){ return  null;}
            //在实例初始化以前调用一次,进行通知
            beanPostProcessor.postProcessBeforeInitialization(instance,beanName);
            BeanWrapper beanWrapper = new BeanWrapper(instance);
            beanWrapper.setPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanName,beanWrapper);
            //在实例初始化以后调用一次,进行通知
            beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            populateBean(beanName,instance);
            //先将对象放入到包装对象上，然后返回包装对象上的实例对象
            return this.beanWrapperMap.get(beanName).getWrappedInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 循环对象属性，如果有@Autowired的注解进行对象的注入
     * @param beanName
     * @param instance
     */
    public void populateBean(String beanName,Object instance){
        Class clazz = instance.getClass();
        if(!(clazz.isAnnotationPresent(Controller.class) ||
                clazz.isAnnotationPresent(Service.class))){
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Autowired.class)){ continue; }
            Autowired autowired = field.getAnnotation(Autowired.class);
            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                Object autoBean =  this.beanWrapperMap.get(autowiredBeanName).getWrappedInstance();
                if(autoBean==null){//递归-生成对象注入
                    autoBean = getBean(autowiredBeanName);
                }
                field.set(instance,autoBean);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(false);
        }
    }
    //注册模式单例模式->判断是否是单例-并发下的单例会有问题
    private Object instantionBean(BeanDefinition beanDefinition){
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try{
            //因为根据Class才能确定一个类是否有实例
            if(this.cacheBeanMap.containsKey(className)){
                instance = this.cacheBeanMap.get(className);
            }else{
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.cacheBeanMap.put(className,instance);
            }
            return instance;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private void doRegister(List<String> registerBeanClasses) {
        try{
            for (String className : registerBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                //用它实现类来实例化
                if(beanClass.isInterface()){ continue; }
                BeanDefinition beanDefinition = reader.registerBean(className);
                if(beanDefinition != null){
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
                }
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i: interfaces) {
                    //如果是多个实现类，只能覆盖
                    this.beanDefinitionMap.put(i.getName(),beanDefinition);
                }
                //到这里为止，容器初始化完毕
            }
        }catch (Exception e){
           e.printStackTrace();
        }
    }

}
