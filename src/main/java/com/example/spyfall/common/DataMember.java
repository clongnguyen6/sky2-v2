package com.example.spyfall.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataMember {

    String id;

    String location;

    String ipData;

    String description;

    String total;

    String nameMember;

    String idPlayGame;

    public DataMember(Integer id, String location) {
        this.id = String.valueOf(id);
        this.location = location;
    }

    public DataMember(String id, String location, String description) {
        this.id = id;
        this.location = location;
        this.description =description;
    }

    public DataMember(DataMember dataMember, Integer total) {
        this.id = dataMember.getId();
        this.location = dataMember.getLocation();
        this.description = dataMember.getDescription();
        this.total = String.valueOf(total);
    }
}
