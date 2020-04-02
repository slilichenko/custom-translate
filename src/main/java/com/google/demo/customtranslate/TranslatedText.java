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

public class TranslatedText {

  private final String source;
  private final String base;
  private final String glossaryBased;
  private final String sourceLanguageCode;
  private final String targetLanguageCode;
  private final String mimeType;
  private final String modelId;

  public static TranslatedText empty() {
    return new TranslatedText("", "", "", "", "", "", "");
  }

  public TranslatedText(String source, String base,
      String glossaryBased, String sourceLanguageCode, String targetLanguageCode,
      String mimeType, String modelId) {
    this.source = source;
    this.base = base;
    this.glossaryBased = glossaryBased;
    this.sourceLanguageCode = sourceLanguageCode;
    this.targetLanguageCode = targetLanguageCode;
    this.mimeType = mimeType;
    this.modelId = modelId;
  }

  public String getSource() {
    return source;
  }

  public String getBase() {
    return base;
  }

  public String getGlossaryBased() {
    return glossaryBased;
  }

  public String getSourceLanguageCode() {
    return sourceLanguageCode;
  }

  public String getTargetLanguageCode() {
    return targetLanguageCode;
  }

  public String getMimeType() {
    return mimeType;
  }

  public String getModelId() {
    return modelId;
  }
}
