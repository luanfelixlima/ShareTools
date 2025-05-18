package com.fesa.sharetools.Service;

import com.fesa.sharetools.Model.Tool;
import com.fesa.sharetools.Model.User;

import java.util.List;

public interface ToolService extends BaseService<Tool, Long> {
    List<Tool> findByOwner(User user);
    List<Tool> findAllExceptOwner(User user);

}
