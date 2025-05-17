package com.fesa.sharetools.Repository;

import com.fesa.sharetools.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // Apenas necessário se quiser buscar por nome:
    // Optional<Role> findByName(String name);
}
