package com.bong.codingtest.data;

import java.util.List;

public class User {
    public String login;
//    public int id;
//    public String node_id;
    public String avatar_url;
//    public String gravatar_id;
//    public String url;
//    public String html_url;
//    public String followers_url;
//    public String following_url;
//    public String gists_url;
//    public String starred_url;
//    public String subscriptions_url;
//    public String organizations_url;
//    public String repos_url;
//    public String events_url;
//    public String received_events_url;
//    public String type;
//    public boolean site_admin;

    public Integer score;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
