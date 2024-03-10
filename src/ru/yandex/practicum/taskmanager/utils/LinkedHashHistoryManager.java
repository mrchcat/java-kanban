package ru.yandex.practicum.taskmanager.utils;

import ru.yandex.practicum.taskmanager.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class LinkedHashHistoryManager implements HistoryManager {

    private final Node head;
    private final HashMap<Integer, Node> nodes;

    public LinkedHashHistoryManager() {
        head = new Node(null, null, null);
        nodes = new HashMap<>();
    }

    @Override
    public void add(Task item) {
        if (item == null) {
            return;
        }

        int id = item.getId();
        Node oldFirst = head.next;
        if (!nodes.containsKey(id)) {
            Node cur = new Node(item, head, oldFirst);
            head.next = cur;
            if (oldFirst != null) {
                oldFirst.before = cur;
            }
            nodes.put(id, cur);
            return;
        }

        Node cur = nodes.get(id);
        cur.value = item;
        if (cur == oldFirst) {
            return;
        }

        Node afterCur = cur.next;
        Node beforeCur = cur.before;
        beforeCur.next = afterCur;
        if (afterCur != null) {
            afterCur.before = beforeCur;
        }
        head.next = cur;
        oldFirst.before = cur;
        cur.next = oldFirst;
        cur.before = head;
    }

    @Override
    public void remove(int id) {
        if (!nodes.containsKey(id)) {
            return;
        }
        Node cur = nodes.get(id);
        Node beforeCur = cur.before;
        Node afterCur = cur.next;
        beforeCur.next = afterCur;
        if (afterCur != null) {
            afterCur.before = beforeCur;
        }
        nodes.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        if (head.next == null) {
            return Collections.emptyList();
        }
        ArrayList<Task> list = new ArrayList<>();
        Node pos = head;
        while ((pos = pos.next) != null) {
            list.add(pos.value);
        }
        return list;
    }

    @Override
    public void clear() {
        nodes.clear();
        head.next = null;
    }

    private static class Node {
        Task value;
        Node before;
        Node next;

        public Node(Task value, Node before, Node next) {
            this.value = value;
            this.before = before;
            this.next = next;
        }
    }
}
