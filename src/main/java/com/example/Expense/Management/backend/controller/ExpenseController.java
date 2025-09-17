package com.example.Expense.Management.backend.controller;

import com.example.Expense.Management.backend.dto.ExpenseDTO;
import com.example.Expense.Management.backend.model.Expense.ExpenseStatus;
import com.example.Expense.Management.backend.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> submitExpense(
            @RequestParam String expenseName,
            @RequestParam BigDecimal price,
            @RequestParam String date,
            @RequestParam(required = false) MultipartFile proofImage,
            Authentication authentication) throws IOException {

        String userEmail = authentication.getName();
        LocalDate expenseDate = LocalDate.parse(date);

        ExpenseDTO expense = expenseService.createExpense(expenseName, price, expenseDate, proofImage, userEmail);
        return ResponseEntity.ok(expense);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ExpenseDTO>> getMyExpenses(Authentication authentication) {
        String userEmail = authentication.getName();
        List<ExpenseDTO> expenses = expenseService.getUserExpenses(userEmail);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses() {
        List<ExpenseDTO> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateExpenseStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");
        ExpenseStatus status;
        try {
            status = ExpenseStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body("Invalid status value");
        }

        try {
            ExpenseDTO updatedExpense = expenseService.updateExpenseStatus(id, status);
            return ResponseEntity.ok(updatedExpense);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status transition: " + e.getMessage());
        }
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Path filePath = Paths.get("uploads/").resolve(filename).normalize();
        Resource resource = new FileSystemResource(filePath);

        if (resource.exists() && resource.isReadable()) {
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            String lowerFilename = filename.toLowerCase();
            if (lowerFilename.endsWith(".pdf")) {
                mediaType = MediaType.APPLICATION_PDF;
            } else if (lowerFilename.endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
                mediaType = MediaType.IMAGE_JPEG;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
