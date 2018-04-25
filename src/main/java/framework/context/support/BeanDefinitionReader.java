package framework.context.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author jim
 * @create 2018-04-25 19:40
 **/
public class BeanDefinitionReader {
    private  Properties config = new Properties();
    public BeanDefinitionReader(String ... locations) {
        //在Spring中是通过Reader去查找和定位对不对
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != is){is.close();}
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
