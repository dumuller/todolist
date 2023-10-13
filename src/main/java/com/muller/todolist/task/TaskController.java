package com.muller.todolist.task;

import com.muller.todolist.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    TaskRepository taskRepository;

    @PostMapping
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {

        if (LocalDateTime.now().isAfter(taskModel.getDataInicio()) ||
            LocalDateTime.now().isAfter(taskModel.getDataFim())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verifique as datas informas. Data e ínicio e fim devem ser válidas!");
        }

        taskModel.setIdUsuario((UUID) request.getAttribute("idUser"));
        var taskSalva = taskRepository.save(taskModel);
        return ResponseEntity.ok(taskSalva);
    }

    @GetMapping
    public ResponseEntity<List<TaskModel>> getTasks(HttpServletRequest request) {
        var idUser = (UUID) request.getAttribute("idUser");
        return ResponseEntity.ok(taskRepository.findAllByIdUsuario(idUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity putTasks(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        Optional<TaskModel> taskParaAtualizar = taskRepository.findById(id);

        if (!taskParaAtualizar.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id inválido");

        var idUsuario = request.getAttribute("idUser");

        if (!taskParaAtualizar.get().getIdUsuario().equals(idUsuario))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão para alterar essa tarefa");

        Utils.copyNonNullProperties(taskModel, taskParaAtualizar.get());

        return ResponseEntity.ok(taskRepository.save(taskParaAtualizar.get()));
    }
}
