package Model;

public class PathPreset {
    private final String presetName;
    private final String sourcePathName;
    private final String destinationPathName;

    public String getPresetName() {
        return presetName;
    }

    public String getSourcePathName() {
        return sourcePathName;
    }

    public String getDestinationPathName() {
        return destinationPathName;
    }

    public PathPreset(String presetName, String sourcePathName, String destinationPathName) {
        this.presetName = presetName;
        this.sourcePathName = sourcePathName;
        this.destinationPathName = destinationPathName;
    }
}
