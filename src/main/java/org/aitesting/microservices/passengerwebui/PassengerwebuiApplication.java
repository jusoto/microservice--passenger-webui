package org.aitesting.microservices.passengerwebui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
//@EnableRedisHttpSession
public class PassengerwebuiApplication extends /*WebSecurityConfigurerAdapter*/ SpringBootServletInitializer {
 
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PassengerwebuiApplication.class);
    }
	
//	@Bean
//	public RestTemplate restTemplate(RestTemplateBuilder builder) {
//		return builder.build();
//	}

	public static void main(String[] args) {
		SpringApplication.run(PassengerwebuiApplication.class, args);
	}

//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//	    http.httpBasic().disable();
//	    http.authorizeRequests().anyRequest().authenticated();
//	}
}
