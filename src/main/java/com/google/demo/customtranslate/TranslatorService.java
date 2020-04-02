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

import com.google.cloud.translate.v3.GlossaryName;
import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextGlossaryConfig;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TranslatorService {

  private final String projectId;
  private final String glossaryId;

  public TranslatorService(@Value("${gcp.project.id}") String projectId,
      @Value("${glossary.id}") String glossaryId) {
    this.projectId = projectId;
    this.glossaryId = glossaryId;
  }

  public TranslatedText translate(String text, String sourceLanguageCode, String targetLanguageCode,
      String modelId, String mimeType)
      throws IOException {
    return translateTextWithGlossary(projectId, sourceLanguageCode, targetLanguageCode, text,
        glossaryId,
        modelId,
        mimeType);
  }

  public static TranslatedText translateTextWithGlossary(
      String projectId,
      String sourceLanguageCode,
      String targetLanguageCode,
      String text,
      String glossaryId,
      String modelId, String mimeType)
      throws IOException {

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (TranslationServiceClient client = TranslationServiceClient.create()) {
      // Supported Locations: `global`, [glossary location], or [model location]
      // Glossaries must be hosted in `us-central1`
      // Custom Models must use the same location as your model. (us-central1)
      String location = "us-central1";
      LocationName parent = LocationName.of(projectId, location);

      GlossaryName glossaryName = GlossaryName.of(projectId, location, glossaryId);
      TranslateTextGlossaryConfig glossaryConfig =
          TranslateTextGlossaryConfig.newBuilder().setGlossary(glossaryName.toString()).build();

      String modelPath =
          String.format("projects/%s/locations/%s/models/%s", projectId, location, modelId);

      List<String> contents = mimeType.equals("text/plain") ?
          Arrays.asList(text.split("\\n"))
          : Collections.singletonList(text);

      // Supported Mime Types: https://cloud.google.com/translate/docs/supported-formats

      TranslateTextRequest request =
          TranslateTextRequest.newBuilder()
              .setParent(parent.toString())
              .setMimeType(mimeType)
              .setSourceLanguageCode(sourceLanguageCode)
              .setTargetLanguageCode(targetLanguageCode)
              .addAllContents(contents)
              .setGlossaryConfig(glossaryConfig)
              .setModel(modelPath)
              .build();

      TranslateTextResponse response = client.translateText(request);

      return new TranslatedText(text,
          extractTranslation(response.getTranslationsList()),
          extractTranslation(response.getGlossaryTranslationsList()),
          sourceLanguageCode,
          targetLanguageCode,
          mimeType, modelId);
    }
  }

  private static String extractTranslation(List<Translation> translations) {
    StringBuilder result = new StringBuilder();
    for (Translation translation : translations) {
      if (result.length() > 0) {
        result.append("\n");
      }
      result.append(translation.getTranslatedText());
    }
    return result.toString();
  }
}
