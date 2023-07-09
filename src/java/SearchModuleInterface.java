package com.example.servingwebcontent;
import java.rmi.*;
import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.*;
import java.io.*;


public interface SearchModuleInterface extends Remote{
    public String showStats() throws RemoteException;
    public void newURL(String url) throws RemoteException;
    public ArrayList<URLInfo> searchURLS(String sentence) throws RemoteException;
    public boolean login(String username,String password) throws RemoteException;
    
}