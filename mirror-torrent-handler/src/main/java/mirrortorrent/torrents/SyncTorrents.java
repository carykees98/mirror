package mirrortorrent.torrents;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import org.lavajuno.lucidjson.JsonObject;
import org.lavajuno.lucidjson.JsonString;

public class SyncTorrents implements Runnable{

    private JsonObject mirrorConfig;
    private String torrentFolder;
    private String downloadFolder;

    public SyncTorrents(JsonObject a, String tf, String df){
        this.mirrorConfig = a;
        this.torrentFolder = tf;
        this.downloadFolder = df;
    }

    public void run(){

        for(String name : mirrorConfig.getKeys()){
            JsonObject projectConfig = (JsonObject) mirrorConfig.get(name);
            //skip if there are no torrent folders to check
            if(projectConfig.get("torrents") == null){
                continue;
            }

            try{
                //get the glob string for the mirrors.json project
                String glob = ((JsonString) projectConfig.get("torrents")).getValue();
                System.out.println(glob);

                //Find all torrent files from the given glob
                HashSet<String> torrentFiles = new HashSet<>();
                torrentFiles.addAll(GlobSearch(glob, "", ".torrent"));

                //skip till next project if no torrent files found 
                if(torrentFiles.isEmpty()){
                    continue;
                }

                //add folders to contain this projects torrent files if they dont exist
                File torrentFolderName = new File(torrentFolder + "/" + name);
                File downloadFolderName = new File(downloadFolder + "/" + name);
                if(!torrentFolderName.exists()){
                    torrentFolderName.mkdir();
                }
                if(!downloadFolderName.exists()){
                    downloadFolderName.mkdir();
                }

                //Create a hard link for each torrent file
                for(String f : torrentFiles){
                    String[] flist = f.split("/");
                    String newFilePath = torrentFolder + "/" + name + "/" + flist[flist.length-1];
                    File newFile = new File(newFilePath);
                    if(!newFile.exists()){
                        Path oldPath = Paths.get(f);
                        Path newPath = Paths.get(newFilePath);
                        Files.createLink(newPath, oldPath);
                    }

                    //add non torrent file to downloads directory if it exists in the same glob
                    String newDownloadFile = removeSuffix(flist[flist.length-1], ".torrent");
                    String newDownloadFilePath = downloadFolder + "/" + name + "/" + newDownloadFile;
                    HashSet<String> downloadFiles = new HashSet<>();
                    downloadFiles.addAll(GlobSearch(glob, "", newDownloadFile));
                    for(String fd : downloadFiles){
                        File ndf = new File(newDownloadFilePath);
                        if(!ndf.exists()){
                            Files.createLink(Paths.get(newDownloadFilePath), Paths.get(fd));
                        }
                    }
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public String removeSuffix(String s, String suffix){
        return s.substring(0,s.length()-suffix.length());
    }

    //given a glob string find all the files that match that string
    public HashSet<String> GlobSearch(String glob, String path, String suffix) throws IOException{
        
        //create an array containing each part of the glob string
        String[] globParts = glob.split("/");
        //create a hashset to collect all the files we find into
        HashSet<String> output = new HashSet<>();
        
        if(!glob.equals("")){
            if(globParts[0].equals("*")){

                //remove the first element from the globParts array
                globParts = Arrays.copyOfRange(globParts, 1, globParts.length);
                //join globparts array back into glob
                glob = String.join("/", globParts);

                //call GlobSearch on every directory in the directory
                File dir = new File(path);
                File[] dirList = dir.listFiles();
                for(File f : dirList){
                    if(f.isDirectory()){
                        output.addAll(GlobSearch(glob , f.toString() + "/", suffix));
                    }
                }
                
            }
            else{
                //add first element of array to path
                path += globParts[0] + "/";

                //check to make sure that the path exists
                File dir = new File(path);
                if(!dir.exists()){
                    System.out.println("error: " + path + " not found");
                    return new HashSet<>();
                }

                //remove the first element from the globParts array
                globParts = Arrays.copyOfRange(globParts, 1, globParts.length);
 
                //recursivly call GlobSearch on the cut down array, joined into a string.
                output.addAll(GlobSearch(String.join("/", globParts), path, suffix));
            }
            
        }
        else{
            //base case
            //loop over every file in the directory and test if it ends in the suffix
            File dir = new File(path);
            File[] fileList = dir.listFiles();
            for(File f : fileList){
                if(f.isFile() && f.toString().endsWith(suffix)){
                    output.add(f.toString());
                }
            }
        }
        return output;
    }
}
