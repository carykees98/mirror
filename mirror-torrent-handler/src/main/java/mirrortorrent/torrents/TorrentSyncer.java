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

import mirrortorrent.io.Log;

public class TorrentSyncer implements Runnable {

    final private JsonObject mirrorsConfig;
    final private File torrentFolder;
    final private File downloadFolder;

    public TorrentSyncer(
            final JsonObject mirrorsConfig,
            final File torrentsDirectory,
            final File downloadsDirectory) {
        this.mirrorsConfig = mirrorsConfig;
        this.torrentFolder = torrentsDirectory;
        this.downloadFolder = downloadsDirectory;
    }

    @Override
    public void run() {
        Log log = Log.getInstance();

        for (final String projectName : mirrorsConfig.keys()) {
            final JsonObject projectConfig = (JsonObject) mirrorsConfig.get(projectName);
            final String torrentGlobString = projectConfig.get("torrents").toString();

            if (torrentGlobString == null) {
                continue;
            }

            log.info("Syncing torrents for " + projectName);

            // Find all torrent files from the given torrentGlobString
            HashSet<String> torrentFiles = null;
            try {
                torrentFiles = GlobSearch(torrentGlobString, new File(""), ".torrent");
            } catch (IOException e) {
                log.warn(e.getMessage());
                continue;
            }

            // Continue if there are no torrent files to sync
            if (torrentFiles.isEmpty()) {
                continue;
            }

            // Create project directories if they do not already exist
            final File projectTorrentFolder = torrentFolder
                    .toPath()
                    .resolve(projectName)
                    .toFile();
            if (!projectTorrentFolder.exists()) {
                projectTorrentFolder.mkdir();
            }

            final File projectDownloadFolder = downloadFolder
                    .toPath()
                    .resolve(projectName)
                    .toFile();
            if (!projectDownloadFolder.exists()) {
                projectDownloadFolder.mkdir();
            }

            // Create a hard link for each torrent absoluteFilePath
            for (final String absoluteFilePath : torrentFiles) {
                final List<String> absolutePathComponents = Arrays.asList(absoluteFilePath.split("/"));

                final File linkLocation = torrentFolder
                        .toPath()
                        .resolve(projectName)
                        .resolve(absolutePathComponents.getLast())
                        .toFile();

                if (!linkLocation.exists()) {
                    try {
                        Path existingLocation = Paths.get(absoluteFilePath);
                        Files.createLink(linkLocation.toPath(), existingLocation);
                    } catch (IOException e) {
                        log.warn(e.getMessage());
                    }
                }

                // Add non torrent absoluteFilePath to downloads directory if it exists in the same torrentGlobString
                final File newDownloadFile = downloadFolder
                        .toPath()
                        .resolve(projectName)
                        .resolve(removeSuffix(absolutePathComponents.getLast(), ".torrent"))
                        .toFile();

                try {
                    for (final String foundFile
                            : GlobSearch(torrentGlobString, new File("/"), newDownloadFile.getPath())) {
                        if (!newDownloadFile.exists()) {
                            Files.createLink(newDownloadFile.toPath(), Paths.get(foundFile));
                        }
                    }
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
            }
        }
    }

    public String removeSuffix(final String s, final String suffix) {
        return s.substring(0, s.length() - suffix.length());
    }

    // Given a glob string, recursively find all the files that match that string
    public HashSet<String> GlobSearch(
            String globString,
            File directoryToSearch,
            final String suffix) throws IOException {
        // Create an array containing each part of the torrentGlobString string
        List<String> globParts = Arrays.asList(globString.split("/"));
        // Create a hashset to collect all the files we find into
        HashSet<String> foundFiles = new HashSet<>();

        if (!globString.equals("")) {
            if (globParts.get(0).equals("*")) {
                // Remove the 0th element from the globParts array
                globParts.remove(0);

                // Join globparts array back into torrentGlobString
                globString = String.join("/", globParts);

                // Call GlobSearch on every sub directory in the current directory
                for (final File item : directoryToSearch.listFiles()) {
                    if (item.isDirectory()) {
                        foundFiles.addAll(GlobSearch(globString, item, suffix));
                    }
                }
            } else {
                directoryToSearch = directoryToSearch
                        .toPath()
                        .resolve(globParts.get(0))
                        .toFile();

                // Check to make sure that the directoryToSearch exists
                if (!directoryToSearch.exists()) {
                    System.out.println("error: " + directoryToSearch + " not found");
                    return new HashSet<>();
                }

                // Remove the 0th element from the globParts array
                globParts.remove(0);

                // Recursivly call GlobSearch on the cut down array, joined into a string.
                foundFiles.addAll(GlobSearch(String.join("/", globParts), directoryToSearch, suffix));
            }
        } else {
            // Base case
            // Loop over every absoluteFilePath in the directory and test if it ends in the suffix
            for (final File item : directoryToSearch.listFiles()) {
                if (item.isFile() && item.getPath().endsWith(suffix)) {
                    foundFiles.add(item.getPath());
                }
            }
        }

        return foundFiles;
    }
}
