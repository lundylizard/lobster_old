package me.lundy.lobster.utils;

import java.util.List;

public class Pagination<T> {

    private final List<T> items;
    private int currentPage;

    public Pagination(List<T> items) {
        this.items = items;
        this.currentPage = 1;
    }

    public List<T> getCurrentPageItems() {
        int startIndex = (currentPage - 1) * QueueUtils.TRACKS_PER_PAGE;
        int endIndex = Math.min(startIndex + QueueUtils.TRACKS_PER_PAGE, items.size());
        return items.subList(startIndex, endIndex);
    }

    public List<T> getAllItems() {
        return items;
    }

    public void firstPage() {
        currentPage = 1;
    }

    public boolean isFirstPage() {
        return currentPage == 1;
    }

    public void lastPage() {
        currentPage = getTotalPages();
    }

    public boolean isLastPage() {
        return currentPage == getTotalPages();
    }

    public void nextPage() {
        if (currentPage < getTotalPages()) currentPage++;
    }

    public void previousPage() {
        if (currentPage > 1) currentPage--;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) items.size() / QueueUtils.TRACKS_PER_PAGE);
    }

}
