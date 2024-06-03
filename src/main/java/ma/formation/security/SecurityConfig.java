package ma.formation.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import lombok.AllArgsConstructor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    // Bean pour AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Bean pour AuthenticationProvider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    // Configuration du filtre de sécurité
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        configureHttpSecurity(http);
        return http.build();
    }

    // Méthode pour configurer HttpSecurity
    private void configureHttpSecurity(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);

        configureAuthorization(http);
        configureFormLogin(http);
        configureExceptionHandling(http);
        configureLogout(http);
        configureRememberMe(http);

        http.authenticationProvider(authenticationProvider());
    }

    // Configuration des autorisations
    private void configureAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/resources/**"),
                    new AntPathRequestMatcher("/webjars/**"),
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/fonts/**"),
                    new AntPathRequestMatcher("/webfonts/**"),
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/resources/**"),
                    new AntPathRequestMatcher("/static/**"),
                    new AntPathRequestMatcher("/plugins/**"),
                    new AntPathRequestMatcher("/about_us"),
                    new AntPathRequestMatcher("/contact_us"),
                    new AntPathRequestMatcher("/reset_password"),
                    new AntPathRequestMatcher("/webjars/**"),
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/frontendold/**"),
                    new AntPathRequestMatcher("/frontend/**"),
                    new AntPathRequestMatcher("/backend/**")
            ).permitAll();

            auth.requestMatchers(new AntPathRequestMatcher("/admin/**")).hasAnyAuthority("ADMIN");
            auth.requestMatchers(new AntPathRequestMatcher("/user/**")).hasAnyAuthority("USER");

            auth.anyRequest().authenticated();
        });
    }

    // Configuration de la page de connexion
    private void configureFormLogin(HttpSecurity http) throws Exception {
        http.formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/user/index", true)
                .permitAll());
    }

    // Configuration de la gestion des exceptions
    private void configureExceptionHandling(HttpSecurity http) throws Exception {
        http.exceptionHandling(exception -> exception
                .accessDeniedPage("/403"));
    }

    // Configuration de la déconnexion
    private void configureLogout(HttpSecurity http) throws Exception {
        http.logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll());
    }

    // Configuration de l'option "Remember Me"
    private void configureRememberMe(HttpSecurity http) throws Exception {
        http.rememberMe()
                .key("uniqueAndSecretKey") // Clé secrète pour signer les tokens Remember Me
                .rememberMeParameter("remember-me") // Paramètre du formulaire pour le Remember Me
                .tokenValiditySeconds(86400); // Durée de validité du token Remember Me en secondes (ici, 24 heures)
    }
}
