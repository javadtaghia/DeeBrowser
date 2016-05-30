package co.zew.browser.bus;

import co.zew.browser.fragment.BookmarksFragment;

/**
 * Created by Stefano Pacifici on 26/08/15.
 */
public final class BrowserEvents {

    private BrowserEvents() {
        // No instances
    }

    /**
     * Used to reply to the {@link BookmarksFragment} message
     * {@link BookmarkEvents.WantToBookmarkCurrentPage}. The interaction
     * result is a new bookmark added.
     */
    public static class AddBookmark {
        public final String title, url, folder; // javad bookmark add issue

        public AddBookmark(final String title, final String url, final String folder) { // javad bookmark add issue
            this.title = title;
            this.url = url;
            this.folder = folder; // javad bookmark add issue
        }
    }

    /**
     * Used to reply to {@link BookmarksFragment} message
     * {@link BookmarkEvents.WantInfoAboutCurrentPage}. This is generally
     * used to update the {@link BookmarksFragment} interface.
     */
    public static class CurrentPageUrl {
        public final String url;

        public CurrentPageUrl(final String url) {
            this.url = url;
        }
    }

    /**
     * Notify the BookmarksFragment and TabsFragment that the user pressed the back button
     */
    public static class UserPressedBack {
    }
}
