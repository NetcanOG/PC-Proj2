package pc.crawler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Concurrent crawler.
 *
 */
public class ConcurrentCrawler extends BaseCrawler{

  private AtomicInteger rid = new AtomicInteger(0);
  private final int numberOfThreads;
  private Thread[] thread; 

  public ConcurrentCrawler(int threads) throws IOException {
    this.numberOfThreads = threads;
    this.thread = new Thread[numberOfThreads];
  }

  class Bob{
    private LinkedList<URL> toVisit;
    private HashSet<URL> seen;

    public Bob(){
      toVisit = new LinkedList<>();
      seen = new HashSet<>();
    }

    synchronized URL removeFromList(){
      return toVisit.isEmpty()? null : toVisit.removeFirst();
    }
    
    synchronized void parsing(ArrayList<URL> links){ 
      for (URL newURL : links) {
        if (seen.add(newURL)) {
          // URL not seen before
          toVisit.addLast(newURL);
        } 
      }
    }

    synchronized boolean isEmpty(){
      return toVisit.isEmpty();
    }
  }

  class Wendy{
    Boolean[] threadUse;
    
    Wendy(){
      threadUse = new Boolean[numberOfThreads];
    }

    synchronized Boolean anyThreadOn(){
      for(Boolean thread: threadUse){
        if(thread) return true;
      }
      return false;
    }

    synchronized void setThread(int id, boolean using){
      threadUse[id] = using;
    }
  }

  public static void main(String[] args) throws IOException {
    int threads = args.length > 0 ? Integer.parseInt(args[0]) : 4;
    String rootPath = args.length > 1 ? args[1] : "http://localhost:8123/";
    ConcurrentCrawler cc = new ConcurrentCrawler(threads);
    cc.setVerboseOutput(true);
    cc.crawl(new URL(rootPath));
  }
  
  @Override
  public void crawl(URL root) {
    long t = System.currentTimeMillis();
    Bob bob = new Bob();
    Wendy wendy = new Wendy();
    log("Starting at %s", root);
    bob.parsing(new ArrayList<URL>(Arrays.asList(root)));    

    for(int i = 0; i < numberOfThreads; i++){
      final int id = i;
      thread[i] = new Thread(() -> {
        URL url;
        while(true) {
          url = bob.removeFromList();
          if(url != null){
            wendy.setThread(id, true);
            File htmlContents = download(rid.incrementAndGet(), url);
            if (htmlContents != null){
              ArrayList<URL> links = parseLinks(url, htmlContents);
              bob.parsing(links);
            } 
             wendy.setThread(id, false);
          } else if(!wendy.anyThreadOn()){
            break;
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
    System.out.printf("Done: %d transfers in %d ms (%.2f transfers/s)%n", rid.get(),
        t, (1e+03 * rid.get()) / t);
  }
}
