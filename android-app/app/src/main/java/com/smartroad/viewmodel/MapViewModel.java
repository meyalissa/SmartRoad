package com.smartroad.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.smartroad.model.Hazard;
import com.smartroad.repository.HazardRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Hazard Map screen state. Hazards are fetched from the network only when
 * {@link #refresh()} is called; toggling the status/category filters re-applies
 * them purely in memory via {@link #filteredHazards}, so filter changes never
 * trigger a new API request or touch the map/camera.
 */
public class MapViewModel extends ViewModel {

    public static final String FILTER_ALL = "All";

    private final HazardRepository repository = new HazardRepository();

    private final MediatorLiveData<List<Hazard>> allHazards = new MediatorLiveData<>();
    private LiveData<List<Hazard>> currentFetch;

    private String statusFilter = FILTER_ALL;
    private String categoryFilter = FILTER_ALL;
    private final MediatorLiveData<List<Hazard>> filteredHazards = new MediatorLiveData<>();

    public MapViewModel() {
        filteredHazards.addSource(allHazards, list -> applyFilters());
        refresh();
    }

    /** Re-fetches hazard reports from the API. Call on pull-to-refresh / FAB tap only. */
    public void refresh() {
        if (currentFetch != null) allHazards.removeSource(currentFetch);
        currentFetch = repository.getHazards();
        allHazards.addSource(currentFetch, allHazards::setValue);
    }

    /** The list to render on the map: {@link #allHazards} narrowed by the active filters. */
    public LiveData<List<Hazard>> getFilteredHazards() {
        return filteredHazards;
    }

    public void setStatusFilter(String status) {
        statusFilter = status;
        applyFilters();
    }

    public void setCategoryFilter(String category) {
        categoryFilter = category;
        applyFilters();
    }

    private void applyFilters() {
        List<Hazard> source = allHazards.getValue();
        if (source == null) {
            filteredHazards.setValue(null);
            return;
        }
        List<Hazard> result = new ArrayList<>();
        for (Hazard h : source) {
            boolean statusMatches = FILTER_ALL.equals(statusFilter) || statusFilter.equalsIgnoreCase(h.getStatus());
            boolean categoryMatches = FILTER_ALL.equals(categoryFilter) || categoryFilter.equalsIgnoreCase(h.getType());
            if (statusMatches && categoryMatches) result.add(h);
        }
        filteredHazards.setValue(result);
    }
}
