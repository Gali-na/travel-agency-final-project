package com.epam.travel_agency_final_project.model;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cart {
    // Мапа: Ключ - ID туру, Значення - кількість
    private final Map<UUID, Integer> items;
    private static final Gson gson = new Gson();

    public Cart() {
        this.items = new HashMap<>();
    }

    public Cart(Map<UUID, Integer> items) {
        this.items = items != null ? items : new HashMap<>();
    }

    // Логіка додавання туру (те, про що ти писав)
    public void addTour(UUID tourId) {
        items.put(tourId, items.getOrDefault(tourId, 0) + 1);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                '}';
    }

    public Map<UUID, Integer> getItems() {
        return items;
    }

    // Перетворюємо об'єкт Cart в JSON-рядок для куки
    public String toJson() {
        return gson.toJson(this.items);
    }
    public void removeTour(UUID tourId) {
        if (this.items != null) {
            this.items.remove(tourId);
        }
    }
    // Статичний метод для збирання об'єкта Cart із JSON-рядка куки
    public static Cart fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new Cart();
        }
        try {
            // Оскільки в нас Generic-тип (Map<UUID, Integer>), Gson потребує TypeOf
            Type type = new TypeToken<Map<UUID, Integer>>() {
            }.getType();
            Map<UUID, Integer> deserializedItems = gson.fromJson(json, type);
            return new Cart(deserializedItems);
        } catch (Exception e) {
            // Якщо кука пошкоджена, повертаємо порожній кошик, щоб не "ламати" додаток
            return new Cart();
        }
    }
}
