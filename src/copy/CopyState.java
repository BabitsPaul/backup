package copy;

import java.io.File;

public class CopyState {
    private String fileIn, fileOut;

    private File currentFile;
    private long currentFileProgress;
    private long currentFileLength;

    private int totalFileProgress = 0;
    private int totalFiles;

    private long totalBytes;
    private long totalBytesProgress = 0l;

    private boolean precomputationComplete = false;

    public CopyState(String fileIn, String fileOut)
    {
        this.fileIn = fileIn;
        this.fileOut = fileOut;
    }

    public String getFileIn() {
        return fileIn;
    }

    public String getFileOut() {
        return fileOut;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
        currentFileLength = currentFile.length();
        currentFileProgress = 0l;
        totalFileProgress++;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public long getCurrentFileProgress() {
        return currentFileProgress;
    }

    public long getCurrentFileLength() {
        return currentFileLength;
    }

    public void currentFileProgress(int progress)
    {
        currentFileProgress += progress;
        totalBytesProgress += progress;
    }

    //precomputation
    public void precomputationComplete(int totalFiles, long totalBytes) {
        this.totalFiles = totalFiles;
        this.totalBytes = totalBytes;

        precomputationComplete = true;
    }

    public boolean isPrecomputationComplete() {
        return precomputationComplete;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public long getTotalBytesProgress() {
        return totalBytesProgress;
    }

    public int getTotalFileProgress() {
        return totalFileProgress;
    }
}
