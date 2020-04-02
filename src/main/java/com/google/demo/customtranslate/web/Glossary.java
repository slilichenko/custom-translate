package com.google.demo.customtranslate.web;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.demo.customtranslate.GlossaryService;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class Glossary {

  private final GlossaryService glossaryService;
  private final String glossaryLocation;
  private final String projectId;

  public Glossary(
      GlossaryService glossaryService, @Value("${glossary.uri}") String glossaryLocation,
      @Value("${gcp.project.id}") String projectId) {
    this.glossaryService = glossaryService;
    this.glossaryLocation = glossaryLocation;
    this.projectId = projectId;
  }

  @RequestMapping(value = "/glossary", method = RequestMethod.GET)
  public String displayGlossary(Model model) throws IOException {
    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    String[] storageChunks = glossaryLocation.split("/", 4);
    Blob blob = storage.get(BlobId.of(storageChunks[2], storageChunks[3]));

    List<String> headers = new ArrayList<>();
    List<List<String>> data = new ArrayList<>();
    try (CSVParser csvParser = new CSVParser(
        new InputStreamReader(Channels.newInputStream(blob.reader())), CSVFormat.DEFAULT)) {
      Iterator<CSVRecord> iterator = csvParser.iterator();
      iterator.next().forEach(h -> headers.add(h));
      while (iterator.hasNext()) {
        CSVRecord next = iterator.next();
        List<String> row = new ArrayList<>();
        data.add(row);
        next.forEach(cell -> row.add(cell));
      }
    }

    model.addAttribute("glossaryHeaders", headers);
    model.addAttribute("glossaryData", data);
    model.addAttribute("glossaryLocation", glossaryLocation);
    return "glossary";
  }


  @RequestMapping(value = "/glossary", method = RequestMethod.POST)
  public String reload(Model model)
      throws InterruptedException, ExecutionException, IOException {
    glossaryService.deleteGlossary();
    glossaryService.createGlossary();

    model.addAttribute("message", "Glossary has been reloaded");
    return displayGlossary(model);
  }
}
