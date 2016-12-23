package copy;

public class CopyState {
    private String fileIn, fileOut;

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
}
