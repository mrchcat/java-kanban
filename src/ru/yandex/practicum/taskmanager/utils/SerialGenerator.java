package ru.yandex.practicum.taskmanager.utils;

public class SerialGenerator implements Generator{

    private static int count;

    public SerialGenerator(int start) {
        count=start-1;
    }

    @Override
    public Integer getId() {
        count++;
        return count;
    }
}
