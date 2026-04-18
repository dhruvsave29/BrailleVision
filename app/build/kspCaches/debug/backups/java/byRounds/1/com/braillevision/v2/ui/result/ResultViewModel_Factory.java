package com.braillevision.v2.ui.result;

import android.content.Context;
import com.braillevision.v2.data.local.HistoryDao;
import com.braillevision.v2.data.preferences.PreferencesManager;
import com.braillevision.v2.data.spell.SymSpell;
import com.braillevision.v2.data.tflite.YoloDetector;
import com.braillevision.v2.domain.usecase.SpeakTextUseCase;
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
public final class ResultViewModel_Factory implements Factory<ResultViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<YoloDetector> yoloDetectorProvider;

  private final Provider<HistoryDao> historyDaoProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<SpeakTextUseCase> speakTextUseCaseProvider;

  private final Provider<SymSpell> symSpellProvider;

  public ResultViewModel_Factory(Provider<Context> contextProvider,
      Provider<YoloDetector> yoloDetectorProvider, Provider<HistoryDao> historyDaoProvider,
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<SpeakTextUseCase> speakTextUseCaseProvider, Provider<SymSpell> symSpellProvider) {
    this.contextProvider = contextProvider;
    this.yoloDetectorProvider = yoloDetectorProvider;
    this.historyDaoProvider = historyDaoProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.speakTextUseCaseProvider = speakTextUseCaseProvider;
    this.symSpellProvider = symSpellProvider;
  }

  @Override
  public ResultViewModel get() {
    return newInstance(contextProvider.get(), yoloDetectorProvider.get(), historyDaoProvider.get(), preferencesManagerProvider.get(), speakTextUseCaseProvider.get(), symSpellProvider.get());
  }

  public static ResultViewModel_Factory create(Provider<Context> contextProvider,
      Provider<YoloDetector> yoloDetectorProvider, Provider<HistoryDao> historyDaoProvider,
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<SpeakTextUseCase> speakTextUseCaseProvider, Provider<SymSpell> symSpellProvider) {
    return new ResultViewModel_Factory(contextProvider, yoloDetectorProvider, historyDaoProvider, preferencesManagerProvider, speakTextUseCaseProvider, symSpellProvider);
  }

  public static ResultViewModel newInstance(Context context, YoloDetector yoloDetector,
      HistoryDao historyDao, PreferencesManager preferencesManager,
      SpeakTextUseCase speakTextUseCase, SymSpell symSpell) {
    return new ResultViewModel(context, yoloDetector, historyDao, preferencesManager, speakTextUseCase, symSpell);
  }
}
