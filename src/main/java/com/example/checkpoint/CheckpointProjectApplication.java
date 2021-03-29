package com.example.checkpoint;

import com.example.checkpoint.model.Checkpoint;
import com.example.checkpoint.model.CheckpointRepository;
import com.example.checkpoint.model.Task;
import com.example.checkpoint.model.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class CheckpointProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckpointProjectApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(CheckpointRepository repository, TaskRepository taskRepository) {
        return args -> {

            List<Task> taskList1 = Arrays.asList(new Task(1l, "Meeting With stakeholders", false), new Task(2l, "Technology discussions", false),
                    new Task(3l, "Documenting", false), new Task(4l, "Approvals", false));

            taskRepository.saveAll(taskList1);
            repository.save(new Checkpoint(11l, "Gather Requirements", taskList1,0d));

            List<Task> taskList2 = Arrays.asList(new Task(5l, "Process confirmation", false), new Task(6l, "Setting up documentations", false),
                    new Task(7l, "Sprint planning", false), new Task(8l, "Gap assessment", false));

            taskRepository.saveAll(taskList2);
            repository.save(new Checkpoint(2l, "Project Kickoff", taskList2,0d));

            List<Task> taskList3 = Arrays.asList(new Task(22l, "Setting up DEV environment", false), new Task(23l, "Unit Test", false),
                    new Task(24l, "Development of new features", false), new Task(30l, "Developer trainings", false));

            taskRepository.saveAll(taskList3);
            repository.save(new Checkpoint(3l, "Build", taskList3,0d));

            List<Task> taskList4 = Arrays.asList(new Task(34l, "Setting up QA environment", false), new Task(35l, "ITC Test", false),
                    new Task(43l, "Key Users playback", false), new Task(29l, "BA users trainings", false));

            taskRepository.saveAll(taskList4);
            repository.save(new Checkpoint(4l, "Integration test", taskList4,0d));

            List<Task> taskList5 = Arrays.asList(new Task(66l, "Setting up PRE-PROD environment", false), new Task(87l, "UAT Test", false),
                    new Task(32l, "UAT test data", false), new Task(49l, "End User trainings", false));

            taskRepository.saveAll(taskList5);
            repository.save(new Checkpoint(77l, "User Acceptance Test", taskList5,0d));


        };
    }

}
