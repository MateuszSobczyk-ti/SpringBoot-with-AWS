package com.sobczyk.testThing.repository;

import com.sobczyk.testThing.entity.File;
import org.springframework.data.repository.CrudRepository;

public interface FileRepository extends CrudRepository<File, Long> {
    boolean existsByName(String name);
}
