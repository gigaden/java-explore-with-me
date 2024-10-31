package ru.practictum.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
    private LocalDateTime timestamp = LocalDateTime.now();


}
