package com.fesa.sharetools.Service;

import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;

import java.util.List;
import java.util.Optional;

public interface ToolService extends BaseService<Tool, Long> {
    List<Tool> findByOwner(User user);
    List<Tool> findAllExceptOwner(User user);
    Optional<Tool> findById(Long id);
    Tool getById(Long id);
    Tool save(Tool tool);
    List<Tool> findByOwnerNotAndAvailableTrue(User user);
    void delete(Long id);
    List<Tool> getAvailableToolsFromOtherUsers(User currentUser);
}
