package com.example.web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


@Entity
@Builder
@Table(name = "photos")
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

  @Id
  private String id;

  private String name;

  @Lob
  @JdbcTypeCode(SqlTypes.BINARY)
  private byte[] photo;

}
