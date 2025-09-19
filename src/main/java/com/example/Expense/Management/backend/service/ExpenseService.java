package com.example.Expense.Management.backend.service;

import com.example.Expense.Management.backend.dto.ExpenseDTO;
import com.example.Expense.Management.backend.dto.UserDTO;
import com.example.Expense.Management.backend.model.Expense;
import com.example.Expense.Management.backend.model.User;
import com.example.Expense.Management.backend.model.Expense.ExpenseStatus;
import com.example.Expense.Management.backend.repository.ExpenseRepository;
import com.example.Expense.Management.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.Expense.Management.backend.service.S3Service;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private S3Service s3Service;

    // private static final String UPLOAD_DIR = "uploads/";

    @org.springframework.beans.factory.annotation.Value("${app.upload.dir}")
    private String uploadDir;

    public ExpenseDTO createExpense(String expenseName, BigDecimal price, LocalDate date,
                                MultipartFile proofImage, String userEmail) throws IOException {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = new Expense(expenseName, price, date, null, user);
        if (proofImage != null && !proofImage.isEmpty()) {
            String fileName = saveFile(proofImage);
            expense.setProofImagePath(fileName);
        }

        Expense savedExpense = expenseRepository.save(expense);
        return convertToDTO(savedExpense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseDTO> getUserExpenses(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Expense> expenses = expenseRepository.findByUser(user);
        return expenses.stream()
                .map(expense -> convertToDTO(expense))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpenseDTO> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        return expenses.stream()
                .map(expense -> convertToDTO(expense))
                .collect(Collectors.toList());
    }

    @Transactional
    public ExpenseDTO updateExpenseStatus(Long expenseId, ExpenseStatus status) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        // Validate status transitions
        ExpenseStatus currentStatus = expense.getStatus();
        if (currentStatus == ExpenseStatus.PENDING && (status == ExpenseStatus.APPROVED || status == ExpenseStatus.REJECTED)) {
            // Allow
        } else if (currentStatus == ExpenseStatus.APPROVED && status == ExpenseStatus.PAID) {
            // Allow
        } else {
            throw new IllegalArgumentException("Invalid status transition from " + currentStatus + " to " + status);
        }

        expense.setStatus(status);
        Expense updatedExpense = expenseRepository.save(expense);
        return convertToDTO(updatedExpense);
    }

    public List<Expense> getExpensesByStatus(ExpenseStatus status) {
        return expenseRepository.findByStatus(status);
    }

    public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByDateRange(startDate, endDate);
    }

    private String saveFile(MultipartFile file) throws IOException {
        String fileName = "invoices/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        byte[] fileBytes = file.getBytes();
        String contentType = file.getContentType();
        String s3Url = s3Service.uploadFile(fileName, fileBytes, contentType);
        return s3Url;
    }

    private ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setExpenseName(expense.getExpenseName());
        dto.setPrice(expense.getPrice());
        dto.setDate(expense.getDate());
        dto.setProofImagePath(expense.getProofImagePath());
        dto.setStatus(expense.getStatus().name());
        dto.setUser(convertToUserDTO(expense.getUser()));
        return dto;
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet()));
        return dto;
    }
}
