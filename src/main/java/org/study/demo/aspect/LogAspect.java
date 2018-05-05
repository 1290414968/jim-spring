package org.study.demo.aspect;
public class LogAspect {
    //切面方法，在目标方法执行之前调用
    public void before(){
        System.out.println("Invoker Before Method!!!");
    }
    //切面方法，在目标方法执行之后调用
    public void after(){
        System.out.println("Invoker After Method!!!");
    }
}
