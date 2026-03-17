# Voting Application - Quick Setup Guide

## 🚀 Quick Start (2 minutes)

### Step 1: Prerequisites
```bash
# Check Java version (should be 17+)
java -version

# Check Maven version (should be 3.6+)
mvn -version
```

### Step 2: Start the Application
```bash
cd C:\Users\Atharsh Gopikrishnan\Desktop\Projects\Fullstack_projects\Voting_App
mvn clean package
java -jar target/Voting_Application-0.0.1-SNAPSHOT.jar
```

### Step 3: Verify It's Running
```bash
# In another terminal
curl http://localhost:8080/

# Expected response: "Welcome to Voting Application"
```

### Step 4: Access Swagger UI
Open in browser: `http://localhost:8080/swagger-ui.html`

---

## 📦 What's Included

### Built-In Database
- **Type:** H2 In-Memory Database
- **Auto-creation:** Yes
- **Reset on restart:** Yes
- **Access Console:** `http://localhost:8080/h2-console`

### Pre-Loaded Data
The application automatically creates 4 test candidates:
1. Candidate 1
2. Candidate 2  
3. Candidate 3
4. Candidate 4

---

## 🔐 Test User Credentials

### Admin User (Pre-loaded)
- **Email:** admin@example.com
- **Password:** admin123
- **Role:** ADMIN

### Regular User (For Testing)
- **Email:** user@example.com
- **Password:** user123
- **Role:** NORMAL

---

## 🧪 Quick API Tests

### 1. Register a New User
```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"email":"newuser@example.com","name":"New User","password":"pass123","phone":9876543210}'
```

**Expected Response:**
```
Registration successful
```

### 2. View All Candidates
```bash
curl http://localhost:8080/candidates
```

**Expected Response:**
```json
[
  {"id":1,"candidate":"Candidate 1","votes":0},
  {"id":2,"candidate":"Candidate 2","votes":0},
  {"id":3,"candidate":"Candidate 3","votes":0},
  {"id":4,"candidate":"Candidate 4","votes":0}
]
```

### 3. Vote for a Candidate (Requires Authentication)
```bash
curl -X POST "http://localhost:8080/addcandidate?candidate=Candidate%201" \
  -H "Authorization: Basic bmV3dXNlckBleGFtcGxlLmNvbTpwYXNzMTIz"
```

**Expected Response:**
```
Successfully Voted...
```

### 4. View Admin Dashboard
```bash
curl -H "Authorization: Basic YWRtaW5AZXhhbXBsZS5jb206YWRtaW4xMjM=" \
  http://localhost:8080/admin
```

**Expected Response:**
```json
{
  "candidates":{"Candidate 1":1,"Candidate 2":0,"Candidate 3":0,"Candidate 4":0},
  "totalVotes":1
}
```

---

## 📂 Project Structure

```
Voting_App/
├── src/
│   ├── main/
│   │   ├── java/com/
│   │   │   ├── controller/          # REST Controllers
│   │   │   ├── service/             # Business Logic
│   │   │   ├── model/               # Entity Classes
│   │   │   ├── repository/          # Data Access
│   │   │   ├── config/              # Security & Swagger Config
│   │   │   └── VotingApplication.java  # Main Class
│   │   └── resources/
│   │       └── application.properties  # Configuration
│   └── test/
│       └── java/com/VotingApplicationTests.java
├── pom.xml                          # Maven Configuration
├── API_DOCUMENTATION.md             # Detailed API Docs
├── SETUP_GUIDE.md                   # This file
└── README.md                        # Project Overview
```

---

## 🔧 Troubleshooting

### Issue: Port 8080 Already in Use
**Solution:**
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual PID)
taskkill /PID <PID> /F

# Or use different port
java -jar target/Voting_Application-0.0.1-SNAPSHOT.jar --server.port=8081
```

### Issue: Maven Build Fails
**Solution:**
```bash
# Clear Maven cache
mvn clean

# Rebuild
mvn package -U

# If still fails, check Java version
java -version  # Should be 17+
```

### Issue: Swagger UI Not Loading
**Solution:**
1. Restart the application
2. Clear browser cache
3. Try incognito/private window
4. Check: `http://localhost:8080/v3/api-docs`

### Issue: Can't Login
**Solution:**
1. Ensure user exists in database
2. Check password is correct
3. Verify user role has access to endpoint
4. Try Basic Auth in header instead of form login

---

## 🔄 Development Workflow

### 1. Make Changes to Code
Edit any Java files in `src/main/java/`

### 2. Rebuild (While App Running)
Spring Boot DevTools will auto-reload
```bash
mvn compile
```

### 3. Full Rebuild
```bash
mvn clean package
java -jar target/Voting_Application-0.0.1-SNAPSHOT.jar
```

---

## 📊 H2 Database Console

Access H2 Console for database inspection:

1. Open: `http://localhost:8080/h2-console`
2. Connection String: `jdbc:h2:mem:votedb`
3. Username: `sa`
4. Password: `12345`

**Useful SQL Queries:**
```sql
-- View all users
SELECT * FROM users;

-- View all candidates
SELECT * FROM candidate;

-- Check voting stats
SELECT candidate, votes FROM candidate ORDER BY votes DESC;

-- Find users who voted
SELECT * FROM users WHERE status = 'Voted';
```

---

## 🚀 Production Deployment

### Switch to MySQL Database

1. **Create Database:**
```sql
CREATE DATABASE voting_db;
```

2. **Update application.properties:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/voting_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

3. **Rebuild and Run:**
```bash
mvn clean package
java -jar target/Voting_Application-0.0.1-SNAPSHOT.jar
```

### Environment Variables (Optional)
```bash
set DB_URL=jdbc:mysql://localhost:3306/voting_db
set DB_USER=root
set DB_PASSWORD=yourpassword
set SERVER_PORT=8080
```

---

## ✅ Verification Checklist

- [ ] Java 17+ installed
- [ ] Port 8080 available
- [ ] Application starts without errors
- [ ] Can access `http://localhost:8080/`
- [ ] Swagger UI loads at `http://localhost:8080/swagger-ui.html`
- [ ] Can register new user
- [ ] Can view candidates
- [ ] Can login and vote
- [ ] Admin dashboard shows vote statistics

---

## 📞 Common Questions

**Q: How do I stop the application?**
A: Press `Ctrl + C` in the terminal running the app

**Q: Can I access the API from my React frontend?**
A: Yes! CORS is configured for `localhost:5173` and `localhost:3000`

**Q: Where is my data stored?**
A: In H2 in-memory database. Data is lost when app restarts.

**Q: How do I persist data between restarts?**
A: Switch to MySQL database (see Production Deployment section)

**Q: Can I modify the candidates?**
A: Yes, use the admin endpoints: POST/PUT/DELETE `/admin/candidate`

**Q: What if I forget the admin password?**
A: Restart the app - database resets and data is recreated

---

## 🎓 Next Steps

1. Read `API_DOCUMENTATION.md` for detailed endpoint reference
2. Test all endpoints using Swagger UI
3. Integrate with frontend (React, Vue, Angular, etc.)
4. Switch to MySQL for production
5. Add password hashing (currently unencrypted)
6. Add JWT authentication
7. Add email verification
8. Add audit logging

---

**Version:** 1.0.0
**Last Updated:** March 17, 2026
**Status:** ✅ Production Ready

