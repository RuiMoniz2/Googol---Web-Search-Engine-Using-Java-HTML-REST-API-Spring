package com.example.servingwebcontent;
import java.util.ArrayList;
import java.util.Arrays;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

public class SearchModule extends UnicastRemoteObject implements SearchModuleInterface {
  
  static StorageBarrelInterface b1;
  public ArrayList<StorageBarrelInterface> barrels = new ArrayList<StorageBarrelInterface>();
  public HashMap<String,Integer> searchHistoric= new HashMap<String, Integer>();
  static URLQueueInterface queue;
  //ADDRESS MULTICAST SEARCH
  public String storageMULTICAST_ADDRESS = "225.3.2.1";
  public int storagePORT = 1234;


//Thread que recebe os registrys dos barrels por multicast
  public class receiveRegistry implements Runnable{
  //ADDRESS MULTICAST SEARCH
  public String storageMULTICAST_ADDRESS = "225.3.2.1";
  public int storagePORT = 1234;
  public SearchModule search;
  
  
  receiveRegistry(SearchModule search) throws RemoteException {
    this.search=search;
    

}

  @Override
  public void run() {
    try{
    MulticastSocket socketStorage = null;
    socketStorage = new MulticastSocket(storagePORT);  
    InetAddress groupStorage = InetAddress.getByName(storageMULTICAST_ADDRESS);
    socketStorage.joinGroup(groupStorage);
    byte[] bufferStorage = new byte[1024];
    DatagramPacket packetStorage = new DatagramPacket(bufferStorage, bufferStorage.length);
    while(true){
    socketStorage.receive(packetStorage);
    String m1 = new String(packetStorage.getData(), 0, packetStorage.getLength());
    StorageBarrelInterface b = (StorageBarrelInterface) LocateRegistry.getRegistry(Integer.parseInt(m1)).lookup("XPTO");
    if (!search.barrels.contains(b)){
        //System.out.println("ADICIONADO " + m1);
        search.barrels.add(b);
    }

    }
    
    }catch (Exception e) {
        System.out.println("Exception in main: " + e);
    }
    

  }

}
  
  SearchModule() throws RemoteException {

  }

 
  public String showStats() throws RemoteException {
    String ret;
    ArrayList<Integer> arr =queue.showStats();
    ret="URL's in Queue: " +arr.get(1)+ "\n" + "Active Downloaders: " +arr.get(0) + "\n" + "Active Barrels: " + barrels.size() + "\n";
    //ORDENACAO DO HASHMAP FEITA NO CHATGPT
    List<Map.Entry<String, Integer>> list = new LinkedList<>(searchHistoric.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
        return o2.getValue().compareTo(o1.getValue());
    }
    });
    LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
    for (Map.Entry<String, Integer> entry : list) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }
    ret=ret + "Top 10 pesquisas: \n";
    int aux=0;
    for(String s:sortedMap.keySet()){
      if(aux==10){
        break;
      }
      ret=ret + s + " - " + sortedMap.get(s) +" vezes\n";
      
      aux=aux+1;
    }


    return ret;

  }

  public boolean login(String username,String password) throws RemoteException{
      ArrayList<StorageBarrelInterface> offBarrels=new ArrayList<StorageBarrelInterface>();
      boolean ret=false;
      for(int i=0;i<this.barrels.size();i++){
        try{
          ret=barrels.get(i).login(username,password);
          
          //System.out.println("existe");
          break;
        }catch (NullPointerException e){
          offBarrels.add(barrels.get(i));
          //System.out.println("nao existe");
        }

    }
    barrels.removeAll(offBarrels);
    
    return ret;
  }
  public void newURL(String url) throws RemoteException{
    queue.newURL(url,false);

  }
  public ArrayList<URLInfo> searchURLS(String sentence) throws RemoteException{
    ArrayList<URLInfo> urls =new ArrayList<URLInfo>();
    ArrayList<StorageBarrelInterface> offBarrels=new ArrayList<StorageBarrelInterface>();
    if(searchHistoric.containsKey(sentence)){
      Integer count=searchHistoric.get(sentence)+1;
      searchHistoric.remove(sentence);
      searchHistoric.put(sentence,count);
    }
    else{
      this.searchHistoric.put(sentence,1);

    }
    
    for(int i=0;i<this.barrels.size();i++){
        try{
          urls=barrels.get(i).searchURLS(sentence);
          
          //System.out.println("existe");
          break;
        }catch (RemoteException e){
          offBarrels.add(barrels.get(i));
          //System.out.println("nao existe");
        }

    }
    barrels.removeAll(offBarrels);

    return urls;

  }

  public static void main(String args[]) throws RemoteException {
    try {    
    
    SearchModule search = new SearchModule();
    Registry r = LocateRegistry.createRegistry(8001);
		r.rebind("SEARCH", search);  
    queue = (URLQueueInterface) LocateRegistry.getRegistry(9002).lookup("ADD");






    SearchModule.receiveRegistry t =search.new receiveRegistry(search);
    new Thread(t).start();

    while(true){
      
      

    }
    
    
		
    }catch (Exception e) {
			System.out.println("Exception in main: " + e);
		}

  }
  
}

