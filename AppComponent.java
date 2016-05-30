package co.zew.browser.app;

import javax.inject.Singleton;

import co.zew.browser.activity.BrowserActivity;
import co.zew.browser.constant.BookmarkPage;
import co.zew.browser.dialog.BookmarksDialogBuilder;
import co.zew.browser.fragment.BookmarkSettingsFragment;
import co.zew.browser.fragment.BookmarksFragment;
import co.zew.browser.object.SearchAdapter;
import dagger.Component;

/**
 * Created by Stefano Pacifici on 01/09/15.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(BrowserActivity activity);

    void inject(BookmarksFragment fragment);

    void inject(BookmarkSettingsFragment fragment);

    void inject(SearchAdapter adapter);

    void inject(BookmarksDialogBuilder builder);

    void inject(BookmarkPage bookmarkPage);
}
