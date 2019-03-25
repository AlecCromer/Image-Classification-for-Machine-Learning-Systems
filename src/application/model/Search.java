package model;

import java.util.ArrayList;
import java.util.List;

public class Search {

    List<String> dedupedList;

    List<String> dedupedListSubtract;

    int searchCounter;
    String dedupedSearch = "";
    String dedupedSearchSubtract = "";

    public String getDedupedSearch() {
        return dedupedSearch;
    }

    public void setDedupedSearch(String dedupedSearch) {
        this.dedupedSearch = dedupedSearch;
    }

    public String getDedupedSearchSubtract() {
        return dedupedSearchSubtract;
    }

    public void setDedupedSearchSubtract(String dedupedSearchSubtract) {
        this.dedupedSearchSubtract = dedupedSearchSubtract;
    }

    public List<String> getDedupedListSubtract() {
        return dedupedListSubtract;
    }

    public void setDedupedListSubtract(List<String> dedupedListSubtract) {
        this.dedupedListSubtract = dedupedListSubtract;
    }

    public List<String> getDedupedList() {
        return dedupedList;
    }

    public void setDedupedList(List<String> dedupedList) {
        this.dedupedList = dedupedList;
    }


    public int getSearchCounter() {
        return searchCounter;
    }

    public void setSearchCounter(int searchCounter) {
        this.searchCounter = searchCounter;
    }


    public void removeSubtractList() {
        dedupedListSubtract.removeAll(dedupedListSubtract);
    }
    public void removeAddList() {
        dedupedList.removeAll(dedupedList);
    }
    public void searchStringAdd() {
        searchCounter = 0;
        dedupedSearch = "";
        for (String object : dedupedList) {

            System.out.println(object);
            if (searchCounter == 0) {
                dedupedSearch = "'" + object + "'";
                searchCounter++;
            } else {
                dedupedSearch = dedupedSearch + ",'" + object + "'";
                searchCounter++;
            }

        }
        System.out.println(dedupedSearch);
    }

    public void searchStringSubtract() {
        searchCounter = 0;
        dedupedSearchSubtract ="";
        for (String object : dedupedListSubtract) {
            object = object.substring(1);
            System.out.println(object);
            if (searchCounter == 0) {
                dedupedSearchSubtract = "'" + object + "'";
                searchCounter++;
            } else {
                dedupedSearchSubtract = dedupedSearchSubtract + ",'" + object + "'";
                searchCounter++;
            }
        }
        System.out.println(dedupedSearchSubtract);

    }
}
