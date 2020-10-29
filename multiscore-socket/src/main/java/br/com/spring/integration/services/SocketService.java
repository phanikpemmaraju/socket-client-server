package br.com.spring.integration.services;

import br.com.spring.integration.dto.SocketData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;

@MessageEndpoint
public class SocketService {

    @Autowired
    private ServiceExecutor serviceExecutor;

    // We require this incase of any incoming transformations
    @Transformer(inputChannel= "fromMultiScoreTcp", outputChannel= "serviceExecute")
    public SocketData transform(SocketData socketData) {
        return socketData;
    }

    @ServiceActivator(inputChannel= "serviceExecute")
    public SocketData serviceExecute(SocketData socketData) {
        SocketData data = serviceExecutor.executeService(socketData);
        return data;
    }
}
