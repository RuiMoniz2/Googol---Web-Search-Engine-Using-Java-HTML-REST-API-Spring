package com.example.servingwebcontent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.lang.*;
import java.util.LinkedHashMap;

public class StorageBarrel extends UnicastRemoteObject implements StorageBarrelInterface {
  //ADDRESS MULTICAST DOWNLOADERS
  public String MULTICAST_ADDRESS = "224.3.2.1";
  public int PORT = 4321;



  public boolean state;
  static SearchModuleInterface searchModule;
  HashMap< String, ArrayList<URLInfo>> index = new HashMap< String, ArrayList<URLInfo>>();
  HashMap<String,String> loginInfo = new HashMap<String, String>();;

 //Thread que envia o registo por Multicast para o SearchModule 
public class sendRegistry implements Runnable{
  //ADDRESS MULTICAST SEARCH
  public String searchMULTICAST_ADDRESS = "225.3.2.1";
  public int searchPORT = 1234;
  public String registry;

  sendRegistry(String registry) throws RemoteException {
  this.registry=registry;
}

  @Override
  public void run() {
    try{
    MulticastSocket socketSearch = null;
    socketSearch = new MulticastSocket();
    String portSearch = this.registry;
    byte[] bufferSearch = portSearch.getBytes();
    InetAddress groupSearch = InetAddress.getByName(searchMULTICAST_ADDRESS);
    DatagramPacket packetSearch = new DatagramPacket(bufferSearch, bufferSearch.length, groupSearch, searchPORT);
    while(true){
      socketSearch.send(packetSearch);
      Thread.sleep(15000);
    }
    }catch (Exception e) {
        System.out.println("Exception in main: " + e);
    }
    

  }

}

StorageBarrel(boolean state) throws RemoteException {
  this.state=state;
  this.loginInfo.put("admin","ola123");
  this.loginInfo.put("admin2","ol13");
}
//Método que efetua o login
public boolean login(String username,String password) throws RemoteException{
  if(this.loginInfo.containsKey(username) && this.loginInfo.get(username).compareTo(password)==0){
    return true;
  }
  return false;
}

//Método que recebe uma string e efetua a pesquisa no indice
public ArrayList<URLInfo> searchURLS(String sentence) throws RemoteException {
  System.out.println("Searching for: " + sentence);

  ArrayList<String> stopWords = new ArrayList<String>(Arrays.asList("de", "a", "O", "que", "e", "do", "da", "em", "um", "para", "é", "com", "não", "uma", "os", "no", "se", "na", "por", "mais", "as", "dos", "como", "mas", "foi", "ao", "ele", "das", "tem", "à", "seu", "sua", "ou", "ser", "quando", "muito", "há", "nos", "já", "está", "eu", "também", "só", "pelo", "pela", "até", "isso", "ela", "entre", "era", "depois", "sem", "mesmo", "aos", "ter", "seus", "quem", "nas", "me", "esse", "eles", "estão", "você", "tinha", "foram", "essa", "num", "nem", "suas", "meu", "às", "minha", "têm", "numa", "pelos", "elas", "havia", "seja", "qual", "será", "nós", "tenho", "lhe", "deles", "essas", "esses", "pelas", "este", "fosse", "dele", "tu", "te", "vocês", "vos", "lhes", "meus", "minhas", "teu", "tua", "teus", "tuas", "nosso", "nossa", "nossos", "nossas", "dela", "delas", "esta", "estes", "estas", "aquele", "aquela", "aqueles", "aquelas", "isto", "aquilo", "estou", "está", "estamos", "estão", "estive", "esteve", "estivemos", "estiveram", "estava", "estávamos", "estavam", "estivera", "estivéramos", "esteja", "estejamos", "estejam", "estivesse", "estivéssemos", "estivessem", "estiver", "estivermos", "estiverem", "hei", "há", "havemos", "hão", "houve", "houvemos", "houveram", "houvera", "houvéramos", "haja", "hajamos", "hajam", "houvesse", "houvéssemos", "houvessem", "houver", "houvermos", "houverem", "houverei", "houverá", "houveremos", "houverão", "houveria", "houveríamos", "houveriam", "sou", "somos", "são", "era", "éramos", "eram", "fui", "foi", "fomos", "foram", "fora", "fôramos", "seja", "sejamos", "sejam", "fosse", "fôssemos", "fossem", "for", "formos", "forem", "serei", "ser")); 
  String[] words = sentence.split("\\s+");
  ArrayList<String> usableWords = new ArrayList<String>();
  ArrayList<URLInfo> urls = new ArrayList<URLInfo>();
  for (String word : words){
    if (!stopWords.contains(word)){
      //System.out.println(word);
      usableWords.add(word);
    }
  }
  if (this.index.containsKey(usableWords.get(0))){
  urls=index.get(usableWords.get(0));
  for (int i=0;i<usableWords.size();i++){
    if (this.index.containsKey(usableWords.get(i))){
    ArrayList<URLInfo> wrdURL = this.index.get(usableWords.get(i));
    for(int j=0;j<urls.size();j++){
      if(!wrdURL.contains(urls.get(j))){
        urls.remove(j);
      }
    }
    }
    else{
      return null;
    }
    
  }    

  }
  else{
    return null;
  }

  return urls;}



//Método que imprime o indice
public void printIndex() throws RemoteException {
  for(String word : this.index.keySet()){
    ArrayList<URLInfo> values=index.get(word);
    System.out.println("The word '"+word+"' is associated with the following URL's");
    for (URLInfo url : values){
      url.showInfo();
    }

  }}

//Método que adiciona um novo URL com as devidas palavras encontradas no indice
public void addURL(ArrayList<String> words ,URLInfo newURL) throws RemoteException {

      for (String word :words){
          if(index.containsKey(word)){
              ArrayList<URLInfo> old = index.get(word);
              old.add(newURL);
              index.put(word,old);
          }
          else{
                ArrayList<URLInfo> new1 = new ArrayList();
                new1.add(newURL);
                index.put(word,new1);
          }
          
      }
      System.out.println(newURL.URL + " indexado.");
      }
public static void main(String args[]) throws RemoteException {
  try{

    StorageBarrel b1=new StorageBarrel(true);

    StorageBarrel.sendRegistry t =b1.new sendRegistry(args[0]);
    new Thread(t).start();

    Registry r = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
    r.rebind("XPTO", b1);

		MulticastSocket socketDownloader = null;
    socketDownloader = new MulticastSocket(b1.PORT);  
    InetAddress group = InetAddress.getByName(b1.MULTICAST_ADDRESS);
    socketDownloader.joinGroup(group);

    while(true){

      byte[] buffer = new byte[4096];
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      socketDownloader.receive(packet);
      String message = new String(packet.getData(), 0, packet.getLength());
      String url=message.substring(0,message.indexOf('|')).trim();
      String rest=message.substring(message.indexOf('|')+1,message.length()-1);
      String title=rest.substring(0,rest.indexOf('|')).trim();

      rest=rest.substring(rest.indexOf('|')+1,rest.length()-1);
      String quote =rest.substring(0,rest.indexOf('|')).trim();
      rest=rest.substring(rest.indexOf('|')+1,rest.length()-1);

      String[] wordsAux=rest.split(";");
      List<String> auxlist = Arrays.asList(wordsAux);
      ArrayList<String> words = new ArrayList<String>(auxlist);
      
      b1.addURL(words,new URLInfo(url,title,quote,new ArrayList<String>()));


    }
		
    } catch (Exception e) {
			System.out.println("Exception in main: " + e);
		}

  } }

