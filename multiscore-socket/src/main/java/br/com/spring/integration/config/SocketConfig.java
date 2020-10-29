package br.com.spring.integration.config;

import br.com.spring.integration.serializer.CustomDeserializer;
import br.com.spring.integration.serializer.CustomSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.ThreadAffinityClientConnectionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableIntegration
@IntegrationComponentScan
public class SocketConfig {

    @Value("${socket.host}")
    private String clientSocketHost;

    @Value("${socket.port}")
    private Integer clientSocketPort;

    @Bean
    public TcpOutboundGateway tcpOutGateToMultiScore(AbstractClientConnectionFactory connectionFactory) {
        TcpOutboundGateway gate = new TcpOutboundGateway();
        gate.setConnectionFactory(clientCF());
        return gate;
    }

    @Bean
    public TcpInboundGateway tcpInGateFromMultiScore(AbstractServerConnectionFactory connectionFactory)  {
        TcpInboundGateway inGate = new TcpInboundGateway();
        inGate.setConnectionFactory(connectionFactory);
        inGate.setRequestChannel(fromMultiScoreTcp());
        return inGate;
    }

    @Bean
    public MessageChannel fromMultiScoreTcp() {
        return new DirectChannel();
    }

    // Outgoing requests
    @Bean
    public ThreadAffinityClientConnectionFactory clientCF() {
        TcpNetClientConnectionFactory tcpNetClientConnectionFactory = new TcpNetClientConnectionFactory(clientSocketHost, serverCF().getPort());
        tcpNetClientConnectionFactory.setSingleUse(true);
        ThreadAffinityClientConnectionFactory threadAffinityClientConnectionFactory = new ThreadAffinityClientConnectionFactory(
            tcpNetClientConnectionFactory);
        return threadAffinityClientConnectionFactory;
    }


    // Incoming requests
    @Bean
    public AbstractServerConnectionFactory serverCF() {
//        TcpNioServerConnectionFactory tcpNioServerConnectionFactory = new TcpNioServerConnectionFactory(clientSocketPort);
//        tcpNioServerConnectionFactory.setSingleUse(true);
//        tcpNioServerConnectionFactory.setSerializer(new MultiScoreSerializer());
//        tcpNioServerConnectionFactory.setDeserializer(new MultiScoreDeserializer());
        TcpNetServerConnectionFactory tcpNetServerConnectionFactory = new TcpNetServerConnectionFactory(clientSocketPort);
        tcpNetServerConnectionFactory.setSerializer(new CustomSerializer());
        tcpNetServerConnectionFactory.setDeserializer(new CustomDeserializer());
        tcpNetServerConnectionFactory.setSingleUse(true);
        tcpNetServerConnectionFactory.setTaskExecutor(taskExecutor());
        // Below command gives the list of open connection ids
        // we can close a specific connection as well.
//        tcpNetServerConnectionFactory.getOpenConnectionIds();
//        tcpNioServerConnectionFactory.setTaskExecutor(taskExecutor());
        return tcpNetServerConnectionFactory;
    }


    @Bean
    public TaskExecutor taskExecutor () {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(50);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(120);
        return executor;
    }

}
