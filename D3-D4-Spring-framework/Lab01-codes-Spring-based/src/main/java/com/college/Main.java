package com.college;

import com.college.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {

        // Start the Spring Container
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        // Retrieve College bean
        College college = context.getBean(College.class);

        // Use the bean
        college.displayCollegeInformation();

        // Close the Spring Container
        context.close();
    }
}