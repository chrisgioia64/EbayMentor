package tests.view_item;

import api.CustomUtilities;
import pages.ViewItemPage;
import pages.WatchlistPage;

import java.sql.Driver;

/**
 * This defines the sub-operations to be performed on an Add Watchlist/Remove Watchlist test
 */
public interface WatchlistTestTemplate {

    public void addToWatchlist(ViewItemPage viPage,
                               WatchlistPage watchlistPage, String itemNumber);

    public void removeFromWatchlist(ViewItemPage viPage,
                                    WatchlistPage watchlistPage, String itemNumber);

    /**
     * Check correct behavior when adding/removing the item from the
     * watchlist via clicking the Watchlist button.
     */
    public static class ClickButtonTemplate implements WatchlistTestTemplate {

        @Override
        public void addToWatchlist(ViewItemPage viPage, WatchlistPage watchlistPage, String itemNumber) {
            viPage.clickWatchButton();
            CustomUtilities.sleep(2000);
        }

        @Override
        public void removeFromWatchlist(ViewItemPage viPage, WatchlistPage watchlistPage, String itemNumber) {
            viPage.clickWatchButton();
            CustomUtilities.sleep(2000);
        }
    }

    public static class WatchlistLinkTemplate implements WatchlistTestTemplate {

        @Override
        public void addToWatchlist(ViewItemPage viPage, WatchlistPage watchlistPage, String itemNumber) {
            viPage.getWatchlistLink().click();
        }

        @Override
        public void removeFromWatchlist(ViewItemPage viPage, WatchlistPage watchlistPage, String itemNumber) {
            viPage.clickWatchButton();
            CustomUtilities.sleep(2000);
        }
    }

    public static class WatchlistPageTemplate implements WatchlistTestTemplate {

        @Override
        public void addToWatchlist(ViewItemPage viPage, WatchlistPage watchlistPage, String itemNumber) {
            viPage.clickWatchButton();
            CustomUtilities.sleep(2000);
        }

        @Override
        public void removeFromWatchlist(ViewItemPage viPage, WatchlistPage watchlistPage, String itemNumber) {
            watchlistPage.navigateToPage();
            watchlistPage.deleteProduct(itemNumber);
        }
    }


}
