package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.utils.HistoryManager;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagersTest {
    Managers managers = new Managers();

    @Test
    void getDefaultTaskManager() {
        assertTrue(managers.getDefault() instanceof TaskManager);
    }

    @Test
    void getDefaultHistory() {
        assertTrue(managers.getDefaultHistory() instanceof HistoryManager<?>);
    }
}