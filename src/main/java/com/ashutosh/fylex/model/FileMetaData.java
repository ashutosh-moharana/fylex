package com.ashutosh.fylex.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="files")
@Builder
public class FileMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;

    private String storedFileName;

    private Long size;

    private LocalDateTime uploadTime;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

}
