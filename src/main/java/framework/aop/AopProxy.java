package framework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//默认就用JDK动态代理
public class AopProxy implements InvocationHandler{

    private AopConfig config;
    //target为原生对象
    private Object target;

    //把原生的对象传进来，生成代理对象返回
    public Object getProxy(Object instance){
        this.target = instance;
        Class<?> clazz = instance.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(),clazz.getInterfaces(),this);
    }
    public void setConfig(AopConfig config){
        //在ApplicationContext解析配置文件的封装成config对象传递进来
        this.config = config;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //获取原生对象的方法
        Method m = this.target.getClass().getMethod(method.getName(),method.getParameterTypes());
        //在原始方法调用以前要执行增强的代码
        AopConfig.GPAspect aspect = null;
        if(config.contains(m)){
            aspect = config.get(m);
        }
        aspect.getPoints()[0].invoke(aspect);
        //反射调用原始的方法
        Object obj = method.invoke(this.target,args);
        //在原始方法调用以后要执行增强的代码
        aspect.getPoints()[1].invoke(aspect);
        //将最原始的返回值返回出去
        return obj;
    }
}
