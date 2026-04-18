package com.braillevision.v2.domain.usecase;

import android.content.Context;
import com.braillevision.v2.data.preferences.PreferencesManager;
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
public final class SpeakTextUseCase_Factory implements Factory<SpeakTextUseCase> {
  private final Provider<Context> contextProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public SpeakTextUseCase_Factory(Provider<Context> contextProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.contextProvider = contextProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public SpeakTextUseCase get() {
    return newInstance(contextProvider.get(), preferencesManagerProvider.get());
  }

  public static SpeakTextUseCase_Factory create(Provider<Context> contextProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new SpeakTextUseCase_Factory(contextProvider, preferencesManagerProvider);
  }

  public static SpeakTextUseCase newInstance(Context context,
      PreferencesManager preferencesManager) {
    return new SpeakTextUseCase(context, preferencesManager);
  }
}
