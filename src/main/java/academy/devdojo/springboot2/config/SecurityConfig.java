package academy.devdojo.springboot2.config;

import academy.devdojo.springboot2.service.DevDojoUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final DevDojoUserDetailsService devDojoUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
//            Usamos o csrf pra adicionar uma camada a mais de seguraça. Evitando chamadas que não tenham o
//            token da chamada anterior
//            .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//            .and()
            .authorizeRequests()
            .antMatchers("/animes/admin/**").hasRole("ADMIN")
            .antMatchers("/animes/**").hasRole("USER")
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        log.info("Password encoded {}", passwordEncoder.encode("test"));

        auth.inMemoryAuthentication()
                .withUser("fabio2")
                .password(passwordEncoder.encode("password"))
                .roles("USER", "ADMIN")
                .and()
                .withUser("test2")
                .password(passwordEncoder.encode("test"))
                .roles("USER");

        auth.userDetailsService(devDojoUserDetailsService)
                .passwordEncoder(passwordEncoder);
    }

}
