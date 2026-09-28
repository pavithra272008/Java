package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entry_number", nullable = false, unique = true, length = 50)
    private String entryNumber;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "journal_type", nullable = false, length = 30)
    private JournalType journalType;

    @Column(length = 50)
    private String reference;

    @Column(length = 250)
    private String description;

    @Column(name = "debit_account_code", nullable = false, length = 20)
    private String debitAccountCode;

    @Column(name = "credit_account_code", nullable = false, length = 20)
    private String creditAccountCode;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    protected JournalEntry() {}

    public JournalEntry(String entryNumber, LocalDate entryDate, JournalType journalType, String reference, String description, String debitAccountCode, String creditAccountCode, BigDecimal amount) {
        this.entryNumber = entryNumber;
        this.entryDate = entryDate;
        this.journalType = journalType;
        this.reference = reference;
        this.description = description;
        this.debitAccountCode = debitAccountCode;
        this.creditAccountCode = creditAccountCode;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public String getEntryNumber() { return entryNumber; }
    public LocalDate getEntryDate() { return entryDate; }
    public JournalType getJournalType() { return journalType; }
    public String getReference() { return reference; }
    public String getDescription() { return description; }
    public String getDebitAccountCode() { return debitAccountCode; }
    public String getCreditAccountCode() { return creditAccountCode; }
    public BigDecimal getAmount() { return amount; }
}
