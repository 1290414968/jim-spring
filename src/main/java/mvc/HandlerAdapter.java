package mvc;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

/**
 * 设计模式的经典使用
 */
public class HandlerAdapter {
    private Map<String,Integer> paramMapping;
    public HandlerAdapter(Map<String,Integer> paramMapping){
        this.paramMapping = paramMapping;
    }
    public ModelAndView handler(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handler) throws InvocationTargetException, IllegalAccessException {
        //1、方法的所有参数类型
        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();
        //2、页面url传递过来的参数集合map
        Map<String,String[]> reqParameterMap = req.getParameterMap();
        //3、构造对应的形参赋值数组对象
        Object [] paramValues = new Object[paramTypes.length];
        //循环页面的集合参数对象
        for (Map.Entry<String,String[]> param : reqParameterMap.entrySet()) {
            //参数值
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]","").replaceAll("\\s","");
            //参数名称在paramMapping中不存在
            if(!this.paramMapping.containsKey(param.getKey())){continue;}
            int index = this.paramMapping.get(param.getKey());
            //因为页面上传过来的值都是String类型的，而在方法中定义的类型是千变万化的
            //要针对我们传过来的参数进行类型转换
            paramValues[index] = caseStringValue(value,paramTypes[index]);
        }
        if(this.paramMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = this.paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }
        if(this.paramMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = this.paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }
        //反射调用controller的方法，并返回对象
        Object result =  handler.getMethod().invoke(handler.getController(),paramValues);
        if(result == null){ return  null; }
        boolean isModelAndView = handler.getMethod().getReturnType() == ModelAndView.class;
        if(isModelAndView){
            return (ModelAndView)result;
        }else{
            return null;
        }
    }
    private Object caseStringValue(String value,Class<?> clazz){
        if(clazz == String.class){
            return value;
        }else if(clazz == Integer.class){
            return  Integer.valueOf(value);
        }else if(clazz == int.class){
            return Integer.valueOf(value).intValue();
        }else {
            return null;
        }
    }
}
