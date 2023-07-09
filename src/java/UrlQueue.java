package com.example.servingwebcontent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.*;
import java.io.*;

public class UrlQueue extends UnicastRemoteObject implements URLQueueInterface {
    ArrayList<String> queue = new ArrayList<String>();
    int MAX_DOWNLOADS=25;
    int ACTIVE_DOWNLOADS=0;
  
  
  UrlQueue() throws RemoteException {

  }
  //Método que mostra as estatisticas
  public ArrayList<Integer> showStats() throws RemoteException {
    ArrayList<Integer> stats =new ArrayList<Integer>();
    stats.add(ACTIVE_DOWNLOADS);
    stats.add(queue.size());
    return stats;
  }
  //Método que decrementa o numero de downloads ativos (é usado por RMI quando um downloader acaba)
  public void endDownload() throws RemoteException{
    this.ACTIVE_DOWNLOADS=this.ACTIVE_DOWNLOADS-1;
  }
  //Método que adiciona um novo url á Queue
  public void newURL(String url,boolean isRecursive){
    if(isRecursive){
      this.queue.add(url);
    }
    else{
      this.queue.add(0,url);
    }
    System.out.println(url + " adicionado à fila.");

  }
  //Método que imprime print da queue
  public void printQueue(){
    for (String url:this.queue){
        System.out.println(url);
    }
  }


  public static void main(String args[]) throws RemoteException {
    
    try { 

       UrlQueue q = new UrlQueue();
       Registry r = LocateRegistry.createRegistry(9002);
		   r.rebind("ADD", q);
       Registry r1 = LocateRegistry.createRegistry(9003);
		   r1.rebind("QUEUE", q);

       while (true){

        if(q.ACTIVE_DOWNLOADS<q.MAX_DOWNLOADS && q.queue.size() > 0){
          
          Downloader d1 =new Downloader(q.queue.get(0));
          d1.start();
          q.ACTIVE_DOWNLOADS=q.ACTIVE_DOWNLOADS+1;
          q.queue.remove(0);

        }
        Thread.sleep(25);
      }
		
    
		
    }catch (Exception e) {
			System.out.println("Exception in main: " + e);
		}

  }
  
}