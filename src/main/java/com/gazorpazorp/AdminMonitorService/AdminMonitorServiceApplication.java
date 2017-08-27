package com.gazorpazorp.AdminMonitorService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.core.Base64Variants;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import de.codecentric.boot.admin.config.EnableAdminServer;

@SpringBootApplication
@EnableAdminServer
@EnableEurekaClient
public class AdminMonitorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminMonitorServiceApplication.class, args);
	}
	
	@Configuration
	public static class SecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// Page with login form is served as /login.html and does a POST on /login
			http.formLogin().loginPage("/login.html").loginProcessingUrl("/login").permitAll();
			// The UI does a POST on /logout on logout
			http.logout().logoutUrl("/logout");
			// The ui currently doesn't support csrf
			http.csrf().disable();

			// Requests for the login page and the static assets are allowed
			http.authorizeRequests()
					.antMatchers("/login.html", "/**/*.css", "/img/**", "/third-party/**")
					.permitAll();
			// ... and any other request needs to be authorized
			http.authorizeRequests().antMatchers("/**").authenticated();

			// Enable so that the clients can authenticate via HTTP basic for registering
			http.httpBasic();
		}
	}
}
