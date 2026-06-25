package com.epam.travel_agency_final_project.model;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.ToString;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@ToString
public class Cart {
    private final Map<UUID, Integer> items;
    private static final Gson gson = new Gson();
    public Cart() {
        this.items = new HashMap<>();
    }
    public Cart(Map<UUID, Integer> items) {
        this.items = items != null ? items : new HashMap<>();
    }
    public void addTour(UUID tourId) {
        items.put(tourId, items.getOrDefault(tourId, 0) + 1);
    }

    public Map<UUID, Integer> getItems() {
        return items;
    }
    public String toJson() {
        return gson.toJson(this.items);
    }
    public void removeTour(UUID tourId) {
        if (this.items != null) {
            this.items.remove(tourId);
        }
    }
    public static Cart fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new Cart();
        }
        try {
            Type type = new TypeToken<Map<UUID, Integer>>() {
            }.getType();
            Map<UUID, Integer> deserializedItems = gson.fromJson(json, type);
            return new Cart(deserializedItems);
        } catch (Exception e) {
            return new Cart();
        }
    }
}
