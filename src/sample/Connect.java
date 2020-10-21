package sample;

import com.fazecast.jSerialComm.SerialPort;
import javafx.scene.control.TextArea;

import java.nio.charset.StandardCharsets;

class Connect extends Thread {
    SerialPort serialPort;
    String answer = "";
    TextArea ta;


    public Connect(SerialPort serialPort, TextArea ta) {
        this.serialPort = serialPort;
        this.ta = ta;
    }

    public void run() {
        while (true) {
            while (serialPort.bytesAvailable() == 0) {
                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("===============================");
            ta.appendText("======================================/n");

            byte[] inputButes = new byte[serialPort.bytesAvailable()];
            serialPort.readBytes(inputButes, inputButes.length);
            String s = new String(inputButes, StandardCharsets.UTF_8);
            answer=s;
            ta.appendText(" message: " + s);
            System.out.println(" message: " + s);


        }


    }
}
