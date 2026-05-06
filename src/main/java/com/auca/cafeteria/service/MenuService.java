package com.auca.cafeteria.service;

import com.auca.cafeteria.model.MenuItem;
import com.auca.cafeteria.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getAvailableMenuItems() {
        return menuItemRepository.findByAvailableTrue();
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public MenuItem saveMenuItem(MenuItem item) {
        return menuItemRepository.save(item);
    }

    public MenuItem toggleAvailability(Long itemId) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Menu item not found: " + itemId));
        item.setAvailable(!item.getAvailable());
        return menuItemRepository.save(item);
    }

    public void deleteMenuItem(Long itemId) {
        menuItemRepository.deleteById(itemId);
    }

    public MenuItem getMenuItemById(Long itemId) {
        return menuItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Menu item not found: " + itemId));
    }
}