package com.lokico.PSWind;


import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dGFJUG4zdjBIbkwzVnUwZWpnd011ZFE6MQ") 
public class CrashReporterApplication extends Application {
    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();
    }
}