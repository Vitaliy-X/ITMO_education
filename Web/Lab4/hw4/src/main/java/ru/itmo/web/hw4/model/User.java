package ru.itmo.web.hw4.model;

import ru.itmo.web.hw4.util.DataUtil;

import java.util.List;
import java.util.stream.Collectors;

public class User {
    private final long id;
    private final String handle;
    private final String name;

    public User(long id, String handle, String name) {
        this.id = id;
        this.handle = handle;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getHandle() {
        return handle;
    }

    public String getName() {
        return name;
    }

    public List<Post> getPosts() {
        return DataUtil.POSTS.stream().filter(post -> post.getUserId() == id).collect(Collectors.toList());
    }

    public long getPostCount() {
        return DataUtil.POSTS.stream().filter(post -> post.getUserId() == id).count();
    }
}
