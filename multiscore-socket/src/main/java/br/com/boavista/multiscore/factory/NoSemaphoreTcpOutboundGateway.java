package br.com.boavista.multiscore.factory;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.integration.MessageTimeoutException;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpConnectionFailedCorrelationEvent;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NoSemaphoreTcpOutboundGateway extends TcpOutboundGateway {

    private volatile AbstractClientConnectionFactory connectionFactory;
    private final Map<String, AsyncReply> pendingReplies = new ConcurrentHashMap();

    @Override
    public boolean onMessage(Message<?> message) {
        String connectionId = (String)message.getHeaders().get("ip_connectionId");
        if(connectionId == null) {
            this.logger.error("Cannot correlate response - no connection id");
            this.publishNoConnectionEvent(message, (String)null, "Cannot correlate response - no connection id");
            return false;
        }

        if(this.logger.isTraceEnabled()) {
            this.logger.trace("onMessage: " + connectionId + "(" + message + ")");
        }

        NoSemaphoreTcpOutboundGateway.AsyncReply reply = (NoSemaphoreTcpOutboundGateway.AsyncReply)this.pendingReplies.get(connectionId);
        if(reply == null) {
            if(message instanceof ErrorMessage) {
                return false;
            } else {
                String errorMessage = "Cannot correlate response - no pending reply for " + connectionId;
                this.logger.error(errorMessage);
                this.publishNoConnectionEvent(message, connectionId, errorMessage);
                return false;
            }
        } else {
            reply.setReply(message);
            return false;
        }

    }

    @Override
    protected Message handleRequestMessage(Message<?> requestMessage) {
        connectionFactory = (AbstractClientConnectionFactory) this.getConnectionFactory();
        Assert.notNull(this.getConnectionFactory(), this.getClass().getName() + " requires a client connection factory");

        TcpConnection connection = null;
        String connectionId = null;

        Message var7;
        try {
            /*if(!this.isSingleUse()) {
                this.logger.debug("trying semaphore");
                if(!this.semaphore.tryAcquire(this.requestTimeout, TimeUnit.MILLISECONDS)) {
                    throw new MessageTimeoutException(requestMessage, "Timed out waiting for connection");
                }

                haveSemaphore = true;
                if(this.logger.isDebugEnabled()) {
                    this.logger.debug("got semaphore");
                }
            }*/

            connection = this.getConnectionFactory().getConnection();
            NoSemaphoreTcpOutboundGateway.AsyncReply e = new NoSemaphoreTcpOutboundGateway.AsyncReply(10000);
            connectionId = connection.getConnectionId();
            this.pendingReplies.put(connectionId, e);
            if(this.logger.isDebugEnabled()) {
                this.logger.debug("Added pending reply " + connectionId);
            }

            connection.send(requestMessage);

            //connection may be closed after send (in interceptor) if its disconnect message
            if (!connection.isOpen())
                return null;

            Message replyMessage = e.getReply();
            if(replyMessage == null) {
                if(this.logger.isDebugEnabled()) {
                    this.logger.debug("Remote Timeout on " + connectionId);
                }

                this.connectionFactory.forceClose(connection);
                throw new MessageTimeoutException(requestMessage, "Timed out waiting for response");
            }

            if(this.logger.isDebugEnabled()) {
                this.logger.debug("Response " + replyMessage);
            }

            var7 = replyMessage;
        } catch (Exception var11) {
            this.logger.error("Tcp Gateway exception", var11);
            if(var11 instanceof MessagingException) {
                throw (MessagingException)var11;
            }

            throw new MessagingException("Failed to send or receive", var11);
        } finally {
            if(connectionId != null) {
                this.pendingReplies.remove(connectionId);
                if(this.logger.isDebugEnabled()) {
                    this.logger.debug("Removed pending reply " + connectionId);
                }
            }
        }
        return var7;
    }

    private void publishNoConnectionEvent(Message<?> message, String connectionId, String errorMessage) {
        ApplicationEventPublisher applicationEventPublisher = this.connectionFactory.getApplicationEventPublisher();
        if(applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(new TcpConnectionFailedCorrelationEvent(this, connectionId, new MessagingException(message, errorMessage)));
        }
    }

    private final class AsyncReply {
        private final CountDownLatch latch;
        private final CountDownLatch secondChanceLatch;
        private final long remoteTimeout;
        private volatile Message<?> reply;

        private AsyncReply(long remoteTimeout) {
            this.latch = new CountDownLatch(1);
            this.secondChanceLatch = new CountDownLatch(1);
            this.remoteTimeout = remoteTimeout;
        }

        public Message<?> getReply() throws Exception {
            try {
                if(!this.latch.await(this.remoteTimeout, TimeUnit.MILLISECONDS)) {
                    return null;
                }
            } catch (InterruptedException var2) {
                Thread.currentThread().interrupt();
            }
            for(boolean waitForMessageAfterError = true; this.reply instanceof ErrorMessage; waitForMessageAfterError = false) {
                if(!waitForMessageAfterError) {
                    if(this.reply.getPayload() instanceof MessagingException) {
                        throw (MessagingException)this.reply.getPayload();
                    }

                    throw new MessagingException("Exception while awaiting reply", (Throwable)this.reply.getPayload());
                }
                NoSemaphoreTcpOutboundGateway.this.logger.debug("second chance");
                this.secondChanceLatch.await(2L, TimeUnit.SECONDS);
            }
            return this.reply;
        }

        public void setReply(Message<?> reply) {
            if(this.reply == null) {
                this.reply = reply;
                this.latch.countDown();
            } else if(this.reply instanceof ErrorMessage) {
                this.reply = reply;
                this.secondChanceLatch.countDown();
            }
        }
    }

}
