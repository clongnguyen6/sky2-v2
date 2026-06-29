package com.example.spyfall.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoDuck {
    Integer id;
    String ipConfig;
    String userName;

    @Override
    public String toString() {
        return id + ". " + userName + ". ";
    }
}
