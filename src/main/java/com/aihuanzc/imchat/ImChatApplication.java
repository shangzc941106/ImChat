package com.aihuanzc.imchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class ImChatApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(ImChatApplication.class, args);
    }

}
