package mirrortorrent.torrents;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.lavajuno.lucidjson.JsonObject;
import org.lavajuno.lucidjson.JsonString;

import mirrortorrent.io.Log;

public class TorrentSyncer implements Runnable {

    final private JsonObject mirrorsConfig;
    final private File torrentFolder;
    final private File downloadFolder;

    public TorrentSyncer(JsonObject mirrorsConfig, File torrentsDirectory, File downloadsDirectory) {
        this.mirrorsConfig = mirrorsConfig;
        this.torrentFolder = torrentsDirectory;
        this.downloadFolder = downloadsDirectory;
    }

    @Override
    public void run() {
        Log log = Log.getInstance();

        for (String projectName : mirrorsConfig.getKeys()) {
            JsonObject projectConfig = (JsonObject) mirrorsConfig.get(projectName);
            JsonString projectTorrents = (JsonString) projectConfig.get("torrents");

            if (projectTorrents == null) {
                continue;
            }

            log.info("Syncing torrents for " + projectName);

            //get the glob string for the mirrors.json project
            String glob = projectTorrents.getValue();
            System.out.println(glob);

            //Find all torrent files from the given glob
            HashSet<String> torrentFiles = new HashSet<>();
            try {
                torrentFiles.addAll(GlobSearch(glob, "", ".torrent"));
            } catch (IOException e) {
                log.warn(e.getMessage());
                continue;
            }

            // Continue if there are no torrent files to sync
            if (torrentFiles.isEmpty()) {
                continue;
            }

            // Create project directories if they do not already exist
            File torrentFolderName = new File(torrentFolder + "/" + projectName);
            if (!torrentFolderName.exists()) {
                torrentFolderName.mkdir();
            }
            File downloadFolderName = new File(downloadFolder + "/" + projectName);
            if (!downloadFolderName.exists()) {
                downloadFolderName.mkdir();
            }

            //Create a hard link for each torrent file
            for (String file : torrentFiles) {
                List<String> fileList = Arrays.asList(file.split("/"));

                String newFilePath = torrentFolder + "/" + projectName + "/" + fileList.getLast();
                File newFile = new File(newFilePath);
                try {
                    if (!newFile.exists()) {
                        Path oldPath = Paths.get(file);
                        Path newPath = Paths.get(newFilePath);
                        Files.createLink(newPath, oldPath);
                    }
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }

                //add non torrent file to downloads directory if it exists in the same glob
                String newDownloadFile = removeSuffix(fileList.getLast(), ".torrent");
                String newDownloadFilePath = downloadFolder + "/" + projectName + "/" + newDownloadFile;
                HashSet<String> downloadFiles = new HashSet<>();

                try {
                    downloadFiles.addAll(GlobSearch(glob, "", newDownloadFile));
                    for (String fd : downloadFiles) {
                        File ndf = new File(newDownloadFilePath);
                        if (!ndf.exists()) {
                            Files.createLink(Paths.get(newDownloadFilePath), Paths.get(fd));
                        }
                    }
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
            }

        }
    }

    public String removeSuffix(String s, String suffix) {
        return s.substring(0, s.length() - suffix.length());
    }

    //given a glob string find all the files that match that string
    public HashSet<String> GlobSearch(String glob, String path, String suffix) throws IOException {

        //create an array containing each part of the glob string
        String[] globParts = glob.split("/");
        //create a hashset to collect all the files we find into
        HashSet<String> output = new HashSet<>();

        if (!glob.equals("")) {
            if (globParts[0].equals("*")) {

                //remove the first element from the globParts array
                globParts = Arrays.copyOfRange(globParts, 1, globParts.length);
                //join globparts array back into glob
                glob = String.join("/", globParts);

                //call GlobSearch on every directory in the directory
                File dir = new File(path);
                File[] dirList = dir.listFiles();
                for (File f : dirList) {
                    if (f.isDirectory()) {
                        output.addAll(GlobSearch(glob, f.toString() + "/", suffix));
                    }
                }

            } else {
                //add first element of array to path
                path += globParts[0] + "/";

                //check to make sure that the path exists
                File dir = new File(path);
                if (!dir.exists()) {
                    System.out.println("error: " + path + " not found");
                    return new HashSet<>();
                }

                //remove the first element from the globParts array
                globParts = Arrays.copyOfRange(globParts, 1, globParts.length);

                //recursivly call GlobSearch on the cut down array, joined into a string.
                output.addAll(GlobSearch(String.join("/", globParts), path, suffix));
            }

        } else {
            //base case
            //loop over every file in the directory and test if it ends in the suffix
            File dir = new File(path);
            File[] fileList = dir.listFiles();
            for (File f : fileList) {
                if (f.isFile() && f.toString().endsWith(suffix)) {
                    output.add(f.toString());
                }
            }
        }
        return output;
    }
}
