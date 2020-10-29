package br.com.boavista.multiscore.serializer;

import br.com.boavista.multiscore.dto.SocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.serializer.Deserializer;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class MultiScoreDeserializer implements Deserializer<SocketData> {

    @Override
    public SocketData deserialize(InputStream inputStream) throws IOException {
        log.info("Deserialize the object");
        final int BUFFER_SIZE = 32000;
        final int ETX = 0x03;

        final StringBuffer buffer = new StringBuffer();
        int readLength;
        byte[] bytes;
        bytes = new byte[BUFFER_SIZE];
        do {
            readLength = inputStream.read(bytes);
            if(readLength > 0){
                buffer.append(new String(bytes),0,readLength);
            }
        } while (bytes[readLength-1] != ETX);
        final StringBuffer stringBuffer = buffer.deleteCharAt(buffer.length() - 1);
        String toString = stringBuffer.toString();

        // This is a fixed length of 20 characters for UserId and other info.
        final String SOCKETY = "SOCKETY             ";
        log.info("String : {} ", toString.substring(20));
        final SocketData socketData = SocketData.builder().data(toString.substring(20)).build();
        log.info("SocketData object : {} ", socketData.getData());
        return socketData;
    }
}
