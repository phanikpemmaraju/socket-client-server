package br.com.spring.integration.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

@Service
public class StartupApplicationRunner implements ApplicationRunner {

    @Autowired
    private BuildProperties buildProperties;

    @Override
    public void run(ApplicationArguments args) {
        String tagName = System.getProperty("tagName");
    }
}
