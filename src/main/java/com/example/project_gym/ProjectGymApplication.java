package com.example.project_gym;

import com.example.project_gym.config.ApplicationConfig;
import com.example.project_gym.config.PersistenceConfig;
import com.example.project_gym.config.WebMvcConfig;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class ProjectGymApplication {
    public static void main(String[] args) {
        try (AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext()) {
            rootContext.register(ApplicationConfig.class, PersistenceConfig.class);
            rootContext.refresh();
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8080);

            String docBase = new File(".").getAbsolutePath();
            Context tomcatContext = tomcat.addContext("", docBase);

            AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
            webContext.setParent(rootContext);
            webContext.register(WebMvcConfig.class);

            DispatcherServlet dispatcherServlet = new DispatcherServlet(webContext);
            Tomcat.addServlet(tomcatContext, "dispatcher", dispatcherServlet).setLoadOnStartup(1);
            tomcatContext.addServletMappingDecoded("/", "dispatcher");

            tomcat.getConnector();
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }
}