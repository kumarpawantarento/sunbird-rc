package dev.sunbirdrc.claim.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);
    @Override
    protected void configure(HttpSecurity http) throws Exception {

//        HttpSecurity httpConfig = http.csrf().disable();
//        httpConfig.authorizeRequests()
//                .anyRequest()
//                .permitAll();

//        http.cors()
//                .and()
        http.csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/**/**", "/**/keycloak/login", "**/auth/realms/sunbird-rc/protocol/openid-connect/token")
//                .permitAll()

//                .and()
                .authorizeRequests()
                .antMatchers("/**/keycloak/**", "/**/keycloak/**/**")
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();
    }
}
