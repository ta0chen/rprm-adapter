package com.polycom.webservices.cdr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.cdr.bean.ConferenceTypeCdr;
import com.polycom.sqa.xma.webservices.cdr.bean.ConferenceUsageCdr;
import com.polycom.sqa.xma.webservices.cdr.bean.EndpointUsageCdr;
import com.polycom.sqa.xma.webservices.cdr.bean.Participant;

public class CsvParser {
    public static List<ConferenceTypeCdr>
    getConferenceTypeCdr(final File csvFile) throws IOException {
        final InputStreamReader freader = new InputStreamReader(
                                                                new FileInputStream(csvFile));
        final ICsvBeanReader reader = new CsvBeanReader(freader,
                                                        CsvPreference.EXCEL_PREFERENCE);
        final String[] headers = reader.getHeader(true);
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].replaceAll("[\\s|(|)|\\.]|:|-", "")
                    .replaceAll("Short", "ShortConference");
        }
        final CellProcessor[] readProcessors = new CellProcessor[headers.length];
        for (int i = 0; i < readProcessors.length; i++) {
            readProcessors[i] = new StrMinMax(0, 100);
        }
        final List<ConferenceTypeCdr> conferenceTypeCdrList = new ArrayList<ConferenceTypeCdr>();
        ConferenceTypeCdr conferenceTypeCdr = new ConferenceTypeCdr();
        while ((conferenceTypeCdr = reader.read(ConferenceTypeCdr.class,
                                                headers,
                                                readProcessors)) != null) {
            conferenceTypeCdrList.add(conferenceTypeCdr);
        }
        for (final ConferenceTypeCdr cdr : conferenceTypeCdrList) {
            System.out.println(cdr);
        }
        reader.close();
        return conferenceTypeCdrList;
    }

    public static List<ConferenceUsageCdr>
    getConferenceUsageCdr(final File csvFile) throws IOException {
        final InputStreamReader freader = new InputStreamReader(
                                                                new FileInputStream(csvFile));
        final ICsvBeanReader reader = new CsvBeanReader(freader,
                                                        CsvPreference.EXCEL_PREFERENCE);
        final String[] headers = reader.getHeader(true);
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].replaceAll("[\\s|(|)|\\.]|:|-", "");
        }
        final CellProcessor[] readProcessors = new CellProcessor[headers.length];
        for (int i = 0; i < readProcessors.length; i++) {
            readProcessors[i] = new Optional();
        }
        final List<ConferenceUsageCdr> conferenceUsageCdrList = new ArrayList<ConferenceUsageCdr>();
        ConferenceUsageCdr conferenceUsageCdr = new ConferenceUsageCdr();
        while ((conferenceUsageCdr = reader.read(ConferenceUsageCdr.class,
                                                 headers,
                                                 readProcessors)) != null) {
            conferenceUsageCdrList.add(conferenceUsageCdr);
        }
        ConferenceUsageCdr currentConference = null;
        for (int i = 0; i < conferenceUsageCdrList.size(); i++) {
            final ConferenceUsageCdr cdr = conferenceUsageCdrList.get(i);
            if (cdr.getConferenceScheduler() != null) {
                currentConference = cdr;
            } else {
                final Participant p = new Participant();
                try {
                    CommonUtils.copyProperties(cdr, p);
                    currentConference.getParticipants().add(p);
                    conferenceUsageCdrList.remove(i);
                    i--;
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
        reader.close();
        return conferenceUsageCdrList;
    }

    public static List<EndpointUsageCdr>
    getEndpointUsageCdr(final File csvFile) throws IOException {
        final InputStreamReader freader = new InputStreamReader(
                                                                new FileInputStream(csvFile));
        final ICsvBeanReader reader = new CsvBeanReader(freader,
                                                        CsvPreference.EXCEL_PREFERENCE);
        final String[] headers = reader.getHeader(true);
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].replaceAll("[\\s|(|)|\\.]|:", "");
        }
        final CellProcessor[] readProcessors = new CellProcessor[headers.length];
        for (int i = 0; i < readProcessors.length; i++) {
            readProcessors[i] = new ConvertNullTo("\"\"");
        }
        final List<EndpointUsageCdr> endpointUsageCdrList = new ArrayList<EndpointUsageCdr>();
        EndpointUsageCdr endpointUsageCdr = new EndpointUsageCdr();
        while ((endpointUsageCdr = reader.read(EndpointUsageCdr.class,
                                               headers,
                                               readProcessors)) != null) {
            endpointUsageCdrList.add(endpointUsageCdr);
        }
        reader.close();
        return endpointUsageCdrList;
    }

    public static void main(final String[] args) throws IOException {
        // getEndpointUsageCdr(new
        // File("cdr/ENDPOINT_CDR_DETAIL_ALL_CSV-1.csv"));
        // getConferenceUsageCdr(new File("cdr/CONFLIST_DETAIL_ALL_CSV-5.csv"));
    	getEndpointUsageCdr(new File("cdr/ENDPOINT_CDR_DETAIL_ALL_CSV-5.csv"));
    }
}
