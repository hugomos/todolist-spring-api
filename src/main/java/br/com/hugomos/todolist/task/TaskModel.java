package br.com.hugomos.todolist.task;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;

    @Column(length = 50)
    private String title;
    private String description;
    private String priority;

    private LocalDateTime startAt;
    private LocalDateTime finalAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void setTitle(String title) throws Exception {
        if(title.length() > 50){
            throw new Exception("O campo deve conter no maximo 50 caracteres.");
        }

        this.title = title;
    }
}
