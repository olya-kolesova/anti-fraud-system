package antifraud.configuration;

import antifraud.service.AppUserService;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;


import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
public class SecurityConfig {

    @Autowired
    private final AppUserService appUserService;

    public SecurityConfig(AppUserService appUserService) {
        this.appUserService = appUserService;
    }


    @Bean
    public static BeanPostProcessor clearDataSourceUserName() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof HikariDataSource) {
                    ((HikariDataSource) bean).setUsername("");
                }
                return bean;
            }

        };
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(appUserService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }


    @Bean
    public SecurityFilterChain SecurityfilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(withDefaults())
                .csrf(CsrfConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests((authorize) -> authorize
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers("/actuator/shutdown").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                        .requestMatchers("/api/auth/list").hasAnyAuthority("ADMINISTRATOR", "SUPPORT")
                        .requestMatchers(HttpMethod.DELETE, "/api/auth/user/{username}").hasAuthority("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasAuthority("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/auth/access").hasAuthority("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/suspicious-ip").hasAuthority("SUPPORT")
                        .requestMatchers(HttpMethod.DELETE, "/api/antifraud/suspicious-ip/{ip}").hasAuthority("SUPPORT")
                        .requestMatchers(HttpMethod.GET, "/api/antifraud/suspicious-ip").hasAuthority("SUPPORT")
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/stolencard").hasAuthority("SUPPORT")
                        .requestMatchers(HttpMethod.DELETE, "/api/antifraud/stolencard/{number}").hasAuthority("SUPPORT")
                        .requestMatchers(HttpMethod.GET, "/api/antifraud/stolencard").hasAuthority("SUPPORT")
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasAuthority("MERCHANT"))
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));




        return http.build();

    }

//    @Bean
//    public SecurityFilterChain transactionSecurityFilterchain(HttpSecurity http) throws Exception {
//        http
//                .csrf(CsrfConfigurer::disable)
//                .headers(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests((authorize) -> authorize
//                    .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
//                    .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasAuthority("MERCHANT"))
//                .httpBasic().authenticationEntryPoint(authenticationEntryPoint());
//
//        return http.build();
//
//    }
//
//    @Bean
//    public AuthenticationEntryPoint authenticationEntryPoint() {
//        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
//        entryPoint.setRealmName("certificate realm");
//        return entryPoint;
//    }


}
