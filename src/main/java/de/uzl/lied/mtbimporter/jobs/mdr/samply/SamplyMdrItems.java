package de.uzl.lied.mtbimporter.jobs.mdr.samply;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Result;
import de.samply.common.mdrclient.domain.Slot;
import de.uzl.lied.mtbimporter.settings.SamplyMdrSettings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class for accessing items stored in a Samply MDR.
 */
public final class SamplyMdrItems {

    private SamplyMdrItems() {
    }

    /**
     * Get all items of a profile for a specific namespace.
     * @param mdr
     * @param targetNamespace
     * @param targetProfile
     * @return
     * @throws ExecutionException
     * @throws MdrConnectionException
     * @throws MdrInvalidResponseException
     */
    public static Map<String, Map<String, String>> get(SamplyMdrSettings mdr, String targetNamespace,
            String targetProfile)
            throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {

        String mdrLanguage = mdr.getLanguage();

        MdrClient client = new MdrClient(mdr.getUrl());
        List<Result> namespace = client.getNamespaceMembers(mdrLanguage, targetNamespace);
        Map<String, Result> nameSpaceMap = namespace.stream()
                .collect(Collectors.toMap(r -> r.getDesignations().get(0).getDesignation(), Function.identity()));
        if (nameSpaceMap.get(targetProfile) == null) {
            return null;
        }
        List<Result> dataelements = client.getMembers(nameSpaceMap.get(targetProfile).getId(), mdrLanguage);
        Map<String, Map<String, String>> m = new HashMap<String, Map<String, String>>();
        for (Result r : dataelements) {
            String key = r.getDesignations().get(0).getDesignation();
            List<Slot> ls = client.getDataElement(r.getId(), mdrLanguage).getSlots();
            Map<String, String> n = new HashMap<String, String>();
            for (Slot s : ls) {
                n.put(s.getSlotName(), s.getSlotValue());
            }
            m.put(key, n);
        }
        return m;
    }

}
