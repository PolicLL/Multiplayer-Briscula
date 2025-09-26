package com.example.web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


@Entity
@Builder
@Data
@Table(name = "photos")
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

    @Id
    @Getter
    private String id;

    @Setter
    private String name;

    @Lob
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] photo;

}
