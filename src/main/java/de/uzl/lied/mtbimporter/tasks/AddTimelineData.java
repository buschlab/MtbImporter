package de.uzl.lied.mtbimporter.tasks;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.Timeline;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Process timeline files and objects.
 */
public final class AddTimelineData {

    private AddTimelineData() {
    }

    /**
     * Adds timeline data files to study.
     * @param study
     * @param timeline
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static void processTimelineFile(CbioPortalStudy study, File timeline)
            throws JsonGenerationException, JsonMappingException, IOException {
        Class<?> c = getTimelineClass(timeline.getName().replaceFirst("data_timeline_", "").replace(".txt", ""));
        processTimelineClass(study, readTimelineFile(timeline, c),
                timeline.getName().replaceFirst("data_timeline_", "").replace(".txt", ""));
    }

    /**
     * Reads timeline tsv files to jvm Timeline objects.
     * @param <T>
     * @param input
     * @param c
     * @return
     * @throws IOException
     */
    public static <T> List<T> readTimelineFile(File input, Class<T> c) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(c)
                .with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator('\t'));
        MappingIterator<T> inputIterator = or.readValues(input);
        return inputIterator.readAll();
    }

    /**
     * Writes jvm Timeline objects to tsv files.
     * @param timelines
     * @param type
     * @param target
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static void writeTimelineFile(Collection<Timeline> timelines, String type, File target)
            throws JsonGenerationException, JsonMappingException, IOException {
        Class<?> c = getTimelineClass(type);
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        CsvSchema s = om.schemaFor(c).withHeader().withColumnSeparator('\t').withoutQuoteChar();
        om.writer(s.withHeader().withColumnSeparator('\t').withoutQuoteChar()).writeValue(target, timelines);
    }

    private static void processTimelineClass(CbioPortalStudy study, Timeline t, String type)
            throws JsonGenerationException, JsonMappingException, IOException {

        if (t.getStartDate() == null || t.getStopDate() == null) {
            return;
        }
        study.addTimeline(type, t);
    }

    private static <T> void processTimelineClass(CbioPortalStudy study, Collection<T> t, String type)
            throws JsonGenerationException, JsonMappingException, IOException {
        for (T line : t) {
            processTimelineClass(study, (Timeline) line, type);
        }
    }

    private static Class<?> getTimelineClass(String str) {
        Class<?> c;
        try {
            c = Class.forName(
                    "de.uzl.lied.mtbimporter.model.Timeline" + str.substring(0, 1).toUpperCase() + str.substring(1));
        } catch (ClassNotFoundException e) {
            c = Timeline.class;
        }
        return c;
    }

}
