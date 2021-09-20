package de.uzl.lied.mtbimporter.tasks;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.Timeline;
import de.uzl.lied.mtbimporter.model.TimelineImaging;
import de.uzl.lied.mtbimporter.model.TimelineLabTest;
import de.uzl.lied.mtbimporter.model.TimelineSpecimen;
import de.uzl.lied.mtbimporter.model.TimelineStatus;
import de.uzl.lied.mtbimporter.model.TimelineTreatment;

public class AddTimelineData {

    public static void processTimelineData(CbioPortalStudy study, List<Timeline> timeline)
            throws JsonGenerationException, JsonMappingException, IOException {
        for (Timeline t : timeline) {
            switch (t.getClass().getSimpleName()) {
                case "Timeline":
                    processTimlineClass(study, t, Timeline.class);
                    break;
                case "TimelineImaging":
                    processTimlineClass(study, t, TimelineImaging.class);
                    break;
                case "TimelineLabTest":
                    processTimlineClass(study, t, TimelineLabTest.class);
                    break;
                case "TimelineSpecimen":
                    processTimlineClass(study, t, TimelineSpecimen.class);
                    break;
                case "TimelineStatys":
                    processTimlineClass(study, t, TimelineStatus.class);
                    break;
                case "TimelineTreatment":
                    processTimlineClass(study, t, TimelineTreatment.class);
                    break;
            }
        }
    }

    public static void processTimelineFile(CbioPortalStudy study, File timeline)
            throws JsonGenerationException, JsonMappingException, IOException {
        switch (timeline.getName().replaceFirst("data_timeline_", "")) {
            case "imaging":
                processTimlineClass(study, readTimelineFile(timeline, TimelineImaging.class), TimelineImaging.class);
                break;
            case "labtest":
                processTimlineClass(study, readTimelineFile(timeline, TimelineLabTest.class), TimelineLabTest.class);
                break;
            case "specimen":
                processTimlineClass(study, readTimelineFile(timeline, TimelineSpecimen.class), TimelineSpecimen.class);
                break;
            case "status":
                processTimlineClass(study, readTimelineFile(timeline, TimelineStatus.class), TimelineStatus.class);
                break;
            case "treatment":
                processTimlineClass(study, readTimelineFile(timeline, TimelineTreatment.class), TimelineTreatment.class);
                break;
            default:
                processTimlineClass(study, readTimelineFile(timeline, Timeline.class), Timeline.class);
                break;
        }
    }

    public static <T> List<T> readTimelineFile(File input, Class<T> c) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(c)
                .with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator('\t'));
        MappingIterator<T> inputIterator = or.readValues(input);
        return inputIterator.readAll();
    }

    public static <T> void writeTimelineFile(Collection<Timeline> timelines, Class<T> c, File target)
            throws JsonGenerationException, JsonMappingException, IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        CsvSchema s = om.schemaFor(c).withHeader().withColumnSeparator('\t').withoutQuoteChar();
        om.writer(s.withHeader().withColumnSeparator('\t').withoutQuoteChar()).writeValue(target, timelines);
    }

    private static <T> void processTimlineClass(CbioPortalStudy study, Timeline t, Class<T> c)
            throws JsonGenerationException, JsonMappingException, IOException {

        if (t.getStartDate() == null || t.getStopDate() == null) {
            return;
        }
        study.addTimeline(c, t);
    }

    private static <T> void processTimlineClass(CbioPortalStudy study, Collection<T> t, Class<T> c)
            throws JsonGenerationException, JsonMappingException, IOException {
        for (T line : t) {
            processTimlineClass(study, (Timeline)line, c);
        }
    }

}
