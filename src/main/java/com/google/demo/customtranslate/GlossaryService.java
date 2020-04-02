/*
 * Copyright 2020 Google LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.demo.customtranslate;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.cloud.translate.v3.CreateGlossaryMetadata;
import com.google.cloud.translate.v3.CreateGlossaryRequest;
import com.google.cloud.translate.v3.DeleteGlossaryMetadata;
import com.google.cloud.translate.v3.DeleteGlossaryRequest;
import com.google.cloud.translate.v3.DeleteGlossaryResponse;
import com.google.cloud.translate.v3.GcsSource;
import com.google.cloud.translate.v3.Glossary;
import com.google.cloud.translate.v3.GlossaryInputConfig;
import com.google.cloud.translate.v3.GlossaryName;
import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.common.flogger.FluentLogger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
public class GlossaryService implements ApplicationListener<ContextRefreshedEvent> {

  private static FluentLogger logger = FluentLogger.forEnclosingClass();

  private final String projectId;
  private final String glossaryId;
  private final String glossaryUri;
  private final List<String> languageCodes = new ArrayList<>();

  {
    languageCodes.add("en");
    languageCodes.add("ru");
    languageCodes.add("es");
  }

  public GlossaryService(@Value("${gcp.project.id}") String projectId,
      @Value("${glossary.id}") String glossaryId, @Value("${glossary.uri}") String glossaryUri) {
    this.projectId = projectId;
    this.glossaryId = glossaryId;
    this.glossaryUri = glossaryUri;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    try {
      createGlossary();
    } catch (Exception e) {
      if (e.getCause().getClass() == AlreadyExistsException.class) {
        logger.atInfo().log("Glossary %s already exists", glossaryId);
        return;
      }
      throw new RuntimeException(e);
    }
  }

  public void deleteGlossary()
      throws InterruptedException, ExecutionException, IOException {

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (TranslationServiceClient client = TranslationServiceClient.create()) {
      // Supported Locations: `global`, [glossary location], or [model location]
      // Glossaries must be hosted in `us-central1`
      // Custom Models must use the same location as your model. (us-central1)
      GlossaryName glossaryName = GlossaryName.of(projectId, "us-central1", glossaryId);
      DeleteGlossaryRequest request =
          DeleteGlossaryRequest.newBuilder().setName(glossaryName.toString()).build();

      // Start an asynchronous request
      OperationFuture<DeleteGlossaryResponse, DeleteGlossaryMetadata> future =
          client.deleteGlossaryAsync(request);

      logger.atInfo().log("Waiting for glossary deletion operation to complete...");
      DeleteGlossaryResponse response = future.get();
      logger.atInfo().log("Deleted Glossary: %s\n", response.getName());
    }
  }

  // Create a equivalent term sets glossary
  // https://cloud.google.com/translate/docs/advanced/glossary#format-glossary
  public void createGlossary()
      throws IOException, ExecutionException, InterruptedException {

    try (TranslationServiceClient client = TranslationServiceClient.create()) {
      // Supported Locations: `global`, [glossary location], or [model location]
      // Glossaries must be hosted in `us-central1`
      // Custom Models must use the same location as your model. (us-central1)
      String location = "us-central1";
      LocationName parent = LocationName.of(projectId, location);
      GlossaryName glossaryName = GlossaryName.of(projectId, location, glossaryId);

      // Supported Languages: https://cloud.google.com/translate/docs/languages
      Glossary.LanguageCodesSet languageCodesSet =
          Glossary.LanguageCodesSet.newBuilder().addAllLanguageCodes(languageCodes).build();

      // Configure the source of the file from a GCS bucket
      GcsSource gcsSource = GcsSource.newBuilder().setInputUri(glossaryUri).build();
      GlossaryInputConfig inputConfig =
          GlossaryInputConfig.newBuilder().setGcsSource(gcsSource).build();

      Glossary glossaryService =
          Glossary.newBuilder()
              .setName(glossaryName.toString())
              .setLanguageCodesSet(languageCodesSet)
              .setInputConfig(inputConfig)
              .build();

      CreateGlossaryRequest request =
          CreateGlossaryRequest.newBuilder()
              .setParent(parent.toString())
              .setGlossary(glossaryService)
              .build();

      // Start an asynchronous request
      OperationFuture<Glossary, CreateGlossaryMetadata> future =
          client.createGlossaryAsync(request);

      logger.atInfo().log("Waiting for glossary creation operation to complete...");
      Glossary response = future.get();
      logger.atInfo()
          .log("Created glossary: %s, entries: %d", response.getName(), response.getEntryCount());
    }
  }
}