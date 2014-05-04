package com.jmstudios.corvallistransit.utils;

import android.widget.AbsListView;

/**
 * Class which facilitates downloading more arrivals for a route
 * when the user scrolls far enough.
 */
public abstract class ArrivalsListener implements AbsListView.OnScrollListener {
    private int visibleThreshold = 10;

    private int currentIndex = 0;

    private int previousTotal = 0;

    private boolean loading = true;

    private int startIndex = 0;

    public ArrivalsListener() {
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        // explcitly do nothing here
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotal) {
            this.currentIndex = this.startIndex;
            this.previousTotal = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (this.loading && (totalItemCount > previousTotal)) {
            this.loading = false;
            this.previousTotal = totalItemCount;
            this.currentIndex++;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!this.loading &&
                (totalItemCount - visibleItemCount) <=
                        (firstVisibleItem + this.visibleThreshold)) {
            this.loading = true;
            onLoadMore(currentIndex + 1, totalItemCount);
        }
    }

    public abstract void onLoadMore(int index, int total);
}
