package com.h2rd.refactoring.usermanagement.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Context {
    private static ApplicationContext context ;

    public static ApplicationContext getContext(){
        if(context == null) context = new ClassPathXmlApplicationContext("application-config.xml");
        return context;
    }

}
