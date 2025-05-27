package com.fesa.sharetools.Service;

import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Repository.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional; // Added Optional import

@Service
public class ToolServiceImpl extends BaseServiceImpl<Tool, Long> implements ToolService {

    private final ToolRepository toolRepository;

    @Autowired
    public ToolServiceImpl(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    @Override
    protected JpaRepository<Tool, Long> getRepository() {
        return toolRepository;
    }

    @Override
    public List<Tool> findByOwner(User user) {
        return toolRepository.findByOwner(user);
    }

    @Override
    public List<Tool> findAllExceptOwner(User user) {
        return toolRepository.findByOwnerNot(user);
    }

    @Override
    public Optional<Tool> findById(Long id) { // Added Optional return type
        return toolRepository.findById(id);
    }

    @Override
    public Tool getById(Long id) {
        return toolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ferramenta com id '" + id + "' não encontrada."));
    }

    @Override
    public Tool save(Tool tool) {
        return toolRepository.save(tool);
    }

    @Override
    public List<Tool> getAvailableToolsFromOtherUsers(User currentUser) {
        return toolRepository.findByOwnerNotAndAvailableTrue(currentUser);
    }

    @Override
    public List<Tool> findByOwnerNotAndAvailableTrue(User user) {
        return toolRepository.findByOwnerNotAndAvailableTrue(user);
    }

    @Override
    public void delete(Long id) {
        toolRepository.deleteById(id);
    }
}
