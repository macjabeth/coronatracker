package com.macjabeth.coronatracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.macjabeth.coronatracker.models.GlobalStats;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CoronaDataService {
  private static final String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

  private List<GlobalStats> allStats = new ArrayList<>();

  public List<GlobalStats> getAllStats() {
    return allStats;
  }

  @PostConstruct
  @Scheduled(cron = "0 * * ? * *")
  public void fetchCoronaData() throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(DATA_URL)).build();
    HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
    StringReader csvBodyReader = new StringReader(httpResponse.body());
    Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
    allStats.clear();
    for (CSVRecord record : records) {
      GlobalStats stats = new GlobalStats();
      stats.setState(record.get("Province/State"));
      stats.setCountry(record.get("Country/Region"));
      int currentCases = Integer.parseInt(record.get(record.size() - 1));
      int prevCases = Integer.parseInt(record.get(record.size() - 2));
      stats.setLatestTotalCases(currentCases);
      stats.setDiffFromPrevDay(currentCases - prevCases);
      allStats.add(stats);
    }
  }
}
