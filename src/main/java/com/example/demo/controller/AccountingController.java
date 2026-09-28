package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.service.AccountingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance")
public class AccountingController {
    private final AccountingService accountingService;

    public AccountingController(AccountingService accountingService) {
        this.accountingService = accountingService;
    }

    // Contacts
    @GetMapping("/contacts")
    public List<Contact> getContacts() {
        return accountingService.getContacts();
    }

    @PostMapping("/contacts")
    @ResponseStatus(HttpStatus.CREATED)
    public Contact createContact(@Valid @RequestBody CreateContactRequest request) {
        return accountingService.createContact(request);
    }

    // Products & Accounts
    @GetMapping("/products")
    public List<Product> getProducts() {
        return accountingService.getProducts();
    }

    @GetMapping("/accounts")
    public List<Account> getAccounts() {
        return accountingService.getAccounts();
    }

    // Purchase Orders (Lab testing, rescue gear)
    @GetMapping("/orders/purchase")
    public List<PurchaseOrder> getPurchaseOrders() {
        return accountingService.getPurchaseOrders();
    }

    @PostMapping("/orders/purchase")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrder createPurchaseOrder(@Valid @RequestBody CreatePurchaseOrderRequest request) {
        return accountingService.createPurchaseOrder(request);
    }

    @PostMapping("/orders/purchase/{id}/convert-to-bill")
    public Invoice convertPoToBill(@PathVariable Long id) {
        return accountingService.convertPoToBill(id);
    }

    // Sales Orders (Commercial Beach Event Permits)
    @GetMapping("/orders/sales")
    public List<SalesOrder> getSalesOrders() {
        return accountingService.getSalesOrders();
    }

    @PostMapping("/orders/sales")
    @ResponseStatus(HttpStatus.CREATED)
    public SalesOrder createSalesOrder(@Valid @RequestBody CreateSalesOrderRequest request) {
        return accountingService.createSalesOrder(request);
    }

    @PostMapping("/orders/sales/{id}/convert-to-invoice")
    public Invoice convertSalesOrderToInvoice(@PathVariable Long id) {
        return accountingService.convertSalesOrderToInvoice(id);
    }

    // Invoices & Payments
    @GetMapping("/invoices")
    public List<Invoice> getInvoices() {
        return accountingService.getInvoices();
    }

    @PostMapping("/invoices/{id}/pay")
    public Payment payInvoice(@PathVariable Long id, @RequestBody(required = false) ProcessPaymentRequest request) {
        if (request == null) {
            request = new ProcessPaymentRequest(null, PaymentMethod.BANK_TRANSFER);
        }
        return accountingService.payInvoice(id, request);
    }

    // Journal Entries
    @GetMapping("/journal-entries")
    public List<JournalEntry> getJournalEntries() {
        return accountingService.getJournalEntries();
    }

    // Financial Reports
    @GetMapping("/reports")
    public FinancialReportResponse getFinancialReport() {
        return accountingService.getFinancialReport();
    }
}
