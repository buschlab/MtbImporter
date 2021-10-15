package de.uzl.lied.mtbimporter.jobs.mdr.samply;

import java.util.function.Function;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.codehaus.groovy.control.CompilationFailedException;

import java.util.concurrent.ExecutionException;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Concept;
import de.samply.common.mdrclient.domain.DataElement;
import de.samply.common.mdrclient.domain.Result;
import de.uzl.lied.mtbimporter.model.mdr.centraxx.RelationConvert;
import de.uzl.lied.mtbimporter.settings.SamplyMdrSettings;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class SamplyMdrConvert {

    public static RelationConvert convert(SamplyMdrSettings mdr, RelationConvert input) throws ExecutionException,
            MdrConnectionException, MdrInvalidResponseException, CompilationFailedException, IOException {

        RelationConvert output = new RelationConvert();
        output.setTargetProfileVersion(input.getTargetProfileVersion());
        output.setTargetProfileCode(input.getTargetProfileCode());
        output.setValues(new HashMap<String, Object>());

        String mdrLanguage = mdr.getLanguage();

        MdrClient client = new MdrClient(mdr.getUrl());
        List<Result> namespace = client.getNamespaceMembers(mdrLanguage, mdr.getSourceNamespace());
        Map<String, Result> nameSpaceMap = namespace.stream()
                .collect(Collectors.toMap(r -> r.getDesignations().get(0).getDesignation(), Function.identity()));
        if (nameSpaceMap.get(input.getSourceProfileCode()) == null) {
            return output;
        }
        List<Result> dataelements = client.getMembers(nameSpaceMap.get(input.getSourceProfileCode()).getId(), mdrLanguage);
        Map<String, Result> dataelementMap = dataelements.stream()
                .collect(Collectors.toMap(r -> r.getDesignations().get(0).getDesignation(), Function.identity()));

        Binding b = new Binding();
        for (Entry<String, Object> bE : input.getValues().entrySet()) {
            b.setVariable(bE.getKey(), bE.getValue());
        }

        for (Entry<String, Object> e : input.getValues().entrySet()) {
            if (dataelementMap.get(e.getKey()) == null) {
                continue;
            }
            DataElement de = client.getDataElement(dataelementMap.get(e.getKey()).getId(), mdrLanguage);
            for (Concept c : de.getConcepts()) {
                if (c.getConceptSystem().equals("cbioportal")
                        && c.getConceptTerm().equals(input.getTargetProfileCode())) {
                    if (c.getConceptLinktype().equals("equal")) {
                        output.getValues().put(c.getConceptText(), e.getValue());
                    } else {
                        GroovyShell s = new GroovyShell(b);
                        b.setVariable("src", e.getValue());
                        Object target = s.evaluate(new File(
                                "mapper/" + input.getTargetProfileCode() + "/" + e.getKey().toLowerCase() + ".groovy"));
                        output.getValues().put(c.getConceptText(), target);
                    }
                }
            }
        }

        return output;
    }

}
