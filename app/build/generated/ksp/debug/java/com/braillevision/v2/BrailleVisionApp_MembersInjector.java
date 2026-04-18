package com.braillevision.v2;

import com.braillevision.v2.data.tflite.YoloDetector;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
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
public final class BrailleVisionApp_MembersInjector implements MembersInjector<BrailleVisionApp> {
  private final Provider<YoloDetector> yoloDetectorProvider;

  public BrailleVisionApp_MembersInjector(Provider<YoloDetector> yoloDetectorProvider) {
    this.yoloDetectorProvider = yoloDetectorProvider;
  }

  public static MembersInjector<BrailleVisionApp> create(
      Provider<YoloDetector> yoloDetectorProvider) {
    return new BrailleVisionApp_MembersInjector(yoloDetectorProvider);
  }

  @Override
  public void injectMembers(BrailleVisionApp instance) {
    injectYoloDetector(instance, yoloDetectorProvider.get());
  }

  @InjectedFieldSignature("com.braillevision.v2.BrailleVisionApp.yoloDetector")
  public static void injectYoloDetector(BrailleVisionApp instance, YoloDetector yoloDetector) {
    instance.yoloDetector = yoloDetector;
  }
}
