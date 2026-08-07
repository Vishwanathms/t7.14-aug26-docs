package com.college.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.college.config.AppConfig;
import com.college.config.WebConfig;

/**
 * Replaces web.xml. Wires the root context (service/DAO beans from
 * AppConfig) and the DispatcherServlet context (MVC beans from WebConfig).
 */
public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) throws ServletException {

        // Root Spring context: service + DAO layer
        AnnotationConfigWebApplicationContext rootContext =
                new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);
        container.addListener(new ContextLoaderListener(rootContext));

        // MVC context: controllers + view resolver
        AnnotationConfigWebApplicationContext webContext =
                new AnnotationConfigWebApplicationContext();
        webContext.register(WebConfig.class);

        ServletRegistration.Dynamic dispatcher =
                container.addServlet("dispatcher", new DispatcherServlet(webContext));

        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }

}
