package mvc;

import framework.context.ApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    }
    //从handlerAdapters中找到handlerMapping对应的Adapters对象，对参数进行处理
    private void initHandlerAdapters(ApplicationContext context) {
        //先将类的属性进行策略封装，用于接受请求之后，对请求参数进行属性匹配
    }
    //经过方法，返回ModelAndView 对象
    private void initViewResolvers(ApplicationContext context) {

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
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp){
        //1）、根据用户请求的URL来获得一个Handler
        HandlerMapping handler = getHandler(req);
        //2)、根据handlerMapping对象获取handlerAdapter对象
        HandlerAdapter ha = getHandlerAdapter(handler)  ;
        //3)、调用方法，得到ModelAndView返回对象
        ModelAndView modelAndView =  ha.handler(req, resp, handler);
        //4）、将modelAndView对象进行处理输出
        processDispatchResult(resp, modelAndView);
    }


    private HandlerMapping getHandler(HttpServletRequest req) {
        return null;
    }
    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        return null;
    }
    private void processDispatchResult(HttpServletResponse resp, ModelAndView modelAndView) {

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
