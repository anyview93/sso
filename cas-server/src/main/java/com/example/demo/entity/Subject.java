package com.example.demo.entity;

/**
 * @author shizhiguo
 * @ClassName Subject
 * @date 2019-03-26
 */
public class Subject {
    private String sessionId;
    private String logoutUrl;
    private String ticket;

    public Subject() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public static class Builder{
        private String sessionId;
        private String logoutUrl;
        private String ticket;

        public Builder() {
        }

        public String getSessionId() {
            return sessionId;
        }

        public Builder setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public String getLogoutUrl() {
            return logoutUrl;
        }

        public Builder setLogoutUrl(String logoutUrl) {
            this.logoutUrl = logoutUrl;
            return this;
        }

        public String getTicket() {
            return ticket;
        }

        public Builder setTicket(String ticket) {
            this.ticket = ticket;
            return this;
        }

        public Subject build(){
            return new Subject(this);
        }
    }

    private Subject(Builder builder){
        sessionId = builder.sessionId;
        logoutUrl = builder.logoutUrl;
        ticket = builder.ticket;
    }
}
