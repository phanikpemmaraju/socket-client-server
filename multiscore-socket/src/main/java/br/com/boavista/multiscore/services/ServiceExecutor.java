package br.com.boavista.multiscore.services;

import br.com.boavista.multiscore.dto.SocketData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@RequiredArgsConstructor
@Slf4j
@Service
public class ServiceExecutor {

    private final DataSource dataSource;

    public SocketData executeService(final SocketData socketData) {
        // Call Program factory.
        log.info("Execute Service : {}",socketData);
        return socketData;
    }
}
