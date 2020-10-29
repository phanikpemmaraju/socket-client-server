package br.com.boavista.multiscore.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Slf4j
@Service
public class StartupApplicationRunner implements ApplicationRunner {

    private final BuildProperties buildProperties;

    @Override
    public void run(ApplicationArguments args) {
        String tagName = System.getProperty("tagName");
        log.info("Started MultiScore Socket Application with build version: {} ", buildProperties.getVersion());
        if(!StringUtils.isEmpty(tagName))
            log.info("Application Tag Name: {} ", tagName);
    }
}
