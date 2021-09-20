package de.uzl.lied.mtbimporter.jobs.mdr.samply;

import java.util.function.Function;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.DataElement;
import de.samply.common.mdrclient.domain.Result;
import de.samply.common.mdrclient.domain.Slot;
import de.uzl.lied.mtbimporter.model.ClinicalHeader;
import de.uzl.lied.mtbimporter.settings.SamplyMdrSettings;

public class SamplyMdrAttributes {

    public static ClinicalHeader getAttributes(SamplyMdrSettings mdr, String targetProfile, String key)
            throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

        String mdrLanguage = mdr.getLanguage();

        MdrClient client = new MdrClient(mdr.getUrl());
        List<Result> namespace = client.getNamespaceMembers(mdrLanguage, mdr.getNamespace());
        Map<String, Result> nameSpaceMap = namespace.stream()
                .collect(Collectors.toMap(r -> r.getDesignations().get(0).getDesignation(), Function.identity()));
        if(nameSpaceMap.get(targetProfile) == null) {
            return null;
        }
        List<Result> dataelements = client.getMembers(nameSpaceMap.get(targetProfile).getId(), mdrLanguage);
        Map<String, Result> dataelementMap = dataelements.stream()
                .collect(Collectors.toMap(r -> r.getDesignations().get(0).getDesignation(), Function.identity()));
        if(dataelementMap.get(key) == null) {
            return null;
        }
        DataElement de = client.getDataElement(dataelementMap.get(key).getId(), mdrLanguage);

        ClinicalHeader ch = new ClinicalHeader();
        for (Slot s : de.getSlots()) {
            switch (s.getSlotName()) {
                case "display-name":
                    ch.setDisplayName(s.getSlotValue());
                    break;
                case "datatype":
                    ch.setDatatype(s.getSlotValue());
                    break;
                case "priority":
                    ch.setPriority((int) Double.parseDouble(s.getSlotValue()));
                    break;
                case "description":
                    ch.setDescription(s.getSlotValue());
                    break;
            }
        }

        return ch;
    }

}
