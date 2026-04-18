package com.braillevision.v2.ui.settings;

import com.braillevision.v2.data.preferences.PreferencesManager;
import com.braillevision.v2.data.tflite.YoloDetector;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<YoloDetector> yoloDetectorProvider;

  public SettingsViewModel_Factory(Provider<PreferencesManager> preferencesManagerProvider,
      Provider<YoloDetector> yoloDetectorProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.yoloDetectorProvider = yoloDetectorProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(preferencesManagerProvider.get(), yoloDetectorProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<YoloDetector> yoloDetectorProvider) {
    return new SettingsViewModel_Factory(preferencesManagerProvider, yoloDetectorProvider);
  }

  public static SettingsViewModel newInstance(PreferencesManager preferencesManager,
      YoloDetector yoloDetector) {
    return new SettingsViewModel(preferencesManager, yoloDetector);
  }
}
