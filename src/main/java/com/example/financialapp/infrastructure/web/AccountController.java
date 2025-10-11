package com.example.financialapp.infrastructure.web;

import com.example.financialapp.application.port.in.AccountUseCase;
import com.example.financialapp.infrastructure.web.dto.CreateAccountRequest;
import com.example.financialapp.infrastructure.web.dto.MoneyRequest;
import com.example.financialapp.infrastructure.web.dto.TransactionRes;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts")
public class AccountController {
    private final AccountUseCase useCase;
    public AccountController(AccountUseCase useCase) { this.useCase = useCase; }

    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> open(@Validated @RequestBody CreateAccountRequest body,
                                       Authentication auth) {
        UUID id = useCase.open(body.customerId(), body.type());
        return ResponseEntity.created(URI.create("/accounts/" + id)).build();
    }

    @PreAuthorize("hasRole('EMPLOYEE') or #id.toString() == authentication.principal")
    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> balance(@PathVariable UUID id){
        return ResponseEntity.ok(useCase.balanceOf(id));
    }

    @PreAuthorize("hasRole('EMPLOYEE') or #id.toString() == authentication.principal")
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionRes>> history(@PathVariable UUID id){
        return ResponseEntity.ok(useCase.historyOf(id));
    }

    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @PostMapping("/{id}/deposit")
    public ResponseEntity<Void> deposit(@PathVariable UUID id, @Validated @RequestBody MoneyRequest body) {
        useCase.deposit(id, body.amount());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Void> withdraw(@PathVariable UUID id, @Validated @RequestBody MoneyRequest body) {
        useCase.withdraw(id, body.amount());
        return ResponseEntity.noContent().build();
    }
}
