package mvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class HandlerMapping {
    private Pattern pattern; //url正则对应
    private Object controller;//controller对象
    private Method method;//controller对象上的方法
    public HandlerMapping(Pattern pattern,Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
