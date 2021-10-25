package de.uzl.lied.mtbimporter.tasks;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.Meta;

public class AddMetaFile {

    public static void processMetaFile(CbioPortalStudy study, File m) throws StreamReadException, DatabindException, IOException {
        JavaPropsMapper jpm = new JavaPropsMapper();
        Meta meta = jpm.readValue(m, Meta.class);
        study.addMeta(m.getName(), meta);
        study.setStudyId(meta.getCancerStudyIdentifier());
    }

}
