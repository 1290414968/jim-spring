package framework.beans;

import framework.core.FactoryBean;

/**
 * @author jim
 * @create 2018-04-25 19:40
 **/
public class BeanWrapper extends FactoryBean {
    //还会用到  观察者  模式
    //支持事件响应，会有一个监听
    private BeanPostProcessor postProcessor;
    private Object wrapperInstance;
    //原始的通过反射new出来，要把包装起来，存下来
    private Object originalInstance;
    public BeanWrapper(Object instance){
        this.wrapperInstance = instance;
        this.originalInstance = instance;
    }
    public BeanPostProcessor getPostProcessor() {
        return postProcessor;
    }
    public void setPostProcessor(BeanPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }
    public Object getWrappedInstance(){
        return this.wrapperInstance;
    }
    // 返回代理以后的Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass(){
        return this.wrapperInstance.getClass();
    }
}
