package de.uzl.lied.mtbimporter.jobs;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import de.uzl.lied.mtbimporter.model.Maf;
import de.uzl.lied.mtbimporter.settings.Settings;

public class EnsemblResolver {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Maf enrich(Maf m) {

        String mutation = m.getTxChange() != null ? m.getTxChange() : m.getHgvspShort();
        if (mutation == null || mutation.equals("") || mutation.equals("MUTATED")) {
            return m;
        }

        RestTemplate rt = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(Settings.getEnsemblUrl() + "/variant_recoder/human/" + m.getHugoSymbol() + ":"
                        + mutation + "?content-type=application/json");
        try {
            ResponseEntity<List> response = rt.getForEntity(builder.build().encode().toUri(), List.class);
            Map<String, Object> map = (Map<String, Object>) response.getBody().get(0);
            if (map.size() != 2) {
                return m;
            }
            for (Entry<String, Object> o : map.entrySet()) {
                if (o.getKey().equals("warnings")) {
                    continue;
                } else {
                    Map<String, List<String>> content = (Map<String, List<String>>) o.getValue();
                    String s = content.get("hgvsg").get(0);
                    String s2 = s.split("g.")[1].replaceAll("[a-zA-Z>]", "");
                    if (s2.matches("_")) {
                        m.setStartPosition(Integer.parseInt(s2.split("-")[0]));
                        m.setEndPosition(Integer.parseInt(s2.split("-")[1]));
                    } else {
                        m.setStartPosition(Integer.parseInt(s2));
                        m.setEndPosition(Integer.parseInt(s2));
                    }
                    Integer chr = Integer.parseInt(s.replaceAll("NC_", "").split("\\.")[0]);
                    switch (chr) {
                    case 23:
                        m.setChromosome("chrX");
                    case 24:
                        m.setChromosome("chrY");
                    default:
                        m.setChromosome("chr" + chr);
                    }
                }
            }

        } catch (final Exception e) {
            System.err.println("Error when trying to revolve using Ensembl.");
        }

        return m;
    }

}
