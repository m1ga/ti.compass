package ti.compass;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;


@Kroll.module(name = "TiCompass", id = "ti.compass")
public class TiCompassModule extends KrollModule {

    private static final String LCAT = "TiCompassModule";

    public TiCompassModule() {
        super();
    }

    @Kroll.onAppCreate
    public static void onAppCreate(TiApplication app) {
    }
}
