package de.uzl.lied.mtbimporter.jobs.mdr.samply;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.DataElement;
import de.samply.common.mdrclient.domain.Result;
import de.samply.common.mdrclient.domain.Slot;
import de.uzl.lied.mtbimporter.model.ClinicalHeader;
import de.uzl.lied.mtbimporter.model.mdr.MdrAttributes;
import de.uzl.lied.mtbimporter.settings.SamplyMdrSettings;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class to fetch attributes from a Samply MDR.
 */
public final class SamplyMdrAttributes {

    private SamplyMdrAttributes() {
    }

    /**
     * Extracts clinical header information for cBioPortal using Samply MDR.
     * @param mdr Configuration for MDR.
     * @param targetProfile
     * @param key
     * @return
     * @throws ExecutionException
     * @throws MdrConnectionException
     * @throws MdrInvalidResponseException
     */
    public static ClinicalHeader getAttributes(SamplyMdrSettings mdr, String targetProfile, String key)
            throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

        String mdrLanguage = mdr.getLanguage();

        MdrClient client = new MdrClient(mdr.getUrl());
        List<Result> namespace = client.getNamespaceMembers(mdrLanguage, mdr.getTargetNamespace());
        Map<String, Result> nameSpaceMap = namespace.stream()
                .collect(Collectors.toMap(r -> r.getDesignations().get(0).getDesignation(), Function.identity()));
        if (nameSpaceMap.get(targetProfile) == null) {
            return null;
        }
        List<Result> dataelements = client.getMembers(nameSpaceMap.get(targetProfile).getId(), mdrLanguage);
        Map<String, Result> dataelementMap = dataelements.stream()
                .collect(Collectors.toMap(r -> r.getDesignations().get(0).getDesignation(), Function.identity()));
        if (dataelementMap.get(key) == null) {
            return null;
        }
        DataElement de = client.getDataElement(dataelementMap.get(key).getId(), mdrLanguage);

        ClinicalHeader ch = new ClinicalHeader();
        for (Slot s : de.getSlots()) {
            switch (MdrAttributes.fromString(s.getSlotName())) {
                case DISPLAYNAME:
                    ch.setDisplayName(s.getSlotValue());
                    break;
                case DATATYPE:
                    ch.setDatatype(s.getSlotValue());
                    break;
                case PRIORITY:
                    ch.setPriority((int) Double.parseDouble(s.getSlotValue()));
                    break;
                case DESCRIPTION:
                    ch.setDescription(s.getSlotValue());
                    break;
                default:
                    break;
            }
        }

        return ch;
    }

}
