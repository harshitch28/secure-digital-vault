<----------- Version 1.0  -----------> 

<---- BACKEND -----> 

Secure Digital Vault & Password Manager – Backend (v1.0)

A secure, scalable backend system built with **Java + Spring Boot**, allowing users to **upload, encrypt, and retrieve personal files**, with full **JWT-based authentication**, **role-based access control**, and **admin tools**.

# 📌 Table of Contents

* [Tech Stack](#-tech-stack)
* [Architecture Overview](#-architecture-overview)
* [Features – v1.0](#-features--v10)
* [Project Structure](#-project-structure)
* [How It Works](#-how-it-works)
* [Setup & Run Instructions](#-setup--run-instructions)
* [Postman Collection](#-postman-collection)
* [Future Scope](#-future-scope)


# 🚀 Tech Stack

* **Java 17**
* **Spring Boot 3.x**
* **Spring Security**
* **JWT Authentication**
* **MySQL / PostgreSQL**
* **Maven**


### 🧠 Architecture Overview

```
                    +-------------------+
  [User / Admin] →  |  Spring Boot API  | ← Secure REST APIs
                    +-------------------+
                            |
       +--------------------+----------------------+
       |                                           |
 [JWT Auth + RBAC]                        [AES File Encryption]
       |                                           |
  [User Details + Roles]                    [VaultFile Entity]
       |                                           |
  [MySQL Database]                          [Encrypted File Storage]
```

### ✅ Features – v1.0

* 🔐 **User Registration & Login (JWT-based)**
* 🧑‍🤝‍🧑 **Role-Based Access Control (RBAC)** – Admin/User
* 📁 **Secure File Upload & Download (AES encrypted)**
* 🔒 **Access protected via JWT + Spring Security**
* 🧑‍💼 **Admin-only dashboard to manage users/files**

---

### 📁 Project Structure

```
src/
├── controller/           # API endpoints
├── entity/               # JPA Entities (User, Role, VaultFile)
├── repository/           # Spring Data JPA interfaces
├── security/             # JWT filters and utility classes
├── service/              # Business logic layer
├── config/               # Spring Security config
└── util/                 # AES Encryption helpers
```

---

### ⚙️ How It Works

#### 1. 🔐 Authentication & Authorization

* Users register via `/auth/register`
* Login at `/auth/login` returns a JWT
* JWT is validated in a custom `JwtAuthenticationFilter`
* Roles (`ROLE_USER`, `ROLE_ADMIN`) determine endpoint access
* Endpoints are protected using `@PreAuthorize`

#### 2. 📁 Encrypted File Upload

* Users upload a file to `/files/upload`
* File is encrypted using **AES** and saved to disk
* Metadata is stored in `VaultFile` table (name, user, timestamp, path)

#### 3. 📥 Secure Download

* File is decrypted on-the-fly via `/files/download/{id}`
* Only the **owner** or **admin** can download it

#### 4. 🧑‍💼 Admin Panel (API)

* Admin can view:

  * All users: `/admin/all-users`
  * All files: `/admin/all-files`
  * Delete users: `/admin/delete-user/{id}`

---

### 🧪 Setup & Run Instructions

#### ✅ Prerequisites

* Java 17+
* MySQL or PostgreSQL
* Maven
* Postman (for testing)

#### ⚙️ Run the Project

```bash
git clone https://github.com/yourusername/secure-digital-vault.git
cd secure-digital-vault
```

1. Update `application.properties` with your DB credentials
2. Run the app:

```bash
mvn spring-boot:run
```

3. Backend will be available at `http://localhost:8080/api`

### 🧪 Postman Collection

A Postman collection is available to test all endpoints:

* 🔐 Register/Login
* 📁 Upload/Download Files
* 🧑‍💼 Admin APIs

📂 Import from: [`postman/SecureVault-v1.0.postman_collection.json`](#) (add link once file is committed)

Set the following environment variables:

```env
base_url = http://localhost:8080/api
jwt_token = <paste your JWT here>
```

### 📈 Future Scope

Planned in upcoming versions:

* 🔑 Password Manager Module
* 🔗 One-Time Shareable Links (Zero Trust Model)
* 📜 Audit Logging (File access & User activity)
* ☁️ AWS S3 Integration for encrypted storage
* 🛡️ OAuth2-based login

### 📄 License

This project is licensed under the MIT License.
Feel free to fork, modify, and contribute.

### ✨ Author

Built with ❤️ by **[Harshit Chauhan](https://github.com/yourusername)**
For contributions, suggestions, or questions — open an issue!

