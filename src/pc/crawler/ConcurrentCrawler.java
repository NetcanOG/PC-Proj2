package pc.crawler;

import java.io.File;
import java.io.IOException;
import java.net.URL;


/**
 * Concurrent crawler.
 *
 */
public class ConcurrentCrawler extends BaseCrawler {

  public static void main(String[] args) throws IOException {
    int threads = args.length > 0 ? Integer.parseInt(args[0]) : 4;
    String rootPath = args.length > 1 ? args[1] : "http://localhost:8123";
    ConcurrentCrawler cc = new ConcurrentCrawler(threads);
    cc.setVerboseOutput(true);
    cc.crawl(new URL(rootPath));
  }

  private final int numberOfThreads;

  public ConcurrentCrawler(int threads) throws IOException {
    this.numberOfThreads = threads;
  }

  @Override
  public void crawl(URL root) {
    // TODO: create threads, then crawl!
  }
}
