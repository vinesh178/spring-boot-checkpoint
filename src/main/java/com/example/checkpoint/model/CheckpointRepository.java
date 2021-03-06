package com.example.checkpoint.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {

    @Query(value = "Select CHECKPOINT_ID from CHECKPOINT_TASK_LIST where TASK_LIST_ID=:taskId", nativeQuery = true)
    public Long findCheckpointByTaskId(Long taskId);

}
