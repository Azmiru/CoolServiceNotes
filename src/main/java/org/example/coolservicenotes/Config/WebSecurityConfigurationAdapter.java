package org.example.coolservicenotes.Config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface WebSecurityConfigurationAdapter {
    void cofigure(HttpSecurity http) throws Exception;
}
