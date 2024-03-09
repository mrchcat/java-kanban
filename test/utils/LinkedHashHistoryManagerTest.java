package utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.tasks.Selftask;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.LinkedHashHistoryManager;

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

    @DisplayName("add some tasks one after another")
    @Tag("add")
    @Test
    void simpleAdd() {
        for (int i = tasks.size() - 1; i >= 0; i--) {
            history.add(tasks.get(i));
        }
        assertArrayEquals(tasks.toArray(), history.getHistory().toArray());
    }

    @DisplayName("add some tasks one after another and add the first one again")
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

    @DisplayName("add some tasks one after another and add the last one again")
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


    @DisplayName("add some tasks one after another and add one in the middle again")
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

    @DisplayName("add 1_000_000 tasks")
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


    @DisplayName("clear list of tasks")
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

    @DisplayName("remove task in the beginning")
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

    @DisplayName("remove task at the end")
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

    @DisplayName("remove task in the middle")
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

    @DisplayName("remove all tasks one after another")
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

