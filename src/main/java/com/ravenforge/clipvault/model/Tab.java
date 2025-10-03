package com.ravenforge.clipvault.model;

import io.ebean.Model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tab")
@Getter
@Setter
public class Tab extends Model {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob
    @Column
    private String password;
}
