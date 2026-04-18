package com.braillevision.v2.data.tflite;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class YoloDetector_Factory implements Factory<YoloDetector> {
  private final Provider<Context> contextProvider;

  public YoloDetector_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public YoloDetector get() {
    return newInstance(contextProvider.get());
  }

  public static YoloDetector_Factory create(Provider<Context> contextProvider) {
    return new YoloDetector_Factory(contextProvider);
  }

  public static YoloDetector newInstance(Context context) {
    return new YoloDetector(context);
  }
}
