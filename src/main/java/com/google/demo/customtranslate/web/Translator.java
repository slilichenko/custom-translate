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

package com.google.demo.customtranslate.web;

import com.google.demo.customtranslate.TranslatedText;
import com.google.demo.customtranslate.TranslatorService;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Translator {

  public static class TranslationModel {
    private final String id;
    private final String name;

    public TranslationModel(String id, String name) {
      this.id = id;
      this.name = name;
    }

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }
  }

  private static List<TranslationModel> DEFAULT_TRANSLATION_MODELS = Collections.unmodifiableList(
      Arrays.asList(new TranslationModel[] {
          new TranslationModel("general/nmt", "Neural Machine Translation"),
          new TranslationModel("general/base", "Phrase-Based Machine Translation"),
      })
  );

  public static class Language {

    private final String code;
    private final String name;

    public Language(String code, String name) {
      this.code = code;
      this.name = name;
    }

    public String getCode() {
      return code;
    }

    public String getName() {
      return name;
    }
  }

  private static List<Language> LANGUAGES = Collections.unmodifiableList(Arrays.asList(
      new Language[]{
          new Language("ru", "Russian"),
          new Language("es", "Spanish"),
      }
  ));

  public static class MimeType {

    private final String type;
    private final String name;

    public MimeType(String type, String name) {
      this.type = type;
      this.name = name;
    }

    public String getType() {
      return type;
    }

    public String getName() {
      return name;
    }
  }

  private static List<MimeType> MIME_TYPES = Collections.unmodifiableList(Arrays.asList(
      new MimeType[]{
          new MimeType("text/plain", "text"),
          new MimeType("text/html", "HTML"),
      }));

  private TranslatorService translatorService;

  public Translator(TranslatorService translatorService) {
    this.translatorService = translatorService;
  }

  @ModelAttribute(name = "allLanguages")
  public List<Language> allLanguages() {
    return LANGUAGES;
  }

  @ModelAttribute(name = "allMimeTypes")
  public List<MimeType> allMimeTypes() {
    return MIME_TYPES;
  }

  @ModelAttribute(name = "allTranslationModels")
  public List<TranslationModel> allTranslationModels() {
    return DEFAULT_TRANSLATION_MODELS;
  }

  @RequestMapping(value = "/translate", method = RequestMethod.GET)
  public String translate(Model model) {
    model.addAttribute("translation", TranslatedText.empty());
    return "translate";
  }

  @RequestMapping(value = "/translate", method = RequestMethod.POST)
  public String translate(
      @RequestParam(name = "source") String source,
      @RequestParam(name = "targetLanguageCode") String targetLanguageCode,
      @RequestParam(name = "modelId") String modelId,
      @RequestParam(name = "mimeType") String mimeType,
      Model model
  ) {
    TranslatedText translation = TranslatedText.empty();
    if (source.length() > 0) {
      try {
        translation = translatorService
            .translate(source, "en", targetLanguageCode, modelId, mimeType);
      } catch (IOException e) {
        translation = new TranslatedText(source, "Error occurred", "Error occurred",
            "", "", mimeType, modelId);
      }
    }
    model.addAttribute("translation", translation);
    return "translate";
  }
}
