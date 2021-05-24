package pc.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Crawler - sequential version.
 */
public class SequentialCrawler extends BaseCrawler {
  /**
   * Program entry point.
   * 
   * @param args
   *          Arguments.
   * @throws IOException
   *           if an I/O error occurs.
   */
  public static void main(String[] args) throws IOException {
    String rootPath = args.length != 0 ? args[0] : "http://127.0.0.1:8123/";
    SequentialCrawler sc = new SequentialCrawler();
    sc.setVerboseOutput(true);
    sc.crawl(new URL(rootPath));
  }

  public SequentialCrawler() throws FileNotFoundException {
    
  }

  @Override
  public void crawl(URL root) throws IOException {
    long t = System.currentTimeMillis();
    int rid = 0;
    LinkedList<URL> toVisit = new LinkedList<>();
    HashSet<URL> seen = new HashSet<>();
    log("Starting at %s", root);
    seen.add(root);
    toVisit.add(root);
    while (!toVisit.isEmpty()) {
      URL url = toVisit.removeFirst();
      rid++;
      File htmlContents = download(rid, url);
      if (htmlContents != null) {
        ArrayList<URL> links = parseLinks(url, htmlContents);
        for (URL newURL : links) {
          if (seen.add(newURL)) {
            // URL not seen before
            toVisit.addLast(newURL);
          } 
        }
      }
    }
    t = System.currentTimeMillis() - t;
    System.out.printf("Done: %d transfers in %d ms (%.2f transfers/s)%n", rid,
        t, (1e+03 * rid) / t);

  }
}
