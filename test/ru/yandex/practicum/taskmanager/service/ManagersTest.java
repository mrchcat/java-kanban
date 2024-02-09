package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.utils.HistoryManager;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {
    @Test
    void getDefaultTaskManager() {
        assertInstanceOf(TaskManager.class, Managers.getDefault());
    }

    @Test
    void getDefaultHistory() {
        assertInstanceOf(HistoryManager.class, Managers.getDefaultHistory());
    }
}