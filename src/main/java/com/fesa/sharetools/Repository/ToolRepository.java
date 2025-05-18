package com.fesa.sharetools.Repository;

import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToolRepository extends JpaRepository<Tool, Long> {
    List<Tool> findByOwner(User owner);
    List<Tool> findByOwnerNot(User owner);
}
