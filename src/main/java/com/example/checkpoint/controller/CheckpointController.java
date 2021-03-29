package com.example.checkpoint.controller;

import com.example.checkpoint.exceptions.CheckpointNotFoundException;
import com.example.checkpoint.model.Checkpoint;
import com.example.checkpoint.model.CheckpointRepository;
import com.example.checkpoint.model.Task;
import com.example.checkpoint.model.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Validated
public class CheckpointController {

    @Autowired
    CheckpointRepository checkpointRepository;

    @Autowired
    TaskRepository taskRepository;

    //Get all checkpoints
    @GetMapping("/checkpoints")
    public Iterable<Checkpoint> getAllCheckList() {
        return checkpointRepository.findAll();
    }

    //Get checkpoints by id
    @GetMapping("/checkpoint/{checkpointId}")
    public ResponseEntity<Checkpoint> getCheckpoint(@PathVariable @Positive Long checkpointId) {
        return ResponseEntity.ok(checkpointRepository.findById(checkpointId).get());
    }


    //Add a checkpoint
    @PostMapping("/addCheckpoint")
    @Valid
    ResponseEntity<Checkpoint> newCheckPoint(@RequestBody @Valid Checkpoint newCheckpoint) {
        taskRepository.saveAll(newCheckpoint.getTaskList());
        return ResponseEntity.ok(checkpointRepository.save(newCheckpoint));
    }

    //Add a task to a checkpoint by checkpoint id
    @PostMapping("/addTask/{checkpointId}")
    @Valid
    ResponseEntity<Checkpoint> addTaskToCheckPoint(@RequestBody @Valid Task newTask, @PathVariable @Positive Long checkpointId) throws CheckpointNotFoundException {

        Optional<Checkpoint> checkpoint = checkpointRepository.findById(checkpointId);
        if (checkpoint.isPresent()) {
            taskRepository.save(newTask);
            checkpoint.get().getTaskList().add(newTask);
        } else {
            throw new CheckpointNotFoundException("Checkpoint not found");
        }
        return ResponseEntity.ok(checkpointRepository.save(checkpoint.get()));
    }

    //Delete a task of a checkpoint
    @DeleteMapping("/checkpoint/{checkpointId}/{taskId}")
    ResponseEntity<String> deleteTask(@PathVariable @Positive Long checkpointId, @PathVariable @Positive Long taskId) throws CheckpointNotFoundException {
        Checkpoint checkpoint = checkpointRepository.findById(checkpointId).get();
        if (null == checkpoint) {
            throw new CheckpointNotFoundException("Checkpoint not found");
        } else {
            checkpoint.getTaskList().removeIf(e -> e.getId() == taskId);
            checkpointRepository.save(checkpoint);
            return ResponseEntity.ok("Task deleted successfully");
        }

    }

    @DeleteMapping("/checkpoint/{checkpointId}")
    ResponseEntity<String> deleteCheckPoint(@PathVariable @Positive Long checkpointId) throws CheckpointNotFoundException {
        Optional<Checkpoint> checkpoint = checkpointRepository.findById(checkpointId);
        if (checkpoint.isPresent()) {
            checkpointRepository.deleteById(checkpointId);
            return ResponseEntity.ok("Checkpoint successfully deleted");

        } else {
            throw new CheckpointNotFoundException("Checkpoint not found");
        }
    }

    @PutMapping("/task")
    ResponseEntity<String> updateTaskCompletedStatus(@RequestBody @Valid Task newTask) throws CheckpointNotFoundException {

        //Calculate percent complete
        Optional<Task> task = taskRepository.findById(newTask.getId());
        long checkpointId = checkpointRepository.findCheckpointByTaskId(newTask.getId());
        taskRepository.save(newTask);
        Optional<Checkpoint> checkpoint = checkpointRepository.findById(checkpointId);

        if (checkpoint.isPresent()) {
            try {
                long totalTasks = checkpoint.get().getTaskList().size();
                long completedTasks = checkpoint.get().getTaskList().stream().filter(e -> e.isCompleted()).count();
                double percentCompleted = ((double) completedTasks / totalTasks) * 100;
                BigDecimal bd = new BigDecimal(percentCompleted).setScale(2, RoundingMode.HALF_UP);
                checkpoint.get().setPercentComplete(bd.doubleValue());
            } catch (Exception e) {
                checkpoint.get().setPercentComplete(0d);
            }
        }

        taskRepository.save(newTask);
        return ResponseEntity.ok("Task updated");

    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return errors;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(cv -> {
            errors.put("message", cv.getMessage());
            errors.put("path", (cv.getPropertyPath()).toString());
        });

        return errors;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CheckpointNotFoundException.class)
    public Map<String, String> handleCheckpointNotFoundException(CheckpointNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Error: ", ex.getMessage());
        return errors;
    }

}
