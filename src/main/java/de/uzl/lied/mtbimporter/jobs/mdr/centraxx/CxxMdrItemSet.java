package de.uzl.lied.mtbimporter.jobs.mdr.centraxx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import de.uzl.lied.mtbimporter.model.mdr.centraxx.CxxField;
import de.uzl.lied.mtbimporter.model.mdr.centraxx.CxxForm;
import de.uzl.lied.mtbimporter.model.mdr.centraxx.CxxItem;
import de.uzl.lied.mtbimporter.model.mdr.centraxx.CxxItemSet;
import de.uzl.lied.mtbimporter.model.mdr.centraxx.CxxSection;
import de.uzl.lied.mtbimporter.settings.CxxMdrSettings;

public class CxxMdrItemSet {

    private final static Map<String, CxxItemSet> cache = new HashMap<String, CxxItemSet>();

    public static CxxItemSet get(CxxMdrSettings mdr, String itemSet) {

        if (!cache.containsKey(itemSet)) {
            if (mdr.isTokenExpired()) {
                CxxMdrLogin.login(mdr);
            }
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
            headers.add("Authorization", "Bearer " + mdr.getToken());
            ResponseEntity<CxxItemSet> response = rt.exchange(
                    mdr.getUrl() + "/rest/v1/itemsets/itemset?code=" + itemSet, HttpMethod.GET,
                    new HttpEntity<>(headers), CxxItemSet.class);
            cache.put(itemSet, response.getBody());
        }
        return cache.get(itemSet);
    }

    public static List<CxxItem> getItemList(CxxItemSet itemSet) {
        List<CxxItem> items = new ArrayList<CxxItem>();
        items.addAll(itemSet.getItems());
        return items;
    }

    public static List<CxxItem> getItemList(CxxForm form) {
        List<CxxItem> items = new ArrayList<CxxItem>();
        for (CxxField f : form.getFields()) {
            items.add(f.getItem());
        }
        items.addAll(extractFromSections(form.getSections()));
        return items;
    }

    private static List<CxxItem> extractFromSections(List<CxxSection> sections) {
        List<CxxItem> items = new ArrayList<CxxItem>();
        for (CxxSection s : sections) {
            for (CxxField f : s.getFields()) {
                items.add(f.getItem());
            }
            for (CxxSection subSection : sections) {
                extractFromSections(subSection.getSections());
            }
        }
        return items;
    }

}
