package copy;

public class CopyState {
    private String currentFile;

    private int files,
                filesCopied;

    private long totalBytes,
                    totalBytesCopied;

    private long fileBytes,
                    fileBytesCopied;

    private boolean precomputationComplete;

    private boolean complete;

    private String inputFile, outputFile;

    public CopyState(String inputFile, String outputFile)
    {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    //TODO condense setters

    public String getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(String currentFile) {
        this.currentFile = currentFile;
    }

    public int getFiles() {
        return files;
    }

    public void setFiles(int files) {
        this.files = files;
    }

    public int getFilesCopied() {
        return filesCopied;
    }

    public void setFilesCopied(int filesCopied) {
        this.filesCopied = filesCopied;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public long getTotalBytesCopied() {
        return totalBytesCopied;
    }

    public void setTotalBytesCopied(long totalBytesCopied) {
        this.totalBytesCopied = totalBytesCopied;
    }

    public long getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(long fileBytes) {
        this.fileBytes = fileBytes;
    }

    public long getFileBytesCopied() {
        return fileBytesCopied;
    }

    public void setFileBytesCopied(long fileBytesCopied) {
        this.fileBytesCopied = fileBytesCopied;
    }

    public boolean isPrecomputationComplete() {
        return precomputationComplete;
    }

    public void setPrecomputationComplete(boolean precomputationComplete) {
        this.precomputationComplete = precomputationComplete;
    }

    public boolean isCopyingComplete() {
        return complete;
    }

    public void setCopyingComplete(boolean complete) {
        this.complete = complete;
    }


    public String getInputFile() {
        return inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }
}
