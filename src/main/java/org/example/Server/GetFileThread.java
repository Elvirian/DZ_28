package org.example.Server;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GetFileThread extends Thread {

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(8082)) {

            Socket socket = serverSocket.accept();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            String fileName = dataInputStream.readUTF();

            int nameBegin = fileName.lastIndexOf("/") + 1;
            fileName = fileName.substring(nameBegin);

            System.out.println("fileName=" + fileName);

            int filePart;
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);

            long fileSizeFromClient = dataInputStream.readLong();

            byte[] buffer = new byte[4 * 1024];

            while (fileSizeFromClient > 0 &&
                    (filePart = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSizeFromClient))) != -1) {
                fileOutputStream.write(buffer, 0, filePart);
                fileSizeFromClient -= filePart;
            }
            System.out.println("File is Received");
            fileOutputStream.close();

            dataInputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            run();
        }
    }
}