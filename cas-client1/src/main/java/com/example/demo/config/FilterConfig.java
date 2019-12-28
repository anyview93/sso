package com.example.demo.config;

import com.example.demo.filter.AuthFilter;
import com.example.demo.filter.LogoutFilter;
import com.example.demo.filter.TicketValidateFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @author shizhiguo
 * @ClassName FilterConfig
 * @date 2019-03-24
 */
@Configuration
public class FilterConfig {

    @Bean
    public Filter authFilter() {
        return new AuthFilter();
    }

    @Bean
    public Filter ticketValidateFilter() {
        return new TicketValidateFilter();
    }

    @Bean
    public Filter logoutFilter() {
        return new LogoutFilter();
    }

    @Bean
    public FilterRegistrationBean<Filter> authFilterRegistration() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(authFilter());
        filterFilterRegistrationBean.addUrlPatterns("/*");
        filterFilterRegistrationBean.setOrder(1);
        return filterFilterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> ticketValidateFilterRegistration() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(ticketValidateFilter());
        filterFilterRegistrationBean.addUrlPatterns("/*");
        filterFilterRegistrationBean.setOrder(2);
        return filterFilterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> logoutFilterRegistration() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(logoutFilter());
        filterFilterRegistrationBean.addUrlPatterns("/logout");
        filterFilterRegistrationBean.setOrder(3);
        return filterFilterRegistrationBean;
    }
}
