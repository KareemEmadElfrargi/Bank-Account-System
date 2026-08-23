# Lab: Bank Account System (Capstone)

`java` `project` `lab` `exceptions` `maven` `solid` `hashmap` `collections`

This is a lab brief, not a tutorial — and it's the capstone of the whole Java in Practice path. It pulls together everything from this second half of the course: a checked exception you design yourself, an unchecked exception a manager class throws, SOLID design shown through an abstract class hierarchy, a HashMap and LinkedList doing real work, a real Maven build, and SLF4J/Logback logging instead of `System.out.println`. This brief describes what to build and why — the exact field names, method signatures, and message wording are yours to design. Study the class diagram, then build to it.

**1.5 hours · capstone**

This lab is deliberately smaller and less prescriptive than the Hotel Management final project — a handful of classes, one exception-heavy manager class, and real design decisions left to you.

**Prerequisites:** checked vs. unchecked exceptions, HashMap, the Set/LinkedList article, SOLID, a Maven project you can build, and SLF4J/Logback logging — all from earlier in this path. No Scanner — drive everything from a hardcoded `Main`.

## By the end you will be able to...

- Design a checked exception with a real `throws` declaration the caller is forced to handle.
- Let an unchecked exception surface a programmer error without cluttering every method signature.
- Show Open/Closed and Single Responsibility in practice through an abstract class hierarchy that a manager class never needs to modify.
- Combine HashMap (lookup by account number) and LinkedList (an append-only transaction log) — each chosen because it's the right structure for the job.
- Wire a real Maven project with a `pom.xml` and get SLF4J/Logback writing real log output.
- Organize code into Java packages (models, exceptions, management).

## Concepts exercised

Checked vs. unchecked exceptions · custom exception classes · `throws` propagation · abstract classes · Open/Closed & Single Responsibility · HashMap · LinkedList · encapsulation & validation · Maven & `pom.xml` · SLF4J/Logback logging · packages.

## The class diagram

Study this before writing anything — it shows the shape of the system, not the implementation. Solid line, hollow triangle = extends. Filled diamond = composition (owns / manages). Open arrow = association (uses / throws).

```
«abstract» Account
- accountNumber, balance
- owner: Customer
+ deposit(amount)
+ withdraw(amount) throws
  your checked exception
+ getAccountType() «abstract»

SavingsAccount
- interestRate
→ "Savings Account"

CheckingAccount
- overdraftAllowance
→ "Checking Account"

Customer
- name, id, email
+ getters, toString()
  owner

com.bank.exceptions
your checked exception
  extends Exception
your unchecked exception
  extends RuntimeException

Bank (com.bank.management)
- accounts: HashMap<String, Account>
- transactionHistory: LinkedList<String>
- logger: an SLF4J Logger
+ openAccount / getAccount
+ deposit / withdraw / getTransactionHistory
  manages accounts

packages: models = Account hierarchy + Customer · exceptions = your two custom types · management = Bank · com.bank = Main
```

Account is abstract with one abstract method; `SavingsAccount` and `CheckingAccount` override it — that's what lets `Bank` stay closed to modification as account types grow. `withdraw()` can throw your checked exception; `Bank.getAccount()` can throw your unchecked one.

## Package & file layout

```
src/main/java/com/bank/
  models/      Account.java  SavingsAccount.java  CheckingAccount.java  Customer.java
  exceptions/  (your checked exception).java  (your unchecked exception).java
  management/  Bank.java
  Main.java
src/main/resources/logback.xml
pom.xml
```

## Full requirements

These are goals to design to, not signatures to copy. Field names, method names, and exact wording are yours — what matters is that the behavior below is genuinely there.

### Account (abstract, `com.bank.models`)

Private fields: an account number, a balance, and an owning `Customer`. Constructor validates the account number is non-empty and the initial balance isn't negative. Methods:

- One abstract method that subclasses must implement (e.g. something that describes the account type).
- A deposit method — reject a zero or negative amount sensibly; don't let the balance change if you do.
- A withdraw method that declares a checked exception — if the amount requested exceeds the balance, throw it; otherwise subtract and update the balance. Every caller of this method must catch the exception or declare it themselves — that's the whole point of making it checked.
- Getters for balance, account number, and owner.

### SavingsAccount / CheckingAccount

Each extends `Account`, takes `(accountNumber, balance, owner)`. `SavingsAccount` also stores an interest rate (e.g. 2.5) and overrides `getAccountType()` → `"Savings Account"`. `CheckingAccount` stores a small overdraft allowance concept and overrides `getAccountType()` → `"Checking Account"`. Neither overrides `withdraw()` — the shared, exception-throwing implementation in `Account` is enough for this lab.

### Customer (`com.bank.models`)

Private name, id, email. Constructor validates name non-empty, email contains `@`. Getters + a `toString()` like `"Jane Doe (C-002, jane@email.com)"`.

### Your checked exception (`com.bank.exceptions`)

