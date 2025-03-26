package id.co.bcaf.solvr.controller;

import id.co.bcaf.solvr.model.account.Role;
import id.co.bcaf.solvr.repository.RoleRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/roles")
public class RoleController {
    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public Iterable<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @PostMapping
    public Role createRole(@RequestBody Role role) {
        return roleRepository.save(role);
    }

    @PutMapping("{id}")
    public Role updateRole(@RequestBody Role role) {
        return roleRepository.save(role);
    }

    @DeleteMapping("{id}")
    public void deleteRole(@PathVariable("id") UUID id) {
        roleRepository.deleteById(id);
    }


}
