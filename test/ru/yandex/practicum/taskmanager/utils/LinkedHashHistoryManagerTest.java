package ru.yandex.practicum.taskmanager.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tasks.Selftask;
import tasks.Task;
import utils.LinkedHashHistoryManager;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class LinkedHashHistoryManagerTest {
    LinkedHashHistoryManager history = new LinkedHashHistoryManager();
    LinkedList<Task> tasks;

    @BeforeEach
    void initTasks() {
        tasks = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            Task task = new Selftask("name " + i, "descr " + i);
            task.setId(i);
            tasks.add(task);
        }
    }

    @DisplayName("добавляем несколько задач подряд")
    @Tag("add")
    @Test
    void simpleAdd() {
        for (int i = tasks.size() - 1; i >= 0; i--) {
            history.add(tasks.get(i));
        }
        assertArrayEquals(tasks.toArray(), history.getHistory().toArray());
    }

    @DisplayName("добавляем несколько задач подряд и повторно вызываем первую из них")
    @Tag("add")
    @Test
    void AddFirstTest() {
        for (int i = tasks.size() - 1; i >= 0; i--) {
            history.add(tasks.get(i));
        }
        int id = tasks.size() - 1;
        Task task = tasks.get(id).copy();
        task.setName("NewName1");
        tasks.remove(id);
        tasks.addFirst(task);
        history.add(task);
        assertArrayEquals(tasks.toArray(), history.getHistory().toArray());
    }

    @DisplayName("добавляем несколько задач подряд и повторно вызываем последнюю из них")
    @Tag("add")
    @Test
    void AddLastTest() {
        for (int i = tasks.size() - 1; i >= 0; i--) {
            history.add(tasks.get(i));
        }
        int id = 0;
        Task task = tasks.get(id).copy();
        task.setName("NewName1");
        history.add(task);
        assertArrayEquals(tasks.toArray(), history.getHistory().toArray());
    }


    @DisplayName("добавляем несколько задач подряд и повторно вызываем задачу в середине")
    @Tag("add")
    @Test
    void AddCenterTest() {
        int middle = tasks.size() / 2;
        for (int i = tasks.size() - 1; i >= 0; i--) {
            history.add(tasks.get(i));
        }
        int id = middle;
        Task task = tasks.get(id).copy();
        task.setName("NewName1");
        tasks.remove(id);
        tasks.addFirst(task);
        history.add(task);
        assertArrayEquals(tasks.toArray(), history.getHistory().toArray());
    }

    @DisplayName("добавляем много элементов")
    @Tag("add")
    @Test
    void addMany() {
        int N = 1_000_000;
        Task task = null;
        for (int i = 0; i < N; i++) {
            task = new Selftask("name " + i, "descr " + i);
            task.setId(i);
            history.add(task);
        }
        assertEquals(task, history.getHistory().getFirst());
    }


    @DisplayName("очищаем список")
    @Tag("clear")
    @Test
    void CLearTest() {
        for (int i = tasks.size() - 1; i >= 0; i--) {
            history.add(tasks.get(i));
        }
        assertFalse(history.getHistory().isEmpty());
        history.clear();
        assertTrue(history.getHistory().isEmpty());
    }

    @DisplayName("удаляем в начале")
    @Tag("delete")
    @Test
    void deleteFirst() {
        for (int i = tasks.size() - 1; i >= 0; i--) {
            history.add(tasks.get(i));
        }
        int id = 0;
        tasks.remove(id);
        history.remove(id);
        assertArrayEquals(tasks.toArray(), history.getHistory().toArray());
    }

    @DisplayName("удаляем в конце")
    @Tag("delete")
    @Test
    void deleteLast() {
        for (int i = tasks.size() - 1; i >= 0; i--) {
            history.add(tasks.get(i));
        }
        int id = tasks.size() - 1;
        tasks.remove(id);
        history.remove(id);
        assertArrayEquals(tasks.toArray(), history.getHistory().toArray());
    }

    @DisplayName("удаляем в середине")
    @Tag("delete")
    @Test
    void deleteCenter() {
        int middle = tasks.size() / 2;
        for (int i = tasks.size() - 1; i >= 0; i--) {
            history.add(tasks.get(i));
        }
        int id = middle;
        tasks.remove(id);
        history.remove(id);
        assertArrayEquals(tasks.toArray(), history.getHistory().toArray());
    }

    @DisplayName("удаляем все элементы по очереди")
    @Tag("delete")
    @Test
    void deleteAll() {
        for (int i = tasks.size() - 1; i >= 0; i--) {
            history.add(tasks.get(i));
        }
        List<Task> listOfTasks = history.getHistory();
        for (Task task : listOfTasks) {
            history.remove(task.getId());
        }
        assertEquals(0, history.getHistory().size());
    }
}

