package com.example.servingwebcontent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;



public class Downloader extends Thread {
    private String MULTICAST_ADDRESS = "224.3.2.1";
    private int PORT = 4321;
    String url;
    public Downloader(String url) {
        this.url=url;
    }
    public void run(){

        MulticastSocket socket = null;
        URLQueueInterface queue = null;    
        try {

            queue = (URLQueueInterface) LocateRegistry.getRegistry(9003).lookup("QUEUE");
             Document doc = Jsoup.connect(url).get();
            StringTokenizer tokens = new StringTokenizer(doc.text());
            int countTokens = 0;
            String title =doc.title();
            ArrayList<String> aux =new ArrayList<String>();
            String quote="";
            int i=0;
            while (tokens.hasMoreElements() && countTokens++ < 100){
            if (i<10){
                quote=quote + tokens.nextToken() + " ";
            }
            aux.add(tokens.nextToken().toLowerCase());
            i=i+1;
            }

            Elements links = doc.select("a[href]");
            ArrayList<String> auxLinks = new ArrayList<String>();
            for (Element link : links){
                auxLinks.add(link.attr("abs:href").toString());
                queue.newURL(link.attr("abs:href").toString(),true);
            }
            URLInfo auxURL =new URLInfo(this.url,title,quote,auxLinks);


            socket = new MulticastSocket();
            String message = this.url + " | " + title + " | " + quote +" | ";
            for(String word :aux){
                message=message + word + ";";
            }
            message=message + " | ";
            //System.out.println(message);





            byte[] buffer = message.getBytes();
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
            queue.endDownload();
            
        } catch (Exception e) {
            System.out.println("Error: " + e);
            try{
                queue.endDownload();
            }catch (Exception err) {
                System.out.println("Error: " + err);
            }
        }

    }
    public static void main(String args[]) {
       
    }
}
