import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public final class Scrapper {

    private String html;
    private Document document;
    private String fileName;
    private String baseURL;
    private String currentDir = System.getProperty("user.dir");

    public static Scrapper init() {
        return new Scrapper();
    }


    public Scrapper fetch(String requestURL) {
        URL url = null;
        this.baseURL = requestURL;
        try {
            url = new URL(requestURL);
        } catch (MalformedURLException e) {
            System.out.println("The URL is malformed!");
        }

        if (url != null) {
            try {
                String line;
                String text = "";
                URLConnection connection = url.openConnection();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = buffer.readLine()) != null) {
                    text += line;
                }
                buffer.close();
                if (!text.isEmpty()) {
                    this.html = text;
                    return this;
                }
            } catch (IOException e) {
                System.out.println("There is an error while connecting to the URL");
            }
        }
        return this;
    }


    public Scrapper parse() {
        if (!this.html.isEmpty()) {
            if (this.fileName == null || this.fileName.endsWith(".html")) {
                this.document = Jsoup.parse(this.html);
            }
        }
        return this;
    }


    //What =>
    //How =>
    //Why =>
    public Scrapper store(String directory) {
        try {
            if (this.fileName == null) {
                this.fileName = "index.html";
            }
            if (!this.fileName.endsWith(".html")) {
                String dir = this.fileName.substring(0, this.fileName.lastIndexOf("/"));
                String formattedFileName = this.fileName.substring(this.fileName.lastIndexOf("/"));
                File file = new File(currentDir + "/download" + dir);
                if (!file.exists()) {
                    file.setWritable(true);
                    file.setReadable(true);
                    file.mkdirs();
                }
                FileWriter writer = new FileWriter(currentDir + "/download" + dir + formattedFileName);
                writer.write(this.html);
                writer.close();
            } else {
                FileWriter writer = new FileWriter(currentDir + "/download/" + this.fileName);
                writer.write(this.document.html());
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }


    public void build(boolean recursive) {
        if (recursive) {
            if (this.fileName.endsWith(".html")) {
                Elements headerJsElements = this.document.head().getElementsByAttributeStarting("src");
                for (Element headerElement : headerJsElements) {
                    Scrapper scrapper = init();
                    scrapper.fileName = headerElement.attributes().get("src");
                    scrapper.baseURL = "https://golang.org";
                    scrapper.fetch(this.baseURL + headerElement.attributes().get("src")).parse().store(headerElement.attributes().get("src")).build(true);
                }
            }
        }
        System.out.println("Done");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Scrapper)) return false;
        Scrapper scrapper = (Scrapper) o;
        return html.equals(scrapper.html) &&
                document.equals(scrapper.document) &&
                fileName.equals(scrapper.fileName) &&
                baseURL.equals(scrapper.baseURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(html, document, fileName, baseURL);
    }


    @Override
    public String toString() {
        return "Scrapper{" +
                "html='" + html + '\'' +
                ", document=" + document +
                ", fileName='" + fileName + '\'' +
                ", baseURL='" + baseURL + '\'' +
                '}';
    }


}
