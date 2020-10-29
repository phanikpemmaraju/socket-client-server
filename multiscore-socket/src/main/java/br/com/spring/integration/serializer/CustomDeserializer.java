package br.com.spring.integration.serializer;

import br.com.spring.integration.dto.SocketData;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.serializer.Deserializer;

public class CustomDeserializer implements Deserializer<SocketData> {

    @Override
    public SocketData deserialize(InputStream inputStream) throws IOException {
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
        final SocketData socketData = new SocketData();
        socketData.setData(toString.substring(20));
        return socketData;
    }
}
