package de.uzl.lied.mtbimporter.jobs.mdr.dataelementhub;

import de.dataelementhub.dal.jooq.enums.AccessLevelType;
import de.dataelementhub.model.dto.element.Namespace;
import de.uzl.lied.mtbimporter.settings.DataElementHubSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Utility class for resolving namespace name to id.
 */
public final class DataElementHubNamespace {

    private DataElementHubNamespace() {
    }

    /**
     * Obtains namespace id from namespace name.
     * @param mdr
     * @param namespace
     * @return
     */
    public static int getNameSpaceId(DataElementHubSettings mdr, String namespace) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(mdr.getUrl() + "/namespaces");
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
        headers.add("Authorization", "Bearer " + mdr.getToken());

        ParameterizedTypeReference<Map<AccessLevelType, List<Namespace>>> responseType =
                new ParameterizedTypeReference<Map<AccessLevelType, List<Namespace>>>() {};
        ResponseEntity<Map<AccessLevelType, List<Namespace>>> response = rt.exchange(
                builder.build().encode().toUri(), HttpMethod.GET,
                new HttpEntity<>(headers), responseType);

        Map<AccessLevelType, List<Namespace>> ns = response.getBody();
        if (ns == null) {
            return 0;
        }
        List<Namespace> namespaces = new ArrayList<>();
        ns.values().forEach(namespaces::addAll);
        List<Namespace> l = new ArrayList<>();
        namespaces.forEach(n -> n.getDefinitions().forEach(d -> {
            if (d.getDesignation().equals(namespace)) {
                l.add(n);
            }
        }));
        if (l.size() != 1) {
            return 0;
        }
        return l.get(0).getIdentification().getIdentifier();
    }

}
