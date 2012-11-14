package com.gerritforge.its;

import com.gerritforge.init.Section;
import com.google.gerrit.pgm.init.InitStep;
import com.google.gerrit.pgm.util.ConsoleUI;
import com.google.gerrit.server.config.FactoryModule;
import com.google.inject.Injector;

public class InitIts implements InitStep {

  private final Injector initInjector;

  public static enum YesNoEnum {
    Y, N;
  }


  public static enum TrueFalseEnum {
    TRUE, FALSE;
  }

  public InitIts(Injector injector) {
    initInjector = injector.createChildInjector(new FactoryModule() {

      @Override
      protected void configure() {
        factory(Section.Factory.class);
      }
    });
  }

  @Override
  public void run() throws Exception {
  }

  public Section.Factory getSectionFactory() {
    return initInjector.getInstance(Section.Factory.class);
  }

  public boolean isConnectivityRequested(ConsoleUI ui, String url) {
    YesNoEnum wantToTest =
        ui.readEnum(YesNoEnum.N, "Test connectivity to %s", url);
    return wantToTest == YesNoEnum.Y;
  }

  public boolean enterSSLVerify(Section section) {
    return TrueFalseEnum.TRUE == section.select("Verify SSL Certificates",
        "sslVerify", TrueFalseEnum.TRUE);
  }
}
