package mirrortorrent.torrents;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lavajuno.lucidjson.JsonArray;
import org.lavajuno.lucidjson.JsonObject;

import mirrortorrent.io.Log;

public class TorrentScraper implements Runnable {

    final private JsonArray torrentArray;
    final private File torrentDirectory;

    public TorrentScraper(JsonArray torrentArray, File torrentDirectory) {
        this.torrentArray = torrentArray;
        this.torrentDirectory = torrentDirectory;
    }

    @Override
    public void run() {
        Log log = Log.getInstance();

        for (int i = 0; i < torrentArray.size(); i++) {
            JsonObject torrentObject = (JsonObject) torrentArray.get(i);
            String projectUrl = torrentObject.get("url").toString();
            String projectName = torrentObject.get("name").toString();

            if (!projectName.equals("documentfoundation")) {
                log.info("Scraping torrents for " + projectName);
                HashSet<String> links = scrapeLinksWithSuffix(projectUrl, ".torrent");
                downloadFileList(links, torrentDirectory.toPath().resolve(projectName));
            } else {
                log.info("Scraping torrents for LibreOffice");
                HashSet<String> links = scrapeLibreOfficeTorrentLinks(projectUrl, 5);
                downloadFileList(links, torrentDirectory.toPath().resolve("libreoffice"));
            }
        }
    }

    // use jsoup to get the list of anchorTags from a given webpage
    public HashSet<String> scrapeLinksWithSuffix(
            final String url,
            final String filterSuffix) {
        HashSet<String> linksContainingSuffix = new HashSet<>();

        Document doc;
        try {
            //get the html from the page
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            return linksContainingSuffix;
        }

        //get all the tag elements on the page
        final Elements anchorTags = doc.getElementsByTag("a");

        for (final Element tag : anchorTags) {
            // Get the contents of the href attribute
            final String href = tag.attr("abs:href");

            if (tag.text().toLowerCase().equals("parent directory")) {
                continue;
            }

            if (filterSuffix.equals("") || href.endsWith(filterSuffix)) {
                linksContainingSuffix.add(href);
            }
        }

        return linksContainingSuffix;
    }

    public HashSet<String> scrapeLibreOfficeTorrentLinks(
            final String url,
            final int depth) {
        final List tokensToIgnore = Arrays.asList("apache",
                "mirrorbrain",
                "mailto",
                "?C=N;O=D",
                "?C=M;O=A",
                "?C=S;O=A",
                ".tar.gz",
                ".msi",
                ".asc",
                ".dmg");

        HashSet<String> mirrorLinks = new HashSet<>();
        HashSet<String> currentLinks = new HashSet<>();
        HashSet<String> torrentLinks = new HashSet<>();
        currentLinks.add(url);

        for (int i = 0; i < depth; i++) {
            if (i == (depth - 1)) {
                for (final String link : currentLinks) {
                    mirrorLinks.addAll(
                            scrapeLinksWithSuffix(link, ".mirrorlist")
                    );
                }

                break;
            }

            // Retrieve all anchor tags on each page and add them to newLinks set
            HashSet<String> newLinks = new HashSet<>();
            for (final String link : currentLinks) {
                newLinks.addAll(
                        filterWithIgnoreList(
                                scrapeLinksWithSuffix(link, ""),
                                tokensToIgnore
                        )
                );
            }

            currentLinks = newLinks;
        }

        // Replace the `.mirrorlist` ending with `.torrent`
        for (String link : mirrorLinks) {
            link = link.replace(".mirrorlist", ".torrent");
            torrentLinks.add(link);
        }

        return torrentLinks;
    }

    public HashSet<String> filterWithIgnoreList(
            final HashSet<String> inputset,
            final List<String> toIgnore) {
        HashSet<String> outputset = new HashSet<>();

        for (final String link : inputset) {
            boolean shouldIgnore = false;

            for (final String item : toIgnore) {
                if (link.contains(item)) {
                    shouldIgnore = true;
                    break;
                }
            }

            if (!shouldIgnore) {
                outputset.add(link);
            }
        }

        return outputset;
    }

    /**
     * Download files from a vector of urls into a given file location
     */
    public void downloadFileList(
            final HashSet<String> urls,
            final Path directory) {
        for (final String url : urls) {
            String filename = Arrays.asList(url.split("/")).getLast();

            try {
                //download the file using the url, folder and calculated filename
                downloadFile(url, directory.resolve(filename).toFile());
            } catch (IOException e) {
                Log.getInstance().error(e.getMessage());
            }
        }
    }

    /**
     * Downloads a file from a given url into a given file path and name
     */
    public void downloadFile(
            final String url,
            final File outfile) throws IOException {
        //check to make sure that the file doesnt already exist
        if (outfile.exists()) {
            return;
        }

        if (!outfile.getParentFile().exists()) {
            outfile.getParentFile().mkdir();
        }

        HttpURLConnection httpConn;
        try {
            httpConn = (HttpURLConnection) new URL(url).openConnection();
        } catch (IOException e) {
            Log.getInstance()
                    .warn("Failed to establish HTTP connection while downloading file ("
                            + outfile.getPath()
                            + "). Exception message: "
                            + e.getMessage());
            return;
        }

        // Check to make sure that connection is established successfully
        if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Log.getInstance()
                    .warn("Failed to download file: "
                            + outfile.getPath()
                            + ". Received HTTP status: "
                            + httpConn.getResponseCode());
            return;
        }

        try (BufferedInputStream inputStream = new BufferedInputStream(httpConn.getInputStream()); FileOutputStream outputStream = new FileOutputStream(outfile)) {
            int bytesRead;
            byte[] buffer = new byte[4096];

            while (inputStream.available() != 0) {
                bytesRead = inputStream.read(buffer);
                if (bytesRead == -1) {
                    break;
                }

                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            outfile.delete();
            Log.getInstance().warn("Failed to download file: " + outfile.getPath() + ". Exception message: " + e.getMessage());
        }
    }
}
