package de.uzl.lied.mtbimporter.model.mdr.centraxx;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_NULL)
public class RelationConvert {

@JsonProperty("srcProfileCode")
private String sourceProfileCode;
@JsonProperty("srcProfileVersion")
private String sourceProfileVersion;
@JsonProperty("trgProfileCode")
private String targetProfileCode;
@JsonProperty("trgProfileVersion")
private String targetProfileVersion;
@JsonProperty("values")
private Map<String, Object> values;
@JsonProperty("logMessages")
private String[] logMessages;

public String getSourceProfileCode() {
    return sourceProfileCode;
}

public void setSourceProfileCode(String sourceProfileCode) {
    this.sourceProfileCode = sourceProfileCode;
}

public String getSourceProfileVersion() {
    return sourceProfileVersion;
}

public void setSourceProfileVersion(String sourceProfileVersion) {
    this.sourceProfileVersion = sourceProfileVersion;
}

public String getTargetProfileCode() {
    return targetProfileCode;
}

public void setTargetProfileCode(String targetProfileCode) {
    this.targetProfileCode = targetProfileCode;
}

public String getTargetProfileVersion() {
    return targetProfileVersion;
}

public void setTargetProfileVersion(String targetProfileVersion) {
    this.targetProfileVersion = targetProfileVersion;
}

public Map<String, Object> getValues() {
    return values;
}

public void setValues(Map<String, Object> values) {
    this.values = values;
}

public String[] getLogMessages() {
    return logMessages;
}

}
