package br.com.boavista.multiscore.serializer;

import br.com.boavista.multiscore.dto.SocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.serializer.Serializer;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class MultiScoreSerializer implements Serializer<SocketData> {

    @Override
    public void serialize(SocketData socketData, OutputStream outputStream) throws IOException {
        log.info("Serialize the object");
        outputStream.write(socketData.getData().getBytes());
        outputStream.write(0x03);
        outputStream.flush();
    }
}
