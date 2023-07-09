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

public class Client extends UnicastRemoteObject {
  
  static SearchModuleInterface search;
  public boolean logged;
  
  Client() throws RemoteException {
    this.logged=false;
  }


  public static void main(String args[]) throws RemoteException {
    InputStreamReader input = new InputStreamReader(System.in);
	BufferedReader reader = new BufferedReader(input);
    try {    
    
    search = (SearchModuleInterface) LocateRegistry.getRegistry(8001).lookup("SEARCH"); //r.lookup("XPTO");
    Client client =new Client();
    System.out.println("Bem vindo ao Googlo.");
    while(true){
        System.out.print("Insert Query: ");
		String query = reader.readLine();
        String[] words = query.split("\\s+");
        String command = words[0];
        String parameters = "";
        for (int i = 1; i < words.length; i++) {
            parameters += words[i];
            if (i < words.length - 1) {
            parameters += " ";
            }
        }

        if (command.compareTo("STATS") == 0){
                System.out.println("____________________________________________");
                System.out.println("Estatísticas");
                System.out.println("____________________________________________");
                System.out.println(search.showStats());
                System.out.println("____________________________________________");
                System.out.println("____________________________________________");
        }

        else if (command.compareTo("LOGIN") == 0){
            if(search.login(words[1],words[2])){
                System.out.println("Login sucessfull");
                client.logged=true;
            }
            else{
                System.out.println("Failed to login");
            }
        }
        else if (command.compareTo("ADD") == 0){
            search.newURL(parameters);
        }
        else if (command.compareTo("SEARCH") == 0){


        ArrayList<URLInfo> urls =search.searchURLS(parameters);
        if(urls==null){
            System.out.println("Nao existem resultados associados a esta pesquisa");
        }
        if(urls!=null){
            String cmd="";
            int page=0;
            while(cmd.compareTo("BACK")!=0){
                System.out.println("___________________________________________________________________________");
                for(int i=(page*10);i<(page*10)+10 && i<urls.size();i++){
                    urls.get(i).showInfo();
                }
                System.out.println("___________________________________________________________________________");
                System.out.println("Querys:\n NEXT - Pagina seguinte\n PREVIOUS - Pagina anterior \n BACK - Menu Anterior\n");
                System.out.print("->");
                cmd = reader.readLine();
                if(cmd.compareTo("NEXT")==0 && page !=Math.ceil(urls.size()/10)){
                    page=page+1;
                }
                else if(cmd.compareTo("PREVIOUS")==0 && page!=0){
                    page=page-1;
                }


            }
        }
		
        }
        else{
            System.out.println("Comando Errado.");
        }
        

      
      
    }
    
		
    }catch (Exception e) {
			System.out.println("Exception in main: " + e);
		}

  }
  
}