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

public interface URLQueueInterface extends Remote {
  public ArrayList<Integer> showStats() throws RemoteException;
  public void newURL(String url,boolean isRecursive) throws RemoteException;
  public void printQueue() throws RemoteException;
  public void endDownload() throws RemoteException;
}