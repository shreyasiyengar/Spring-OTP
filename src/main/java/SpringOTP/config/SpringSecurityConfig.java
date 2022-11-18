package SpringOTP.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.authentication.UserServiceBeanDefinitionParser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

import SpringOTP.Handler.CustomAccessDeniedHandler;



@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfiguration{

//	@Autowired
	private AccessDeniedHandler accessaDeniedHandler;
	
	@Autowired
	private UserDetailsService usersService; 
	
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable().authorizeRequests().antMatchers("/","/aboutus").permitAll()
		.antMatchers("/admin/**").hasAnyRole("ADMIN")//ONLY admin user can login
		.antMatchers("/user/**").hasRole("USER")//only normal user can login
		.anyRequest().authenticated() //Rest of all request need authentication
		.and()
		.formLogin()
		.loginPage("/login") //loginform all can access....
		.defaultSuccessUrl("/dashboard")
		.failureUrl("/login?error")
		.permitAll()
		.and()
		.logout()
		.permitAll()
		.and()
		
		.exceptionHandling().accessDeniedHandler(accessDeniedHandler());
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		auth.userDetailsService(usersService).passwordEncoder(passwordEncoder);
	}
	
	public AccessDeniedHandler accessDeniedHandler() {
		return new CustomAccessDeniedHandler();
	}
	
}
