package mirrortorrent.torrents;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.helper.ValidationException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.lavajuno.lucidjson.JsonArray;
import org.lavajuno.lucidjson.JsonObject;
import org.lavajuno.lucidjson.JsonString;


public class ScrapeTorrents implements Runnable{
    
    private static final int BUFFER_SIZE = 4096;

    private JsonArray torrentArray;
    private String torrentFolder;

    public ScrapeTorrents(JsonArray a, String s){
        this.torrentArray = a;
        this.torrentFolder = s;
    }

    public void run(){

        for(int i = 0; i < torrentArray.size(); i++){
            JsonObject o = (JsonObject) torrentArray.get(i);
            String projectUrl = ((JsonString) o.get("url")).getValue();
            String[] projectUrlArray = projectUrl.split("/")[2].split("\\.");
            String projectName = projectUrlArray[projectUrlArray.length - 2];

            System.out.println(projectUrl);
            System.out.println(projectName);

            if(!projectName.equals("documentfoundation")){
                HashSet<String> links = scrapeLinks(projectUrl, ".torrent");
                downloadFileList(links, torrentFolder + "/" + projectName);
            }
            else{
                HashSet<String> links = scrapeLibreOfficeTorrentLinks(projectUrl, 5);
                downloadFileList(links, torrentFolder + "/libreoffice");
            }
        }
    }

    //use jsoup to get the list of links from a given webpage
    public HashSet<String> scrapeLinks(String url, String filterSuffix){
        HashSet<String> link_set = new HashSet<>();

        try{
            //get the html from the page
            Document doc = Jsoup.connect(url).get();
            //get all the link elements on the page
            Elements links = doc.getElementsByTag("a");
            for (Element link : links){
                //get the href url for each link and add it to the output vector if the link ends in "filterSuffix"
                String l = link.attr("abs:href");
                if(link.text().toLowerCase().equals("parent directory")) continue;
                if(filterSuffix.equals("") || l.endsWith(filterSuffix)){
                    link_set.add(l);
                }
            }
        }
        catch(IOException | ValidationException e){
            return new HashSet<>();
        }

        return link_set;
    }

    public HashSet<String> scrapeLibreOfficeTorrentLinks(String url, int depth){
        String[] ignoreArray = {"apache", "mirrorbrain", "mailto", "?C=N;O=D", "?C=M;O=A", "?C=S;O=A", ".tar.gz", ".msi", ".asc", ".dmg"};

        HashSet<String> mirrorLinks = new HashSet<>();
        HashSet<String> currientLinks = new HashSet<>();
        HashSet<String> torrentLinks = new HashSet<>();
        currientLinks.add(url);

        for(int i = depth - 1; i >= 1; i--){
            if(i == 1){
                for(String link : currientLinks){
                    mirrorLinks.addAll(scrapeLinks(link, ".mirrorlist"));
                }
            }
            else{
                //retrieve all links on each page and add them to new Links vector
                HashSet<String> newLinks = new HashSet<>();
                for(String link : currientLinks){
                    newLinks.addAll(filterIgnoreList(scrapeLinks(link, ""), ignoreArray));
                }
                //replace currientLinks with newLinks
                currientLinks = newLinks;
            }
        }

        //replace the .mirrorlist ending with .torrent
        for(String s : mirrorLinks){
            s = s.substring(0, s.length() - 11) + ".torrent";
            torrentLinks.add(s);
        }

        return torrentLinks;
    }

    public HashSet<String> filterIgnoreList(HashSet<String> inputset, String[] ignoreArray){
        HashSet<String> outputset = new HashSet<>();
        for(String link : inputset){
            boolean flag = false;
            for(String s : ignoreArray){
                if(link.contains(s)){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                outputset.add(link);
            }
        }

        return outputset;
    }

    //download files from a vector of urls into a given file location
    public void downloadFileList(HashSet<String> urls, String folder){
        for(String url : urls){
            //get the filename from the url
            String[] split_url = url.split("/");
            String filename = split_url[split_url.length - 1];

            System.out.println(filename);
            try{
                //download the file using the url, folder and calculated filename
                downloadFile(url, folder, filename);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    //downloads a file from a given url into a given file path and name
    public void downloadFile(String url, String folder, String filename) throws IOException{
        //check to make sure that the file doesnt already exist
        File f = new File(folder + "/" + filename);
        if(f.exists()){
            return;
        }

        //create the folder if it doesnt exist
        File dir = new File(folder);
        if(!dir.exists()){
            dir.mkdir();
        }

        //open a http connection to the given url 
        HttpURLConnection httpConn = (HttpURLConnection) new URL(url).openConnection();
        int responseCode = httpConn.getResponseCode();

        //check to make sure that connection is established successfully
        if(responseCode == HttpURLConnection.HTTP_OK){
            //initialize input stream from the http connection
            InputStream inputStream = httpConn.getInputStream();

            //initialize output stream to the file
            FileOutputStream outputStream = new FileOutputStream(folder + "/" + filename);

            //read the bytes of the file from the http input stream and output them to the file output stream
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
        }
        else{
            throw new IOException("Failed to connect to " + url + " over http\nResponse Code: " + responseCode);
        }
    }
}
