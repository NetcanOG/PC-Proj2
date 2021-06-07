package pc.crawler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.*;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * Concurrent crawler.
 *
 */
public class ConcurrentCrawler extends BaseCrawler{

  private int rid;
  private LinkedList<URL> toVisit;
  private HashSet<URL> seen;
  private final int numberOfThreads;
  private Thread[] thread; 

  public static void main(String[] args) throws IOException {
    int threads = args.length > 0 ? Integer.parseInt(args[0]) : 4;
    String rootPath = args.length > 1 ? args[1] : "http://localhost:8123";
    ConcurrentCrawler cc = new ConcurrentCrawler(threads);
    cc.setVerboseOutput(true);
    cc.crawl(new URL(rootPath));
  }

  public ConcurrentCrawler(int threads) throws IOException {
    this.numberOfThreads = threads;
    this.toVisit = new LinkedList<>();
    this.seen = new HashSet<>();
    this.thread = new Thread[numberOfThreads];
  }

  synchronized URL removeFromList(){
    rid++;
    return toVisit.removeFirst();
  }

  synchronized void parsing(ArrayList<URL> links){ 
    for (URL newURL : links) {
      if (seen.add(newURL)) {
        // URL not seen before
        toVisit.addLast(newURL);
      } 
    }
  }
  
  @Override
  public void crawl(URL root) {
    long t = System.currentTimeMillis();
    rid = 0;
    log("Starting at %s", root);
    seen.add(root);
    toVisit.add(root);
    
    for(int i = 0; i < numberOfThreads; i++){
      thread[i] = new Thread(() -> {
        while (!toVisit.isEmpty()) {
          URL url = removeFromList();
          File htmlContents = download(rid, url);
          if (htmlContents != null){
            ArrayList<URL> links = parseLinks(url, htmlContents);
            parsing(links);
          } 
        }
      });
      thread[i].start();
    }

    for(int i = 0; i < numberOfThreads; i++){
      try{
        thread[i].join();
      } catch (InterruptedException e){
        e.printStackTrace();
      }
    }

    t = System.currentTimeMillis() - t;
    System.out.printf("Done: %d transfers in %d ms (%.2f transfers/s)%n", rid,
        t, (1e+03 * rid) / t);
  }
}
