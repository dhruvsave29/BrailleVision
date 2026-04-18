package com.braillevision.v2.domain.usecase;

import com.braillevision.v2.data.local.HistoryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SaveHistoryUseCase_Factory implements Factory<SaveHistoryUseCase> {
  private final Provider<HistoryDao> historyDaoProvider;

  public SaveHistoryUseCase_Factory(Provider<HistoryDao> historyDaoProvider) {
    this.historyDaoProvider = historyDaoProvider;
  }

  @Override
  public SaveHistoryUseCase get() {
    return newInstance(historyDaoProvider.get());
  }

  public static SaveHistoryUseCase_Factory create(Provider<HistoryDao> historyDaoProvider) {
    return new SaveHistoryUseCase_Factory(historyDaoProvider);
  }

  public static SaveHistoryUseCase newInstance(HistoryDao historyDao) {
    return new SaveHistoryUseCase(historyDao);
  }
}
