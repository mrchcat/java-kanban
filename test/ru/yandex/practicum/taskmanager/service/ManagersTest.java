package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import utils.LinkedHashHistoryManager;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {
    @DisplayName("получить класс TaskManager")
    @Tag("instance")
    @Test
    void getDefaultTaskManager() {
        assertInstanceOf(TaskManager.class, Managers.getDefault());
    }

    @DisplayName("получить класс LinkedHashHistoryManager")
    @Tag("instance")
    @Test
    void getDefaultHistory() {
        assertInstanceOf(LinkedHashHistoryManager.class, Managers.getDefaultHistory());
    }
}