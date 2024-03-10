package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.utils.LinkedHashHistoryManager;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {
    @DisplayName("get TaskManager class")
    @Tag("instance")
    @Test
    void getDefaultTaskManagerTest() {
        assertInstanceOf(TaskManager.class, Managers.getDefault());
    }

    @DisplayName("get LinkedHashHistoryManager class")
    @Tag("instance")
    @Test
    void getDefaultHistoryTest() {
        assertInstanceOf(LinkedHashHistoryManager.class, Managers.getDefaultHistory());
    }
}