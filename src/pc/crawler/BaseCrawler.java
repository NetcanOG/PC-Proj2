package pc.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Crawler - sequential version.
 */
public abstract class BaseCrawler {

  private boolean           verbose = false;
  private final PrintStream log;

  /**
   * Constructor.
   * 
   * @throws FileNotFoundException
   *           if log file <code>crawler.log</code> cannot be opened.
   */
  public BaseCrawler() throws FileNotFoundException {
    log = new PrintStream("crawler.log");
  }

  /**
   * Enable / disable verbose output.
   * 
   * @param enable
   *          Value for setting.
   */
  public final void setVerboseOutput(boolean enable) {
    verbose = enable;
  }

  /**
   * Crawl starting from given URL.
   * 
   * @param root
   *          Root URL.
   * @throws IOException
   *           if an I/O error occurs.
   */
  public abstract void crawl(URL root) throws IOException; 

  protected final File download(int rid, URL url) {
    try {
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      log("%d | %s | %d | %d bytes | %s", rid, url, conn.getResponseCode(),
          conn.getContentLength(), conn.getContentType());

      if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
        // Data can not be obtained.
        return null;
      }
      // Transfer to temporary file.
      File tmp = File.createTempFile("crawler", "bin");
      tmp.deleteOnExit();
      InputStream in = conn.getInputStream();
      byte[] buf = new byte[16384];
      int total = 0;
      try (FileOutputStream out = new FileOutputStream(tmp)) {
        while (total < conn.getContentLength()) { 
          int n = in.read(buf);
          total += n;
          out.write(buf, 0, n);
        }
      }
      in.close();

      if (!conn.getContentType().equals("text/html")) {
        // NOT HTML
        tmp.delete();
        return null;
      }
      return tmp;
    } catch (Exception e) {
      log("%d | Error: %s - %s", rid, e.getClass().getName(), e.getMessage());
      // e.printStackTrace(System.out);
      return null;
    }
  }

  private static final Pattern HTML_LINK_REGEX = Pattern
      .compile("\\<a\\s+href\\=\"(.*?)\"");

  protected final ArrayList<URL> parseLinks(URL url, File htmlFile) {
    ArrayList<URL> links = new ArrayList<>();
    try (Scanner sin = new Scanner(htmlFile)) {
      while (sin.hasNextLine()) {
        while (sin.findInLine(HTML_LINK_REGEX) != null) {
          MatchResult mr = sin.match();
          String link = mr.group(1);
          if (!link.contains(":") && !link.contains("#")) {
            links.add(new URL(url, new URL(url, link).getPath()));
          }
        }
        sin.nextLine();
      }
    } catch (FileNotFoundException e) {
      log("Error: %s - %s", e.getClass().getName(), e.getMessage());
    } catch (MalformedURLException e) {
      log("Error: %s - %s", e.getClass().getName(), e.getMessage());
    }
    return links;
  }
  
  private final Object LOG_MUTEX = new Object();

  @SuppressWarnings("deprecation")
  protected final void log(String format, Object... args) {
    synchronized(LOG_MUTEX) {
      String msg = String.format("%s | %s", new Date().toGMTString(),
          String.format(format, args));
      log.println(msg);
      if (verbose) System.out.println(msg);
    }
  }

}
