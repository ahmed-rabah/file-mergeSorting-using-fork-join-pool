# 📂 CSV Merge Sort Web Application
A web application developed with **Spring Boot**, **Thymeleaf**, and **OpenCSV**, enabling **sequential** or **concurrent** sorting of CSV files, with customizable column-based sorting support.

---

## 🚀 Project Objective

The main goal is to provide a simple interface to:

- Upload a CSV file,
- Select the sorting column and data type (text or numeric),
- Choose between two sorting methods:
    - **Sequential sorting** using `Collections.sort`
    - **Concurrent/parallel sorting** with **Merge Sort** based on **ForkJoinPool**
- Download the sorted file.

This project implements Java concepts of **parallelism** and **concurrency** to handle large datasets efficiently.

---

## 💠 Technologies Used

| Tool / Library       | Purpose                   |
| -------------------- | ------------------------- |
| **Spring Boot**      | Main framework (web, DI)  |
| **Spring MVC**       | HTTP Controllers          |
| **Thymeleaf**        | HTML template engine      |
| **OpenCSV**          | CSV file reading/writing  |
| **ForkJoinPool**     | Concurrent sort execution |
| **Collections.sort** | Simple, sequential sort   |

---

## 🔄 Features

- 📤 CSV file upload
- 📋 Automatic column extraction
- 🕽 Sorting by any column
- 🔢 Numeric or alphabetical sort
- 🧵 Sequential sort (single-threaded)
- 🚀 Parallel sort (multi-threaded with Fork/Join)
- ⏱ Performance measurement shown in logs (not added)
- ⚠ Display of ignored lines if they are corrupted or incomplete (not added)

---

## 🔍 Technical Aspects

### ✅ Sequential Sort

Uses simply:

```java
Collections.sort(rowList);
```

It's simple and readable, but **not optimal for large files**.

---

### 🧵 Parallel Sort (Concurrent Merge Sort)

Implemented via a `RecursiveTask` with Java 8+ **Fork/Join** framework:

```java
ForkJoinPool pool = new ForkJoinPool();
pool.invoke(new MergeSortTask(rowList));
```

This sort:

- Recursively splits the list into sublists,
- Sorts each part in parallel,
- Merges the sorted results.

✅ **Advantage**: better performance on large files\
⚠ **Limitation**: depends on the number of processors (`Runtime.getRuntime().availableProcessors()`)

---

## 📸 Quick Demo

1. 📤 Upload a CSV file
2. ✅ Select column and type (String / Number)
3. 🚦 Choose "Sequential sort" or "Parallel sort"
4. 📅 Download the sorted file
5. 🔎 View ignored lines (corrupted format, missing columns) (not added)

---

## 📁 Project Structure

```
├── com.azer.csvmergesort
│   ├── controller       → Web controllers
│   ├── model            → CsvRow model
│   ├── service          → CSV reading/sorting/writing
│   ├── task             → MergeSortTask (ForkJoin)
│   └── templates        → Thymeleaf HTML templates
```

---

## ⚠️ Known Limitations

- CSV files must have a header row.
- Lines with an incorrect number of columns are ignored.
- Automatic detection of data type (number vs text) is not yet implemented.
- Parallel sorting may use significant memory for very large files.

---

## 📊 Future Improvements

- 📊 Auto-detect data type for each column
- 🔐 More robust file validation
- ⌛ Display processing time to the user
- 🥪 Unit / integration tests
- 📉 Statistics visualization (histograms, etc.)

---

## 🤝 Contributors

- Zakariaa El jabiry
- Ahmed Rabah

