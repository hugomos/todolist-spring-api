package br.com.hugomos.todolist.task;

import br.com.hugomos.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        UUID userId = (UUID) request.getAttribute("userId");

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio não pode ser menor que a data atual.");
        }

        if(taskModel.getFinalAt().isBefore(taskModel.getStartAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de fim não pode ser menor que a data de inicio.");
        }

        taskModel.setUserId(userId);
        var task = taskRepository.save(taskModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping("")
    public ResponseEntity list(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");

        var tasks = taskRepository.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID taskId) {
        UUID userId = (UUID) request.getAttribute("userId");
        var task = this.taskRepository.findById(taskId).orElse(null);

        if(task == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada ou não existe");
        }

        if(!task.getUserId().equals(userId)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario não tem permissao para executar ação");
        }

        Utils.copyNonNullProperties(taskModel, task);
        var updatedTask = taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }
}
