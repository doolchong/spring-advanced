package org.example.expert.domain.manager.repository;

import org.example.expert.domain.manager.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    @Query("SELECT m FROM Manager m JOIN FETCH m.user WHERE m.todo.id = :todoId")
    List<Manager> findByTodoIdWithUser(@Param("todoId") Long todoId);

    Optional<Manager> findByTodo_IdAndUser_Id(long todoId, long userId);
}
