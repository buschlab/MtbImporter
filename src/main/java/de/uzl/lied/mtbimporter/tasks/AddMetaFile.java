package de.uzl.lied.mtbimporter.tasks;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.Meta;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Class for processing meta files of a study.
 */
public final class AddMetaFile {

    private AddMetaFile() {
    }

    /**
     * Adds information of meta files to a study.
     * @param study
     * @param m
     * @throws IOException
     */
    public static void processMetaFile(CbioPortalStudy study, File m) throws IOException {
        JavaPropsMapper jpm = new JavaPropsMapper();
        Meta meta = jpm.readValue(m, Meta.class);
        study.addMeta(m.getName(), meta);
        meta.setCancerStudyIdentifier(study.getStudyId());
    }

    /**
     * Writes a single meta pojo to a file.
     * @param m
     * @param f
     * @throws IOException
     */
    public static void writeMetaFile(Meta m, File f) throws IOException {
        JavaPropsMapper jpm = new JavaPropsMapper();
        JavaPropsSchema jps = JavaPropsSchema.emptySchema().withKeyValueSeparator(": ");

        String seqStr = jpm.writer(jps).writeValueAsString(m).replace("\\t", "\t");
        Files.write(f.toPath(), seqStr.getBytes());
    }

}
