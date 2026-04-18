package com.braillevision.v2.ui.camera;

import android.content.Context;
import com.braillevision.v2.data.preferences.PreferencesManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class CameraViewModel_Factory implements Factory<CameraViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public CameraViewModel_Factory(Provider<Context> contextProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.contextProvider = contextProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public CameraViewModel get() {
    return newInstance(contextProvider.get(), preferencesManagerProvider.get());
  }

  public static CameraViewModel_Factory create(Provider<Context> contextProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new CameraViewModel_Factory(contextProvider, preferencesManagerProvider);
  }

  public static CameraViewModel newInstance(Context context,
      PreferencesManager preferencesManager) {
    return new CameraViewModel(context, preferencesManager);
  }
}
