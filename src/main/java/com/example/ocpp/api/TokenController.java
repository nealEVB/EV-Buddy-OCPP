package com.example.ocpp.api;

import com.example.ocpp.domain.UserToken;
import com.example.ocpp.repo.UserTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tokens")
//@ConditionalOnProperty(name = "database.enabled", havingValue = "true", matchIfMissing = false)
public class TokenController {
    @Autowired private UserTokenRepo repo;

    @PostMapping
    public UserToken create(@RequestBody UserToken t){ return repo.save(t); }
    @PutMapping("/{id}")
    public UserToken update(@PathVariable Long id, @RequestBody UserToken t){ t.setId(id); return repo.save(t); }
}
