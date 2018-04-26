package framework.beans;

/**
 * @author jim
 * @create 2018-04-25 19:40
 **/
//配置文件的信息转换成BeanDefinition对象
public class BeanDefinition {
    private String beanClassName;//bean类的全称名称
    private boolean lazyInit = false;//是否懒加载
    private String factoryBeanName;//bean类的名称

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
