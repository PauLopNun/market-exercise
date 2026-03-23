# StS — Simple Terminal Supermarket
Pure Java 17 · No frameworks · CSV persistence

## Structure
```
src/
└── com/sts/
    ├── shared/         ← POJOs and AuditLogger (ALL teams use this)
    │   ├── model/
    │   └── audit/
    ├── market/         ← MARKET team
    │   ├── model/
    │   ├── repository/
    │   └── service/
    ├── client/         ← CLIENT team
    ├── payment/        ← PAYMENT team
    ├── store/          ← STORE team
    └── audit/          ← AUDIT team
data/                   ← shared CSV files
```

## Teams
| Module   | Package             |
|----------|---------------------|
| MARKET   | com.sts.market      |
| CLIENT   | com.sts.client      |
| PAYMENT  | com.sts.payment     |
| STORE    | com.sts.store       |
| AUDIT    | com.sts.audit       |
| SHARED   | com.sts.shared      |
