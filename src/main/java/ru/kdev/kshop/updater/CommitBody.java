package ru.kdev.kshop.updater;

import com.google.gson.annotations.SerializedName;

/**
 * @author artem
 */
public class CommitBody {

    @SerializedName("message")
    private String message;

    @SerializedName("author")
    private CommitAuthor author;

    public CommitAuthor getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

}
