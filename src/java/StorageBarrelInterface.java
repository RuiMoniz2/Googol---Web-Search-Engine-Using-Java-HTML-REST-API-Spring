package com.example.servingwebcontent;
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

public interface StorageBarrelInterface extends Remote {
    public ArrayList<URLInfo> searchURLS(String sentence) throws RemoteException;
    public void printIndex() throws RemoteException;
    public void addURL(ArrayList<String> words ,URLInfo newURL) throws RemoteException;
    public boolean login(String username,String password) throws RemoteException;   
}