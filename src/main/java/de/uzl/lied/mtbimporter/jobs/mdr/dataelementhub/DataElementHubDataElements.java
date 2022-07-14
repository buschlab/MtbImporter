package de.uzl.lied.mtbimporter.jobs.mdr.dataelementhub;

import de.dataelementhub.dal.jooq.enums.AccessLevelType;
import de.dataelementhub.model.dto.element.Namespace;
import de.dataelementhub.model.dto.element.section.Slot;
import de.dataelementhub.model.dto.listviews.NamespaceMember;
import de.uzl.lied.mtbimporter.settings.DataElementHubSettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.tinylog.Logger;

/**
 * Class for accessing items stored in a Samply MDR.
 */
public final class DataElementHubDataElements {

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

        DataElementHubSettings mdr = oldMdr;

        if (mdr.isTokenExpired()) {
            mdr = DataElementHubLogin.login(mdr);
        }

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(mdr.getUrl() + "/namespaces");
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
        headers.add("Authorization", "Bearer " + mdr.getToken());
        try {
            ParameterizedTypeReference<Map<AccessLevelType, List<Namespace>>> responseType =
                    new ParameterizedTypeReference<Map<AccessLevelType, List<Namespace>>>() {};
            ResponseEntity<Map<AccessLevelType, List<Namespace>>> response = rt.exchange(
                    builder.build().encode().toUri(), HttpMethod.GET,
                    new HttpEntity<>(headers), responseType);

            Map<AccessLevelType, List<Namespace>> ns = response.getBody();

            List<Namespace> namespaces = new ArrayList<>();
            ns.values().forEach(v -> namespaces.addAll(v));
            List<Namespace> l = new ArrayList<>();
            namespaces.forEach(n -> {
                n.getDefinitions().forEach(d -> {
                    if (d.getDesignation().equals(targetNamespace)) {
                        l.add(n);
                    }
                });
            });
            if (l.size() != 1) {
                return null;
            }
            int id = l.get(0).getIdentification().getIdentifier();

            ParameterizedTypeReference<List<NamespaceMember>> namespaceMembersType =
                    new ParameterizedTypeReference<List<NamespaceMember>>() {};
            UriComponentsBuilder builderMember = UriComponentsBuilder
                    .fromHttpUrl(mdr.getUrl() + "/namespaces/" + id + "/members?elementType=DATAELEMENT");
            HttpHeaders headersMembers = new HttpHeaders();
            headersMembers.add(HttpHeaders.ACCEPT, "application/vnd+de.dataelementhub.listview+json");
            headersMembers.add("Authorization", "Bearer " + mdr.getToken());
            ResponseEntity<List<NamespaceMember>> elementsE = rt.exchange(builderMember.build().toUri(), HttpMethod.GET,
                    new HttpEntity<>(headersMembers), namespaceMembersType);
            List<NamespaceMember> elements = elementsE.getBody();
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

            return m;

        } catch (

        final HttpClientErrorException e) {
            Logger.error("Object not found in MDR!");
            return null;
        }
    }

}
