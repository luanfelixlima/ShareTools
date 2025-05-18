package com.fesa.sharetools.Service;

import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Repository.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
