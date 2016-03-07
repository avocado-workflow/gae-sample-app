package demo;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
public class Application {
	
	@Value("${info.version}")
	private String version;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @RequestMapping("/")
    public String home() {
    	return "Hello World 5";
    }

    @RequestMapping("/version")
    public String getVersion() {
    	return version;
    }
    
    @RequestMapping("/timezone")
    public String time() {
    	return Calendar.getInstance().getTimeZone().toString();
    }
    
//    @Bean
//    public ServletContextInitializer initializer() {
//        return new ServletContextInitializer() {
//
//            @Override
//            public void onStartup(ServletContext servletContext) throws ServletException {
//            	System.out.println("#################");
//            	System.out.println("#################");
//            	System.out.println("#################");
//            	System.out.println("#################");
//            	System.out.println("#################");
//            	ObjectifyService.register(Product.class);
//            }
//        };
//    }

//    @Bean
//    public Filter objectifyFilter() {
//    	return new ObjectifyFilter();
//    }
//    @Bean
//    public FilterRegistrationBean objectifyFilterRegistration() {
//    	FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new ObjectifyFilter());
//        registration.addUrlPatterns("/*");
//        registration.setName("ObjectifyFilterJAVA");
//        return registration;
//    }
}
