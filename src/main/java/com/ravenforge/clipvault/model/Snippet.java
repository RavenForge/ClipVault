package com.ravenforge.clipvault.model;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "snippet")
@Getter
@Setter
public class Snippet extends Model {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(nullable = false)
    private String value;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tab_id")
    private Tab tab;

    @WhenCreated
    @Column(name = "created_at")
    private Instant createdAt;
}
