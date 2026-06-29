package com.example.spyfall.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Spy2 {
    int id;
    String ipConfig;
    String userName;
    String keyword;
    boolean isRemove = false;
    String role = "Dân Thường";
}
