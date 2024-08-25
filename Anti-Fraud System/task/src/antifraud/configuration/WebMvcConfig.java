package antifraud.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class WebMvcConfig {

    @Bean
    public RequestMappingHandlerMappingPostProcessor requestMappingHandlerMappingPostProcessor() {
        return new RequestMappingHandlerMappingPostProcessor();
    }

    public static class RequestMappingHandlerMappingPostProcessor implements BeanPostProcessor {

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof RequestMappingHandlerMapping) {
                ((RequestMappingHandlerMapping) bean).setUseSuffixPatternMatch(false);
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }
}
