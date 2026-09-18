package com.example.Kanban.Board.configuration;

import java.security.Principal;

import com.example.Kanban.Board.annotations.CreatedBy;
import com.example.Kanban.Board.annotations.UpdatedBy;
import com.example.Kanban.Board.utilities.ReflectionUtils;

import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@ApplicationScoped
public class AuditingEntityListener {

    private final SecurityIdentity securityIdentity;

    public AuditingEntityListener(SecurityIdentity securityIdentity) {
        this.securityIdentity = securityIdentity;
    }

    @PrePersist
    public void prePersist(Object entity) throws IllegalAccessException {
        ReflectionUtils.setValueByAnnotation(entity, CreatedBy.class, getCurrentUserEmail());
    }

    @PreUpdate
    public void preUpdate(Object entity) throws IllegalAccessException {
        ReflectionUtils.setValueByAnnotation(entity, UpdatedBy.class, getCurrentUserEmail());
    }

    private String getCurrentUserEmail() {
        Principal principal = securityIdentity.getPrincipal();
        if (principal == null) {
            return null;
        }
        if (principal instanceof DefaultJWTCallerPrincipal jwtPrincipal) {
            return jwtPrincipal.getClaim("sub");
        }
        return principal.getName();
    }
}
