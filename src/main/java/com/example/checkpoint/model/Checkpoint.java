package com.example.checkpoint.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Checkpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "Description")
    @NotBlank(message = "Checkpoint Description cannot be blank")
    private String checkpointDescription;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> taskList;

    private double percentComplete;

}
