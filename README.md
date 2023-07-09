# Googol---Web-Search-Engine-Using-Java-HTML-REST-API-

Googol is a web search engine project developed as part of the Systems Distributed course. The main goal of this project is to create a search engine similar to popular services like Google.com, Bing.com, and DuckDuckGo.com. It includes features such as automatic indexing using a web crawler and search functionality.

## Technologies Used:
#### -Java
#### -Spring Boot
#### -Thymeleaf
#### -RMI (Remote Method Invocation)
#### -WebSockets
#### -REST API integration (Hacker News API)


## Features Implemented:

#### -Web Interface: The project includes a web-based interface for accessing the Googol search engine. It provides users with the ability to perform searches, index new URLs, and view search results.
#### -Automatic Indexing: Users can manually enter a URL to be indexed by the web crawler. The crawler visits the provided URL and associates it with the relevant words found in the page's content.
#### -Recursive Indexing: The automatic indexer recursively visits URLs found on previously visited pages, expanding the index as it discovers new links.
#### -Search Functionality: Users can search for pages containing specific terms. The search engine queries the inverted index and presents a list of pages that match the search terms. Each search result includes the page title, full URL, and a short citation from the page's content.
#### -Real-time System Status: The web interface provides real-time updates on the system's status. Users can access information about active downloaders, barrels, and the top 10 most common searches performed by users.
#### -WebSocket Integration: WebSockets are used to enable instant updates and push information to the client as soon as it becomes available. Real-time updates are provided for the system status and search results from different index partitions.
#### -Integration with Hacker News API: The Googol search engine is integrated with the Hacker News API. Users can index URLs from Hacker News top stories that contain the search terms. They can also request indexing of all stories from a specific Hacker News user.
