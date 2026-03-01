package com.ashutosh.fylex.repo;

import com.ashutosh.fylex.model.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetaDataRepository extends JpaRepository<FileMetaData,Long> {
}
