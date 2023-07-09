package com.example.servingwebcontent;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.example.servingwebcontent.beans.Number;
import com.example.servingwebcontent.forms.Project;
import com.example.servingwebcontent.thedata.Employee;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Controller
public class GreetingController {
    static SearchModuleInterface search;
    static SearchModuleInterface searchM;
    private Integer pageNr=0;
    private Integer maxPages=0;
    private String currentSearch;
    private static final Logger log = LoggerFactory.getLogger(GreetingController.class);


    @GetMapping("/")
    public String redirect() {
        return "redirect:/greeting";
    }

    @GetMapping("/greeting")
    public String greeting(Model model) {
        model.addAttribute("sentence", "");
        model.addAttribute("url", "");
        return "greeting";
    }

    @PostMapping("/indexURL")
    public String indexURL(@RequestParam("url") String url) {
        try {  
            search = (SearchModuleInterface) LocateRegistry.getRegistry(8001).lookup("SEARCH");
            search.newURL(url);
            return "redirect:/greeting";
        }catch (Exception e) {
			System.out.println("Exception in main: " + e);
		}
        return "redirect:/greeting";
    }

    @PostMapping("/greeting")
    public String submitGreeting(@RequestParam("sentence") String sentence) {
        pageNr=0;
        currentSearch=sentence;
        return "redirect:/searchResults?sentence=" + sentence +"&page=" + pageNr;
    }

    @PostMapping("/nextResults")
    public String nextResults() {
        if(pageNr<maxPages){
            pageNr++;
        }
        
        return "redirect:/searchResults?sentence=" + currentSearch +"&page=" + pageNr;
    }
    @PostMapping("/previousResults")
    public String previousResults() {
        if (pageNr > 0) {
            pageNr--; 
        }
        return "redirect:/searchResults?sentence=" + currentSearch +"&page=" + pageNr;
    }

    @GetMapping("/searchResults")
    public String searchResults(@RequestParam("sentence") String sentence, Model model) {
        try {  
            search = (SearchModuleInterface) LocateRegistry.getRegistry(8001).lookup("SEARCH");
            ArrayList<URLInfo> urls = search.searchURLS(sentence);
            if(urls!=null){
                maxPages = (int) Math.floor((double) urls.size() / 10);
            }
            else{
                maxPages=0;
            }
            ArrayList<URLInfo> urlsShow = new ArrayList<URLInfo>();
            for(int i=pageNr*10;i<(pageNr*10)+10;i++){
                if(i<urls.size()){
                    urlsShow.add(urls.get(i));
                }
                
            }
            model.addAttribute("urls", urlsShow);
            return "searchResults";
        }catch (Exception e) {
			System.out.println("Exception in main: " + e);
		}
        /*urls.add(new URLInfo("www.uc.pt", "Universidade de Coimbra", "A universidade fechou", new ArrayList<String>()));
        urls.add(new URLInfo("www.record.pt", "Record, jornal", "O benfica perdeu", new ArrayList<String>()));
        */

        return "searchResults";
    }

    @GetMapping("/statistics")
	public String statistics(Model model) {
        try {  
            search = (SearchModuleInterface) LocateRegistry.getRegistry(8001).lookup("SEARCH");
            String[] stats=search.showStats().split("\n");
            model.addAttribute("stats", stats);
            return "statistics";
        }catch (Exception e) {
			System.out.println("Exception in main: " + e);
		}
		return "statistics";
	} 

    @GetMapping("/login")
	public String login(@ModelAttribute User user,Model model) {
        model.addAttribute("user", user);
		return "login";
	}
    
    
    @PostMapping("/login")
    public String verifyLogin(@ModelAttribute("user") User user) {
    try {  
        search = (SearchModuleInterface) LocateRegistry.getRegistry(8001).lookup("SEARCH");
        if (search.login(user.getUsername(), user.getPassword())) {
            return "redirect:/greeting";
        } else {
            return "redirect:/login";
        }
    } catch (Exception e) {
        System.out.println("Exception in main: " + e);
    }
    return "redirect:/login";
}

    @GetMapping("/hackerNews")
	public String hackerNews(@ModelAttribute String username,Model model) {
        model.addAttribute("username", username);
		return "hackerNews";
	}

    @PostMapping("/hackernewsusers")
    @ResponseBody
    private String hackerNewsUsers(@ModelAttribute("username") String username) {
        try{
            search = (SearchModuleInterface) LocateRegistry.getRegistry(8001).lookup("SEARCH");
            List<String> userEndpoints = List.of(
                "https://hacker-news.firebaseio.com/v0/user/"+username+".json?print=pretty");

        RestTemplate restTemplate = new RestTemplate();

        for (String endpoint : userEndpoints) {
            HackerNewsUserRecord hackerNewsUserRecord = restTemplate.getForObject(endpoint, HackerNewsUserRecord.class);
            assert hackerNewsUserRecord != null;

            if (hackerNewsUserRecord != null) {
                List<String> submitted = hackerNewsUserRecord.getSubmitted();
                if (submitted != null) {
                    for (Object item : submitted) {
                        String itemEndpoint ="https://hacker-news.firebaseio.com/v0/item/"+String.valueOf(item)+".json?print=pretty";
                        HackerNewsItemRecord hackerNewsItemRecord = restTemplate.getForObject(itemEndpoint, HackerNewsItemRecord.class);
                        if(hackerNewsItemRecord.getURL()!=null){
                            log.info(hackerNewsItemRecord.getURL());
                            search.newURL(hackerNewsItemRecord.getURL());
                        }
                        
                    }
                } else {
                    log.info("The user " + username + " doesn't have submitted itens\n");
                }
            } else {
                log.info("The user " + username + " doesn't exist\n");
            }
        }

        return "redirect:/hackerNews";
        }catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
        return "redirect:/hackerNews";
    }
   
}