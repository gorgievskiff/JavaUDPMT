package Lab1UDp;
import java.io.*;
import java.net.*;
import java.util.*;


class MessageSender implements Runnable {
    public final static int PORT = 7331;
    private DatagramSocket sock;
    private String hostname;
    MessageSender(DatagramSocket s, String h) throws UnknownHostException {
        sock = s;
        hostname = InetAddress.getLocalHost().getHostName();
    }
    private void sendMessage(String s) throws Exception {
        byte buf[] = s.getBytes();
        InetAddress address = InetAddress.getByName(hostname);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
        sock.send(packet);
    }
    public void run() {
        boolean connected = false;
        do {
            try {
                sendMessage("GREETINGS");
                connected = true;
            } catch (Exception e) {

            }
        } while (!connected);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                while (!in.ready()) {
                    Thread.sleep(100);
                }
                sendMessage(in.readLine());
                System.out.println("Uspesno ispratena poraka");
            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }
}
class MessageReceiver implements Runnable {
    DatagramSocket sock;
    byte buf[];
    MessageReceiver(DatagramSocket s) {
        sock = s;
        buf = new byte[1024];
    }
    public void run() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                sock.receive(packet);
                String received = ChatServer.decode(buf);
                System.out.println(received);
                buf = new byte[1024];
            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }
}
public class ChatClient {

    public static void main(String args[]) throws Exception {
        System.out.println("First login using 'login your name'\nIf you want to see all clients just type 'clients'\nIf you want to sent a message to someone use 'sent#message#clientname");
        String host = InetAddress.getLocalHost().getHostName();

        DatagramSocket socket = new DatagramSocket();
        MessageReceiver r = new MessageReceiver(socket);
        MessageSender s = new MessageSender(socket, host);
        Thread rt = new Thread(r);
        Thread st = new Thread(s);
        rt.start(); st.start();
    }
}