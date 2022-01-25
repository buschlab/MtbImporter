package de.uzl.lied.mtbimporter.settings;

/**
 * Sets input folder. New data is in source and will be moved to target once it
 * is processed.
 */
public class InputFolder {

    private String source;
    private String target;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

}
