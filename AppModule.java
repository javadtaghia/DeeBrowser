package co.zew.browser.app;

import android.content.Context;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import co.zew.browser.database.BookmarkManager;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Stefano Pacifici on 01/09/15.
 */
@Module
public class AppModule {
    private final BrowserApp app;
    private final Bus bus;

    public AppModule(BrowserApp app) {
        this.app = app;
        this.bus = new Bus();
    }

    @Provides
    public Context provideContext() {
        return app.getApplicationContext();
    }

    @Provides
    @Singleton
    public BookmarkManager provideBookmarkManager() {
        return new BookmarkManager(app.getApplicationContext());
    }

    @Provides
    public Bus provideBus() {
        return bus;
    }
}
