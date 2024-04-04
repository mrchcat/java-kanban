package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.tasks.Selftask;
import ru.yandex.practicum.taskmanager.tasks.Task;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {
    @DisplayName("get default TaskManager class")
    @Tag("instance")
    @Test
    void getDefaultTaskManagerTest() {
        assertInstanceOf(TaskManager.class, Managers.getDefault());
    }

    @DisplayName("get config TaskManager class")
    @Tag("instance")
    @Test
    void getConfigTaskManagerTest() {
        assertInstanceOf(TaskManager.class, Managers.getFromConfig());
    }

    @DisplayName("get config TaskManager class and add datd")
    @Tag("instance")
    @Test
    void getConfigTaskManagerAndAddDataTest() {
        TaskManager taskManager = Managers.getFromConfig();
        Task task = taskManager.add(new Selftask("ss", "sss", Duration.ofDays(1)));
        assertEquals(1, task.getId());
    }


}