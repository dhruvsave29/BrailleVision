package com.braillevision.v2.ui.history;

import com.braillevision.v2.data.local.HistoryDao;
import com.braillevision.v2.domain.usecase.SpeakTextUseCase;
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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<HistoryDao> historyDaoProvider;

  private final Provider<SpeakTextUseCase> speakTextUseCaseProvider;

  public HistoryViewModel_Factory(Provider<HistoryDao> historyDaoProvider,
      Provider<SpeakTextUseCase> speakTextUseCaseProvider) {
    this.historyDaoProvider = historyDaoProvider;
    this.speakTextUseCaseProvider = speakTextUseCaseProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(historyDaoProvider.get(), speakTextUseCaseProvider.get());
  }

  public static HistoryViewModel_Factory create(Provider<HistoryDao> historyDaoProvider,
      Provider<SpeakTextUseCase> speakTextUseCaseProvider) {
    return new HistoryViewModel_Factory(historyDaoProvider, speakTextUseCaseProvider);
  }

  public static HistoryViewModel newInstance(HistoryDao historyDao,
      SpeakTextUseCase speakTextUseCase) {
    return new HistoryViewModel(historyDao, speakTextUseCase);
  }
}
