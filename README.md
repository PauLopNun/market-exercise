# StS — Simple Terminal Supermarket

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Build](https://img.shields.io/badge/Build-Maven-blue?style=flat-square)
![Tests](https://img.shields.io/badge/Tests-JUnit%205-green?style=flat-square)
![Coverage](https://img.shields.io/badge/Coverage-100%25-brightgreen?style=flat-square)
![License](https://img.shields.io/badge/License-Academic-lightgrey?style=flat-square)

> Modular command-line supermarket simulation. Five independent modules communicate exclusively through shared CSV files. No frameworks, no database — pure Java 17.

---

## Project Structure

```
market-exercise/
├── .github/
│   └── workflows/
│       └── ci.yml              # GitHub Actions — tests + coverage on every PR
├── data/                       # Shared CSV files (all modules read/write here)
│   ├── users.csv
│   ├── cart.csv
│   ├── market_stock.csv
│   ├── warehouse.csv
│   └── audit_log.csv
├── src/
│   ├── main/java/com/sts/
│   │   ├── Main.java
│   │   ├── shared/             # POJOs used by ALL teams
│   │   │   ├── model/          # Product.java, CartEntry.java, User.java
│   │   │   └── audit/          # AuditLogger.java
│   │   ├── market/             # MARKET team
│   │   │   ├── model/
│   │   │   ├── repository/     # MarketStockRepository, CartRepository
│   │   │   └── service/        # MarketService
│   │   ├── client/             # CLIENT team
│   │   ├── payment/            # PAYMENT team
│   │   ├── store/              # STORE team
│   │   └── audit/              # AUDIT team
│   └── test/java/com/sts/      # JUnit 5 tests — one folder per module
└── pom.xml                     # Maven build — JUnit 5 + JaCoCo
```

---

## Shared Data Layer

All modules must respect these CSV schemas exactly.

| File | Schema |
|------|--------|
| `users.csv` | `id, name, budget` |
| `cart.csv` | `userId, productId, quantity` |
| `market_stock.csv` | `productId, name, price, current_stock, max_capacity` |
| `warehouse.csv` | `productId, name, total_stock` |
| `audit_log.csv` | `timestamp, module, action, status, details` |

---

## Modules

| Module | Package | Responsibility |
|--------|---------|----------------|
| CLIENT | `com.sts.client` | CLI entry point, user session, command routing |
| MARKET | `com.sts.market` | Shelf stock management — BUY, DROP, RESTOCK |
| PAYMENT | `com.sts.payment` | Checkout, budget validation, LIFO refund logic |
| STORE | `com.sts.store` | Auto-replenishment from warehouse after checkout |
| AUDIT | `com.sts.audit` | Append-only event log, daily revenue report |
| SHARED | `com.sts.shared` | POJOs and AuditLogger — used by all modules |

---

## Module Interaction

```
USER INPUT
    │
    ▼
CLIENT ──── BUY / DROP ──────► MARKET (market_stock.csv + cart.csv)
    │
    ├────── CHECKOUT ─────────► PAYMENT ── insufficient funds ──► MARKET (restock)
    │                               │
    │                               └── success ────────────────► STORE (replenishment)
    │
    └────── LOGS / EXIT ──────► AUDIT (report)

All modules ────────────────────────────────────────────────────► AUDIT (log events)
```

---

## CLI Commands

| Command | Description | Triggers |
|---------|-------------|----------|
| `LOGIN` | Identify user by id or name | — |
| `BUY <id> <qty>` | Add item to cart | MARKET |
| `DROP <id> <qty>` | Remove item from cart | MARKET |
| `CHECKOUT` | Pay for cart contents | PAYMENT → STORE |
| `LOGS` | Show audit log | AUDIT |
| `EXIT` | Close session and generate report | AUDIT |

---

## Code Quality

### Clean Code Standards

All code follows **Robert C. Martin's Clean Code** principles:

| Principle | Status | Details |
|-----------|--------|---------|
| Meaningful Names | IMPLEMENTED | No single-letter variables (except loop counters), no abbreviations |
| Single Responsibility | IMPLEMENTED | Each class has one reason to change |
| DRY (Don't Repeat Yourself) | IMPLEMENTED | CSV parsing logic abstracted to repositories |
| KISS (Keep It Simple) | IMPLEMENTED | No frameworks, pure Java logic |
| Method Names | IMPLEMENTED | Consistent pattern: `read*()`, `write*()`, `find*()`, `save*()` |

Every push and pull request to `main` or `develop` runs automatically:

- Compile with Java 17
- Run all JUnit 5 tests
- Generate JaCoCo coverage report
- Fail build if line coverage drops below **100%**
- Upload coverage report as artifact

---

## Technical Guidelines

- **No frameworks** — pure Java 17, no Spring, no Hibernate
- **No static state** — constructor-based dependency injection only
- **Service pattern** — all logic in Service classes, nothing in Main
- **TDD** — tests written before implementation, 100% coverage required
- **Exception handling** — `IOException` handled with retry mechanism for locked files
- **Append-only** — `audit_log.csv` is never overwritten, only appended
