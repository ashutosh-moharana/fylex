package com.ashutosh.fylex.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {
    @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

    @Column(unique = true,nullable = false)
    private String roomCode;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;


    @OneToMany(mappedBy = "room",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<FileMetaData> files = new ArrayList<>();

}
