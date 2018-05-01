package mvc;

import framework.annotation.Controller;
import framework.annotation.RequestMapping;
import framework.annotation.RequestParam;
import framework.context.ApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatchServlet extends HttpServlet {
    private  final String LOCATION = "contextConfigLocation";
    //集合的设计经典之处？
    private List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();
    private Map<HandlerMapping,HandlerAdapter> handlerAdapters = new HashMap<HandlerMapping,HandlerAdapter>();
    private List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();
    /**
     * 1、配置阶段：配置web.xml的相关信息
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        ApplicationContext context = new ApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context);
    }
    /**
     * 2、初始化阶段：
     * springMVC 九大组件的初始化
     */
    private void initStrategies(ApplicationContext context) {
        initMultipartResolver(context);//文件上传解析，如果请求类型是multipart将通过MultipartResolver进行文件上传解析
        initLocaleResolver(context);//本地化解析
        initThemeResolver(context);//主题解析
        /** 自定义实现 */
        initHandlerMappings(context);//通过HandlerMapping，将请求映射到处理器
        /** 自定义实现 */
        initHandlerAdapters(context);//通过HandlerAdapter进行多类型的参数动态匹配
        initHandlerExceptionResolvers(context);//如果执行过程中遇到异常，将交给HandlerExceptionResolver来解析
        initRequestToViewNameTranslator(context);//直接解析请求到视图名
        /** 自定义会实现 */
        //通过ViewResolvers实现动态模板的解析
        //自己解析一套模板语言
        initViewResolvers(context);//通过viewResolver解析逻辑视图到具体视图实现
        initFlashMapManager(context);//flash映射管理器
    }
    //封装成List<HandlerMapping> 集合对象，requestMapping和method对应
    private void initHandlerMappings(ApplicationContext context) {
        String [] beanNames = context.getBeanDefinitionNames();
        //循环IOC容器中所有的bean
        for (String beanName : beanNames){
            Object controller = context.getBean(beanName);
            Class controllerClazz = controller.getClass();
            if(!controllerClazz.isAnnotationPresent(Controller.class)){//不是@Controller注解的实例
                continue;
            }
            String baseUrl = "";
            if(controllerClazz.isAnnotationPresent(RequestMapping.class)){//有@RequestMapping的注解
                RequestMapping requestMapping  = (RequestMapping)controllerClazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //反射获取到类的所有方法和方法上的requestMapping路径
            Method[] methods = controllerClazz.getMethods();
            for (Method method: methods) {
                if(!controllerClazz.isAnnotationPresent(RequestMapping.class)){continue;}
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String regex = ("/" + baseUrl +requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+","/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new HandlerMapping(pattern,controller,method));
            }
        }
        //requestMapping映射url映射controller及方法完成
    }
    //从handlerAdapters中找到handlerMapping对应的Adapters对象，对参数进行处理
    private void initHandlerAdapters(ApplicationContext context) {
        //先将类的属性进行策略封装，用于接受请求之后，对请求参数进行属性匹配
        //循环handlerMapping的集合对象，将Method中的方法取出来，形参进行赋值
        for(HandlerMapping mapping:this.handlerMappings){
            //形参的map集合，key->形参的名称，value->形参的位置
            Map<String,Integer> paramMapping = new HashMap<String,Integer>();
            //获取注解的自定义方法参数
            Annotation[][] pa = mapping.getMethod().getParameterAnnotations();
            for (int i = 0; i < pa.length ; i ++) {
                for (Annotation a : pa[i]) {
                    if(a instanceof RequestParam){
                        String paramName = ((RequestParam) a).value();
                        if(!"".equals(paramName.trim())){
                            paramMapping.put(paramName,i);
                        }
                    }
                }
            }
            Class<?>[] paramTypes = mapping.getMethod().getParameterTypes();
            for (int i = 0;i < paramTypes.length; i ++) {
                Class<?> type = paramTypes[i];
                if(type == HttpServletRequest.class ||
                        type == HttpServletResponse.class){
                    paramMapping.put(type.getName(),i);
                }
            }
            this.handlerAdapters.put(mapping,new HandlerAdapter(paramMapping));
        }
    }
    //经过方法，返回ModelAndView 对象
    private void initViewResolvers(ApplicationContext context) {
        //将目录下的模板文件，封装map集合 key->模板文件名称，value->模板文件
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File template : templateRootDir.listFiles()) {
            this.viewResolvers.add(new ViewResolver(template.getName(),template));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    /**
     *3、请求处理阶段
     *
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws  Exception{
        //1）、根据用户请求的URL来获得一个Handler
        HandlerMapping handler = getHandler(req);
        //2)、根据handlerMapping对象获取handlerAdapter对象
        HandlerAdapter ha = getHandlerAdapter(handler)  ;
        //3)、调用方法，得到ModelAndView返回对象
        ModelAndView modelAndView =  ha.handler(req, resp, handler);
        //4）、将modelAndView对象进行处理输出
        processDispatchResult(resp, modelAndView);
    }
    //通过解析请求的url路径，循环集合handlerMapping的对象匹配正则
    private HandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()){ return  null;}
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");
        for (HandlerMapping handler : this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()){ continue;}
            return handler;
        }
        return null;
    }
    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){return  null;}
        return this.handlerAdapters.get(handler);
    }
    private void processDispatchResult(HttpServletResponse resp, ModelAndView modelAndView) throws Exception {
        //匹配集合中的模板页面，取出模板，调用viewResolver的resolveView方法进行页面处理
        if(null == modelAndView){ return;}
        if(this.viewResolvers.isEmpty()){ return;}
        for (ViewResolver viewResolver: this.viewResolvers) {
            if(!modelAndView.getViewName().equals(viewResolver.getViewName())){ continue; }
            String out = viewResolver.viewResolver(modelAndView);
            if(out != null){
                resp.getWriter().write(out);
                break;
            }
        }
    }
    /**
     * 以下先不自定义
     */
    private void initFlashMapManager(ApplicationContext context) {
    }
    private void initRequestToViewNameTranslator(ApplicationContext context) {
    }
    private void initHandlerExceptionResolvers(ApplicationContext context) {
    }
    private void initThemeResolver(ApplicationContext context) {
    }
    private void initLocaleResolver(ApplicationContext context) {
    }
    private void initMultipartResolver(ApplicationContext context) {
    }
}
