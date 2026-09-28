package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountingService {
    private final AccountRepository accountRepository;
    private final ContactRepository contactRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final BeachSectorRepository beachSectorRepository;

    private final AtomicLong seqCounter = new AtomicLong(100);

    public AccountingService(
            AccountRepository accountRepository,
            ContactRepository contactRepository,
            ProductRepository productRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            SalesOrderRepository salesOrderRepository,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            JournalEntryRepository journalEntryRepository,
            BeachSectorRepository beachSectorRepository) {
        this.accountRepository = accountRepository;
        this.contactRepository = contactRepository;
        this.productRepository = productRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.beachSectorRepository = beachSectorRepository;
    }

    @PostConstruct
    @Transactional
    public void initMasterData() {
        if (accountRepository.count() > 0) return;

        // 1. Chart of Accounts Master (Use Case 42)
        // Assets
        accountRepository.save(new Account("1010", "Cash & Bank Reserves", AccountType.ASSET, new BigDecimal("142000.00")));
        accountRepository.save(new Account("1030", "Ocean Testing Gear", AccountType.ASSET, new BigDecimal("12500.00")));
        accountRepository.save(new Account("1040", "Rescue Watercraft Assets", AccountType.ASSET, new BigDecimal("85000.00")));
        // Liabilities
        accountRepository.save(new Account("2010", "Testing Lab Payables", AccountType.LIABILITY, new BigDecimal("2400.00")));
        accountRepository.save(new Account("2020", "Equipment Creditors", AccountType.LIABILITY, new BigDecimal("6100.00")));
        // Income
        accountRepository.save(new Account("4010", "Beach Event Permit Revenue", AccountType.INCOME, new BigDecimal("38500.00")));
        accountRepository.save(new Account("4020", "Coastal Commercial Fees", AccountType.INCOME, new BigDecimal("14200.00")));
        // Expenses
        accountRepository.save(new Account("5020", "Lab Testing Expenses", AccountType.EXPENSE, new BigDecimal("12800.00")));
        accountRepository.save(new Account("5030", "Rescue Craft Fuel & Maintenance", AccountType.EXPENSE, new BigDecimal("8400.00")));

        // 2. Contact Master
        Contact cust = contactRepository.save(new Contact(
                "Coastal Surf & Volleyball Tournaments Inc.",
                ContactType.CUSTOMER,
                "events@coastalsports.org",
                "+1 (555) 234-8901",
                "PAC-EV-901"
        ));

        Contact labVendor = contactRepository.save(new Contact(
                "Pacific Marine Bacteriological Laboratories",
                ContactType.VENDOR,
                "contracts@pacificlab.org",
                "+1 (555) 872-3321",
                "V-LAB-44"
        ));

        Contact eqpVendor = contactRepository.save(new Contact(
                "Oceanic Rescue Craft & Safety Equipment Ltd.",
                ContactType.VENDOR,
                "fleet@oceanicrescue.com",
                "+1 (555) 431-9988",
                "V-EQP-12"
        ));

        // 3. Product Master
        productRepository.save(new Product(
                "Commercial Beach Event Permit",
                ProductType.SERVICE,
                new BigDecimal("4500.00"),
                "Authorized tournament and event permit for municipal beach sectors",
                "4010"
        ));

        productRepository.save(new Product(
                "Weekly Ocean Water Bacteriological Testing Service",
                ProductType.SERVICE,
                new BigDecimal("1200.00"),
                "Standard enterococci & fecal coliform lab analysis",
                "5020"
        ));

        productRepository.save(new Product(
                "Rescue Jet-Ski Equipment (Goods)",
                ProductType.GOODS,
                new BigDecimal("24500.00"),
                "High-output marine search & rescue watercraft",
                "1040"
        ));

        // 4. Sample Transactions
        LocalDate today = LocalDate.now();
        // Lab PO
        PurchaseOrder po = purchaseOrderRepository.save(new PurchaseOrder(
                "PO-2026-031",
                labVendor,
                today.minusDays(5),
                OrderStatus.BILLED,
                new BigDecimal("1200.00"),
                "Weekly bacteriological water sample testing (Sectors 1-4)"
        ));

        Invoice bill = invoiceRepository.save(new Invoice(
                "BILL-2026-079",
                InvoiceType.VENDOR_BILL,
                labVendor,
                po.getPoNumber(),
                today.minusDays(4),
                today.plusDays(26),
                new BigDecimal("1200.00")
        ));
        bill.setStatus(InvoiceStatus.PAID);
        bill.setAmountPaid(new BigDecimal("1200.00"));
        invoiceRepository.save(bill);

        paymentRepository.save(new Payment(
                "PAY-2026-011",
                bill,
                today.minusDays(2),
                new BigDecimal("1200.00"),
                PaymentMethod.BANK_TRANSFER
        ));

        // Commercial Sales Order & Invoice
        SalesOrder so = salesOrderRepository.save(new SalesOrder(
                "SO-2026-088",
                cust,
                null,
                today.minusDays(3),
                OrderStatus.INVOICED,
                new BigDecimal("4500.00"),
                "Summer Beach Volleyball Open - Commercial Event Permit (Zone 1)"
        ));

        Invoice inv = invoiceRepository.save(new Invoice(
                "INV-2026-142",
                InvoiceType.CUSTOMER_INVOICE,
                cust,
                so.getOrderNumber(),
                today.minusDays(2),
                today.plusDays(28),
                new BigDecimal("4500.00")
        ));
        inv.setStatus(InvoiceStatus.PAID);
        inv.setAmountPaid(new BigDecimal("4500.00"));
        invoiceRepository.save(inv);

        paymentRepository.save(new Payment(
                "PAY-2026-015",
                inv,
                today.minusDays(1),
                new BigDecimal("4500.00"),
                PaymentMethod.BANK_TRANSFER
        ));

        // 5. Journal Entries
        journalEntryRepository.save(new JournalEntry(
                "JE-2026-001",
                today.minusDays(2),
                JournalType.BANK,
                bill.getInvoiceNumber(),
                "Disbursement for weekly ocean water quality testing PO",
                "2010",
                "1010",
                new BigDecimal("1200.00")
        ));

        journalEntryRepository.save(new JournalEntry(
                "JE-2026-002",
                today.minusDays(1),
                JournalType.BANK,
                inv.getInvoiceNumber(),
                "Receipt for commercial beach event permit order",
                "1010",
                "4010",
                new BigDecimal("4500.00")
        ));
    }

    // Contacts
    @Transactional(readOnly = true)
    public List<Contact> getContacts() {
        return contactRepository.findAll();
    }

    @Transactional
    public Contact createContact(CreateContactRequest req) {
        if (contactRepository.existsByNameIgnoreCase(req.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Contact already exists");
        }
        return contactRepository.save(new Contact(req.name(), req.type(), req.email(), req.phone(), req.taxId()));
    }

    // Products & Accounts
    @Transactional(readOnly = true)
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    // Purchase Orders
    @Transactional(readOnly = true)
    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrderRepository.findAllByOrderByOrderDateDesc();
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(CreatePurchaseOrderRequest req) {
        Contact vendor = contactRepository.findById(req.vendorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));
        String poNumber = "PO-2026-" + seqCounter.incrementAndGet();
        return purchaseOrderRepository.save(new PurchaseOrder(
                poNumber, vendor, LocalDate.now(), OrderStatus.CONFIRMED, req.totalAmount(), req.description()
        ));
    }

    @Transactional
    public Invoice convertPoToBill(Long poId) {
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Order not found"));
        po.setStatus(OrderStatus.BILLED);
        purchaseOrderRepository.save(po);

        String billNumber = "BILL-2026-" + seqCounter.incrementAndGet();
        return invoiceRepository.save(new Invoice(
                billNumber,
                InvoiceType.VENDOR_BILL,
                po.getVendor(),
                po.getPoNumber(),
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                po.getTotalAmount()
        ));
    }

    // Sales Orders
    @Transactional(readOnly = true)
    public List<SalesOrder> getSalesOrders() {
        return salesOrderRepository.findAllByOrderByOrderDateDesc();
    }

    @Transactional
    public SalesOrder createSalesOrder(CreateSalesOrderRequest req) {
        Contact cust = contactRepository.findById(req.customerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        BeachSector sector = req.sectorId() != null ? beachSectorRepository.findById(req.sectorId()).orElse(null) : null;
        String soNumber = "SO-2026-" + seqCounter.incrementAndGet();
        return salesOrderRepository.save(new SalesOrder(
                soNumber, cust, sector, LocalDate.now(), OrderStatus.CONFIRMED, req.totalAmount(), req.description()
        ));
    }

    @Transactional
    public Invoice convertSalesOrderToInvoice(Long orderId) {
        SalesOrder so = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sales Order not found"));
        so.setStatus(OrderStatus.INVOICED);
        salesOrderRepository.save(so);

        String invNumber = "INV-2026-" + seqCounter.incrementAndGet();
        return invoiceRepository.save(new Invoice(
                invNumber,
                InvoiceType.CUSTOMER_INVOICE,
                so.getCustomer(),
                so.getOrderNumber(),
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                so.getTotalAmount()
        ));
    }

    // Invoices & Payments
    @Transactional(readOnly = true)
    public List<Invoice> getInvoices() {
        return invoiceRepository.findAllByOrderByInvoiceDateDesc();
    }

    @Transactional
    public Payment payInvoice(Long invoiceId, ProcessPaymentRequest req) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice is already paid");
        }

        BigDecimal payAmt = req.amount() != null ? req.amount() : invoice.getAmount();
        invoice.setAmountPaid(invoice.getAmountPaid().add(payAmt));
        if (invoice.getAmountPaid().compareTo(invoice.getAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        }
        invoiceRepository.save(invoice);

        PaymentMethod method = req.paymentMethod() != null ? req.paymentMethod() : PaymentMethod.BANK_TRANSFER;
        String payRef = "PAY-2026-" + seqCounter.incrementAndGet();
        Payment payment = paymentRepository.save(new Payment(
                payRef, invoice, LocalDate.now(), payAmt, method
        ));

        // Create Journal Entry balancing the ledger
        String debitAcc = invoice.getType() == InvoiceType.CUSTOMER_INVOICE ? "1010" : "2010";
        String creditAcc = invoice.getType() == InvoiceType.CUSTOMER_INVOICE ? "4010" : "1010";
        String desc = invoice.getType() == InvoiceType.CUSTOMER_INVOICE
                ? "Bank receipt for customer invoice " + invoice.getInvoiceNumber()
                : "Bank disbursement for vendor bill " + invoice.getInvoiceNumber();

        journalEntryRepository.save(new JournalEntry(
                "JE-2026-" + seqCounter.incrementAndGet(),
                LocalDate.now(),
                JournalType.BANK,
                invoice.getInvoiceNumber(),
                desc,
                debitAcc,
                creditAcc,
                payAmt
        ));

        return payment;
    }

    // Journal Entries
    @Transactional(readOnly = true)
    public List<JournalEntry> getJournalEntries() {
        return journalEntryRepository.findAllByOrderByEntryDateDesc();
    }

    // Financial Reports
    @Transactional(readOnly = true)
    public FinancialReportResponse getFinancialReport() {
        List<Account> accounts = accountRepository.findAll();
        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal totalLiabilities = BigDecimal.ZERO;
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        Map<String, BigDecimal> details = new HashMap<>();

        for (Account a : accounts) {
            details.put(a.getName(), a.getBalance());
            switch (a.getType()) {
                case ASSET -> totalAssets = totalAssets.add(a.getBalance());
                case LIABILITY -> totalLiabilities = totalLiabilities.add(a.getBalance());
                case INCOME -> totalIncome = totalIncome.add(a.getBalance());
                case EXPENSE -> totalExpenses = totalExpenses.add(a.getBalance());
            }
        }

        BigDecimal netWorth = totalAssets.subtract(totalLiabilities);
        BigDecimal netOperatingSurplus = totalIncome.subtract(totalExpenses);

        return new FinancialReportResponse(
                "Municipal Smart Public Beach Safety - Financial & Operating Statement",
                totalAssets,
                totalLiabilities,
                netWorth,
                totalIncome,
                totalExpenses,
                netOperatingSurplus,
                details
        );
    }
}
