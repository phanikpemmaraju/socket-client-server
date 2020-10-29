package br.com.spring.integration.serializer;

import br.com.spring.integration.dto.SocketData;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.core.serializer.Serializer;

public class CustomSerializer implements Serializer<SocketData> {

    @Override
    public void serialize(SocketData socketData, OutputStream outputStream) throws IOException {
        outputStream.write(socketData.getData().getBytes());
        outputStream.write(0x03);
        outputStream.flush();
    }
}
