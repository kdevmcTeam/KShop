package ru.kdev.kshop.updater;

import com.google.gson.annotations.SerializedName;

/**
 * @author artem
 */
public class CommitAuthor {

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }


}
