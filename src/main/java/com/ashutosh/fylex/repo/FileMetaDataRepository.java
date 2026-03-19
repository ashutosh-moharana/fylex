package com.ashutosh.fylex.repo;

import com.ashutosh.fylex.model.FileMetaData;
import com.ashutosh.fylex.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileMetaDataRepository extends JpaRepository<FileMetaData,Long> {
    Optional<FileMetaData> findByIdAndRoom(Long id, Room room);
}
