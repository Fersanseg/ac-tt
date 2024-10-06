package com.actt.actt.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class Logger {
    public static void log(String msg) throws IOException {
        FileInputStream fileInputStream = FileOperations.getLogFile();
        byte[] fileContent = fileInputStream.readAllBytes();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String logMsg = timestamp + " --- " + msg + "\n";
        byte[] msgBytes = logMsg.getBytes(StandardCharsets.UTF_8);

        byte[] newFileContent = new byte[fileContent.length + msgBytes.length];

        System.arraycopy(fileContent, 0, newFileContent, 0, fileContent.length);
        System.arraycopy(msgBytes, 0, newFileContent, fileContent.length, msgBytes.length);

        FileOperations.saveLogFile(newFileContent);
        fileInputStream.close();
    }
}