Extends `Exception`. Give it a constructor that takes a message and passes it to `super(message)`. Because it extends `Exception` and not `RuntimeException`, every method on the call path that can throw it must declare it, and `Main` must catch it. This is the same pattern the exception handling article introduced — here it's a real, load-bearing part of the design instead of a standalone example. (A name like `InsufficientFundsException` fits naturally, but the exact name and message are your call.)

### Your unchecked exception (`com.bank.exceptions`)

Extends `RuntimeException`. Same constructor pattern. Thrown by whatever method looks up an account when the account number doesn't resolve to anything. No `throws` declaration is required anywhere — that's the point of it being unchecked — but `Main` should still catch it deliberately so the demo doesn't crash.

### Bank (`com.bank.management`)

Private fields: a `HashMap<String, Account>` keyed by account number, a `LinkedList<String>` holding an append-only list of human-readable transaction descriptions, and a plain SLF4J `Logger` field (no need to wrap it behind your own interface for the core requirement — see the bonus challenges if you want to take that further).

- A way to open a new account — decide sensibly what should happen if the account number is already in use.
- A lookup method that throws your unchecked exception when the account number doesn't resolve to anything — don't return `null` here. That's deliberately different from the Hotel lab's `Hotel.getRoom`, which did return `null`. This is your clean unchecked-exception teaching moment: every caller either lets the exception propagate or catches it, instead of remembering to null-check.
- Deposit and withdraw methods that go through the lookup above (propagating the unchecked exception if the account is missing) and delegate to the Account's own deposit/withdraw — propagating your checked exception on withdrawal.
- Something that returns the transaction history for `Main` to print.

Why a `LinkedList` for the transaction history and not an `ArrayList`? You only ever append at the tail and never index into the middle — exactly what a `LinkedList` is built for. It's also a natural fit if this bank ever needs to trim old history from the front (say, keep only the last 90 days): removing from the head of a `LinkedList` is O(1), while an `ArrayList` would have to shift every remaining element down.

### Main (hardcoded, no Scanner)

Create a `Bank`. Open at least one `SavingsAccount` and one `CheckingAccount`. Do at least one valid deposit and one valid withdrawal. Then, in separate try/catch blocks: attempt a withdrawal larger than an account's balance and catch your checked exception; look up an account number that doesn't exist and catch your unchecked exception. Print a friendly one-line message from each catch — never let the demo crash. Finally, print the transaction history.

## SOLID checklist

## Scenarios your program must handle correctly

Exact wording is your choice — these are the situations that must not silently misbehave:

- A deposit of zero or a negative amount should be rejected sensibly, not silently accepted.
- Withdrawing more than an account's balance must throw your checked exception, not just print a warning and continue.
- Looking up an account number that doesn't exist must throw your unchecked exception, not return `null`.
- Opening an account with a number that's already in use shouldn't silently overwrite the existing account.
- An initial balance that's negative should be rejected by the constructor.

## Maven project setup

Here's a working `pom.xml` for this project — same shape as any Maven project, with SLF4J and Logback added so `mvn clean install` works from the moment you create it:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">

  <modelVersion>4.0.0</modelVersion>

  <groupId>com.bank</groupId>
  <artifactId>bank-account-system</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <dependencies>

    <!-- Logging facade: the API your code calls -->
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>2.0.13</version>
    </dependency>

    <!-- Logging implementation: does the actual writing -->
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.5.6</version>
    </dependency>

  </dependencies>

</project>
```

Classes live in `src/main/java` following the package layout above. Drop a minimal `logback.xml` in `src/main/resources` — the same one from the Logging article works unchanged, writing your logger's output to the console with a timestamp.

## What your output might look like

This isn't a contract to match exactly — exact wording, formatting, and ordering are your design choices. But something in this spirit should come out of your `Main`:

```
Opened Savings Account 100 for John Smith ($1000.00)
Deposited $250.00 into account 100. New balance: $1250.00
Withdrawal failed: not enough funds in account 200
Lookup failed: account 999 not found
--- Transaction history ---
1. Deposited $250.00 into account 100
```

## Bonus challenges

- **Account freezing + transfer** — add a `Set<String>` of frozen account numbers and a `transfer(from, to, amount)` method that checks it before moving any money (the frozen check has to happen before either account is touched, so a frozen destination blocks the whole transfer, not just half of it). Ties back to the Set article.
- **A logging interface** — wrap your SLF4J logger behind your own one-method interface (e.g. a `log(String message)` contract), so `Bank` depends on the interface, never the concrete SLF4J-backed class directly. That's Dependency Inversion, and it's exactly the pattern the Logging article discusses.
- **Interest strategy** — a small interface with one method, e.g. `double calculateInterest(double balance)`, and a couple of implementations `SavingsAccount` can be handed at construction time. Ties directly back to the Strategy pattern in Design Patterns.
- **A JUnit test for `withdraw()`** — add JUnit under Maven's test scope and write one test that asserts a too-large withdrawal throws your checked exception, and one that asserts a normal withdrawal updates the balance correctly. See Maven: Build Tool for Java Projects for how the test scope keeps testing dependencies out of your shipped JAR.
- **CSV export** — a method that writes the transaction history out to a `transactions.csv` file, one line per entry.
