package ru.kdev.kshop.updater;

import com.google.gson.annotations.SerializedName;

/**
 * @author artem
 */
public class Commit {
    @SerializedName("sha")
    private String sha;

    @SerializedName("commit")
    private CommitBody body;

    public String getMessage() {
        return body.getMessage();
    }

    public String getAuthorName() {
        return body.getAuthor().getName();
    }

    public String getShortSha() {
        return sha.substring(0, 8);
    }

    @Override
    public String toString() {
        return "Commit[sha=" + getShortSha() + ", message=" + getMessage() + ", author=" + getAuthorName() + "]";
    }

}
