package ru.practictum.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "statistics")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Statistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app")
    @NonNull
    @NotBlank
    private String app;

    @Column(name = "uri")
    @NonNull
    @NotBlank
    private String uri;

    @Column(name = "ip")
    @NonNull
    @NotBlank
    private String ip;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;


}
