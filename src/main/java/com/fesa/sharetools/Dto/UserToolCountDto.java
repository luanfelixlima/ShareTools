package com.fesa.sharetools.Dto;

public class UserToolCountDto {
    private String userName;
    private long toolCount;

    public UserToolCountDto(String userName, long toolCount) {
        this.userName = userName;
        this.toolCount = toolCount;
    }

    // Getters
    public String getUserName() {
        return userName;
    }

    public long getToolCount() {
        return toolCount;
    }

    // Setters (opcional, mas bom ter)
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setToolCount(long toolCount) {
        this.toolCount = toolCount;
    }
}