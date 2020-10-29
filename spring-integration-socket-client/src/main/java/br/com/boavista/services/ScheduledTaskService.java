package br.com.boavista.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Lazy(false)
@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTaskService {

    // Timeout in milliseconds
    private static final int SOCKET_TIME_OUT = 18000;
    private static final int BUFFER_SIZE = 32000;
    private static final int ETX = 0x03;
    private static final String PARAMETROS_STRING_MAINFRAME = "SOCKETY             ";
    private static final String envio = "SR010080000000AC035964  1172517600012708032016103233F00000113789050CINI                0000000099999999999                                                                                                                                                      O03                                                                                                                                     SCRPO001VP0100000000F00000113789050                                                                                                                                                                             TR990000                                                                                                                                                                                                ";
    private final AtomicInteger atomicInteger = new AtomicInteger();

    @Async
    @Scheduled(fixedDelay = 100000)
    public void sendEnvioMessage() throws IOException, InterruptedException {
        int numberOfRequests = 10;

        Callable<String> executeMultipleSuccessfulRequestTask = () -> socketSendNReceive();

        final Collection<Callable<String>> callables = new ArrayList<>();
        IntStream.rangeClosed(1, numberOfRequests).forEach(i-> {
            callables.add(executeMultipleSuccessfulRequestTask);
        });
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);

        List<Future<String>> taskFutureList = executorService.invokeAll(callables);
        List<String> strings = taskFutureList.stream().map(future -> {
            try {
                return future.get(20000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return "";
        }).collect(Collectors.toList());

        strings.forEach(string -> log.info("Message received from the server: {} ", string));

    }

    public String socketSendNReceive() throws IOException{
        int requestCounter = atomicInteger.incrementAndGet();

        String host = "localhost";
        int port = 8000;

        Socket socket = new Socket();
        InetSocketAddress address = new InetSocketAddress(host, port);
        socket.connect(address, SOCKET_TIME_OUT);
        socket.setSoTimeout(SOCKET_TIME_OUT);

        //Send the message to the server
        OutputStream os = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);

        bos.write(PARAMETROS_STRING_MAINFRAME.getBytes());
        bos.write(envio.getBytes());
        bos.write(ETX);
        bos.flush();
//        log.info("Message sent to the server : {} ",  envio);

        //Get the return message from the server
        InputStream is = socket.getInputStream();
        String response =  receber(is);
        log.info("Received response");
        return response;
    }

    private String receber(InputStream in) throws IOException {
        log.info("iniciando");
        final StringBuffer resposta = new StringBuffer();
        int readLength;
        byte[] buffer;
        buffer = new byte[BUFFER_SIZE];
        do {
            if(Objects.nonNull(in)) {
                log.info("Input Stream not null");
            }
            readLength = in.read(buffer);
            log.info("readLength : {}  ", readLength);
            if(readLength > 0){
                resposta.append(new String(buffer),0,readLength);
                log.info("String recebida");
            }
        } while (buffer[readLength-1] != ETX);
        buffer = null;
        resposta.deleteCharAt(resposta.length()-1);
        return resposta.toString();
    }
}
