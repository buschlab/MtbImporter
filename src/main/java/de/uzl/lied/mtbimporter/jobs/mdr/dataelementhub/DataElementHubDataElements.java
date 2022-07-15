package de.uzl.lied.mtbimporter.jobs.mdr.dataelementhub;

import de.dataelementhub.model.dto.element.section.Slot;
import de.dataelementhub.model.dto.listviews.NamespaceMember;
import de.uzl.lied.mtbimporter.settings.DataElementHubSettings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.tinylog.Logger;

/**
 * Class for accessing items stored in a Samply MDR.
 */
public final class DataElementHubDataElements {

    private static final Map<String, Map<String, Map<String, String>>> CACHE = new HashMap<>();

    private DataElementHubDataElements() {
    }

    /**
     * Get all items of a profile for a specific namespace.
     *
     * @param oldMdr
     * @param targetNamespace
     * @param targetProfile
     * @return
     */
    public static Map<String, Map<String, String>> get(DataElementHubSettings oldMdr, String targetNamespace,
            String targetProfile) {

        if (!CACHE.containsKey(targetNamespace + "_" + targetProfile)) {

            DataElementHubSettings mdr = oldMdr;

            if (mdr.isTokenExpired()) {
                mdr = DataElementHubLogin.login(mdr);
            }

            try {

                RestTemplate rt = new RestTemplate();
                int id = DataElementHubNamespace.getNameSpaceId(mdr, targetNamespace);

                ParameterizedTypeReference<List<NamespaceMember>> namespaceMembersType =
                        new ParameterizedTypeReference<List<NamespaceMember>>() {};
                UriComponentsBuilder builderMember = UriComponentsBuilder
                        .fromHttpUrl(mdr.getUrl() + "/namespaces/" + id + "/members?elementType=DATAELEMENT");
                HttpHeaders headersMembers = new HttpHeaders();
                headersMembers.add(HttpHeaders.ACCEPT, "application/vnd+de.dataelementhub.listview+json");
                headersMembers.add("Authorization", "Bearer " + mdr.getToken());
                Optional<ResponseEntity<List<NamespaceMember>>> elementsE = Optional.of(rt.exchange(builderMember
                        .build().toUri(), HttpMethod.GET, new HttpEntity<>(headersMembers), namespaceMembersType));
                List<NamespaceMember> elements = elementsE.get().getBody();
                if (elements == null) {
                    return null;
                }
                Map<String, Map<String, String>> m = new HashMap<>();
                elements.forEach(e -> {
                    Map<String, String> n = new HashMap<>();
                    String dataElement = "urn:" + id + ":dataelement:" + e.getIdentifier() + ":"
                            + e.getRevision();

                    UriComponentsBuilder builderSlots = UriComponentsBuilder
                            .fromHttpUrl(oldMdr.getUrl() + "/element/" + dataElement + "/slots");
                    ParameterizedTypeReference<List<Slot>> slotsType = new ParameterizedTypeReference<List<Slot>>() {
                    };
                    ResponseEntity<List<Slot>> slotsE = rt.exchange(builderSlots.build().toUri(), HttpMethod.GET,
                            new HttpEntity<>(headersMembers), slotsType);
                    for (Slot s : slotsE.getBody()) {
                        n.put(s.getName(), s.getValue());
                    }
                    m.put(e.getDefinitions().get(0).getDesignation(), n);
                });

                CACHE.put(targetNamespace + "_" + targetProfile, m);

            } catch (

            final HttpClientErrorException e) {
                Logger.error("Object not found in MDR!");
                return null;
            }
        }

        return CACHE.get(targetNamespace + "_" + targetProfile);

    }

}
