package br.com.spring.integration.services;

import br.com.spring.integration.dto.SocketData;
import org.springframework.stereotype.Service;

@Service
public class ServiceExecutor {

    public SocketData executeService(final SocketData socketData) {
        // Call Program factory.
        return socketData;
    }
}