class Stats implements Serializable {
 private Integer urlsQueue;
    private Integer downloadsActive;
    private Integer barrelsActive;
    private LinkedHashMap<String, Integer> searchTop10;

    public Stats(Integer urlsQueue, Integer downloadsActive, Integer barrelsActive, LinkedHashMap<String, Integer> searchTop10) {
        this.urlsQueue = urlsQueue;
        this.downloadsActive = downloadsActive;
        this.barrelsActive = barrelsActive;
        this.searchTop10 = searchTop10;
    }

    public Integer getUrlsQueue() {
        return urlsQueue;
    }

    public void setUrlsQueue(Integer urlsQueue) {
        this.urlsQueue = urlsQueue;
    }

    public Integer getDownloadsActive() {
        return downloadsActive;
    }

    public void setDownloadsActive(Integer downloadsActive) {
        this.downloadsActive = downloadsActive;
    }

    public Integer getBarrelsActive() {
        return barrelsActive;
    }

    public void setBarrelsActive(Integer barrelsActive) {
        this.barrelsActive = barrelsActive;
    }

    public LinkedHashMap<String, Integer> getSearchTop10() {
        return searchTop10;
    }

    public void setSearchTop10(LinkedHashMap<String, Integer> searchTop10) {
        this.searchTop10 = searchTop10;
    }

}

class User implements Serializable {
    String username;
    String password;

  
  User(String username,String password)  {
    this.username=username;
    this.password=password;

  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getUsername() {
      return username;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getPassword() {
      return password;
  }

}
class URLInfo implements Serializable {
  String URL;
  String title;
  String quote;
  ArrayList<String> links;

  
  URLInfo(String URL,String title,String quote,ArrayList<String> links)  {
    this.URL=URL;
    this.title=title;
    this.quote=quote;
    this.links=links;
    

  }
  public void showInfo(){
    System.out.println("URL: " + this.URL);
    System.out.println("Title: " + this.title);
    System.out.println("Quote: " + this.quote + "\n\n");
  }
  public String getURL(){
    return this.URL;
  }
  public String getTitle(){
    return this.title;
  }
  public String getQuote(){
    return this.quote;
  }

}
