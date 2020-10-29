package br.com.boavista.multiscore.services;

import br.com.boavista.multiscore.dto.SocketData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;

@RequiredArgsConstructor
@Slf4j
@MessageEndpoint
public class SocketService {

    private final ServiceExecutor serviceExecutor;

    // We require this incase of any incoming transformations
    @Transformer(inputChannel= "fromMultiScoreTcp", outputChannel= "serviceExecute")
    public SocketData transform(SocketData socketData) {
        log.info("Socket Data transform: {} ", socketData);
        return socketData;
    }

    @ServiceActivator(inputChannel= "serviceExecute")
    public SocketData serviceExecute(SocketData socketData) {
        log.info("Socket serviceExecute: {} ", socketData);
        SocketData data = serviceExecutor.executeService(socketData);
        return data;
    }
}
