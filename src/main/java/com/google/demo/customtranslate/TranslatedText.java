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
