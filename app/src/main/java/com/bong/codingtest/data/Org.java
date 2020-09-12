package com.bong.codingtest.data;

public class Org {
    public String login;
    public int id;
    public String node_id;
    public String url;
    public String repos_url;
    public String events_url;
    public String hooks_url;
    public String issues_url;
    public String members_url;
    public String public_members_url;
    public String avatar_url;
    public String description;

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
