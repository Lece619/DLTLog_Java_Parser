import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import log.DLTLog;

public class DLTParser {
    private List<DLTLog> parseDLT(String filePath) {
        List<DLTLog> dltLogs = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fis)) {
            int bufferedIndex = 0;

            int readCode = 0;
//            for (int i = 0; i < START_LOG.length; i++) {
//                readCode = bufferedInputStream.read();
//            }

            int readByte;

            //첫번째 DLT01 찾기
            readCode = getReadCode(bufferedInputStream, readCode);

            ByteBuffer byteBuffer = ByteBuffer.allocate(16 * 16 * 16 * 16); // Define LOG_SIZE


            while ((readByte = bufferedInputStream.read()) != -1) {
                byteBuffer.put((byte) readByte);
                if (readByte == START_LOGS[readCode][bufferedIndex++]) {
                    if (bufferedIndex == START_LOGS[readCode].length) {
                        byteBuffer.flip();
                        dltLogs.add(DLTLog.createDltLog(byteBuffer.array(), readCode));
                        byteBuffer.clear();
                        bufferedIndex = 0;
                    }
                } else {
                    bufferedIndex = 0;
                }
            }

            byteBuffer.flip();
            dltLogs.add(DLTLog.createDltLog(byteBuffer.array(), readCode));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return dltLogs;
    }
    private static int getReadCode(BufferedInputStream bufferedInputStream, int readCode) throws IOException {

        int readByte;
        int index = 0;

        while ((readByte = bufferedInputStream.read()) != -1) {

            if (readByte == START_LOGS[0][index++]) {
                if (index == START_LOGS[0].length) {
                    readCode = 0;
                    break;
                }
            } else if (readByte == START_LOGS[1][index++]) {
                if (index == START_LOGS[1].length) {
                    readCode = 1;
                    break;
                }
            } else {
                index = 0;
            }

        }
        return readCode;
    }
}
