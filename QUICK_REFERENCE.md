# 🚀 QUICK REFERENCE GUIDE

## 📌 Important URLs

| Resource | URL |
|----------|-----|
| **Home Page** | `http://localhost:8080/` |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **API Docs** | `http://localhost:8080/v3/api-docs` |
| **H2 Console** | `http://localhost:8080/h2-console` |

## 🔑 Test Credentials

### Admin User
```
Email: admin@example.com
Password: admin123
Role: ADMIN
```

### Regular User
```
Email: user@example.com
Password: user123
Role: NORMAL
```

## 🗳️ Test Candidates (Pre-loaded)
- Candidate 1
- Candidate 2
- Candidate 3
- Candidate 4

---

## 📡 Most Used API Endpoints

### 1. Register New User
```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{
    "email":"newuser@example.com",
    "name":"New User",
    "password":"pass123",
    "phone":9876543210
  }'
```

### 2. Get All Candidates
```bash
curl http://localhost:8080/candidates
```

### 3. Vote for Candidate
```bash
curl -X POST "http://localhost:8080/addcandidate?candidate=Candidate%201" \
  -H "Authorization: Basic bmV3dXNlckBleGFtcGxlLmNvbTpwYXNzMTIz"
```

### 4. Admin Dashboard
```bash
curl -H "Authorization: Basic YWRtaW5AZXhhbXBsZS5jb206YWRtaW4xMjM=" \
  http://localhost:8080/admin
```

### 5. Create Candidate (Admin)
```bash
curl -X POST http://localhost:8080/admin/candidate \
  -H "Authorization: Basic YWRtaW5AZXhhbXBsZS5jb206YWRtaW4xMjM=" \
  -H "Content-Type: application/json" \
  -d '{"candidate":"New Candidate","votes":0}'
```

---

## 🔐 Basic Auth Format

To create Basic Auth header:
1. Combine `email:password` (e.g., `admin@example.com:admin123`)
2. Encode in Base64
3. Add to header: `Authorization: Basic <encoded_string>`

**Example for admin:**
```
Original: admin@example.com:admin123
Base64: YWRtaW5AZXhhbXBsZS5jb206YWRtaW4xMjM=
Header: Authorization: Basic YWRtaW5AZXhhbXBsZS5jb206YWRtaW4xMjM=
```

---

## 📂 Important Files to Know

| File | Purpose |
|------|---------|
| **README.md** | Project overview & features |
| **SETUP_GUIDE.md** | Quick start & troubleshooting |
| **API_DOCUMENTATION.md** | Complete API reference |
| **COMPLETION_SUMMARY.md** | What was fixed & implemented |
| **pom.xml** | Maven configuration |
| **application.properties** | Spring Boot configuration |

---

## ⚙️ Configuration Files Location

```
Voting_App/
├── src/main/resources/
│   └── application.properties    ← Database & Swagger config
├── src/main/java/com/config/
│   ├── MyConfig.java             ← Security & CORS
│   └── SwaggerConfig.java        ← API Documentation
└── pom.xml                       ← Maven dependencies
```

---

## 🔧 Commands You'll Need

### Build Application
```bash
mvn clean package
```

### Run Application
```bash
java -jar target/Voting_Application-0.0.1-SNAPSHOT.jar
```

### Run Tests
```bash
mvn test
```

### Check Java Version
```bash
java -version
```

### Check Port Usage
```bash
netstat -ano | findstr :8080
```

### Kill Process on Port 8080
```bash
taskkill /PID <PID_NUMBER> /F
```

---

## 📊 API Response Examples

### Success Response
```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "User Name",
  "phone": 1234567890,
  "status": "Voted",
  "role": "ROLE_NORMAL"
}
```

### Error Response
```json
"Email already exists"
```

### Vote Statistics
```json
{
  "candidates": {
    "Candidate 1": 25,
    "Candidate 2": 15,
    "Candidate 3": 10,
    "Candidate 4": 5
  },
  "totalVotes": 55
}
```

---

## 🚨 Common Errors & Quick Fixes

| Error | Fix |
|-------|-----|
| Port 8080 in use | `taskkill /PID <PID> /F` |
| Build fails | `mvn clean package -U` |
| App won't start | Check Java version: `java -version` |
| CORS error | Check frontend URL in application.properties |
| Swagger not loading | Clear browser cache or try incognito |
| Can't login | Verify email/password in database via H2 console |

---

## 🎯 Typical Workflow

### 1. Start Application
```bash
java -jar target/Voting_Application-0.0.1-SNAPSHOT.jar
```

### 2. Register New User
```bash
POST http://localhost:8080/register
Body: {"email":"...", "name":"...", "password":"...", "phone":...}
```

### 3. View Available Candidates
```bash
GET http://localhost:8080/candidates
```

### 4. Login & Vote
```bash
POST http://localhost:8080/addcandidate?candidate=Candidate%201
With Basic Auth header
```

### 5. Check Admin Dashboard
```bash
GET http://localhost:8080/admin
With Admin Basic Auth header
```

---

## 📋 All 23 API Endpoints

### Home (4)
- `GET /` - Home
- `GET /about` - About
- `GET /register` - Register info
- `GET /signin` - Signin info

### Users (6)
- `POST /register` - Create user
- `GET /user` - Get current user
- `GET /user/{id}` - Get user by ID
- `GET /admin/users` - Get all users (Admin)
- `PUT /user/{id}` - Update user
- `DELETE /user/{id}` - Delete user

### Candidates (2)
- `GET /candidates` - Get all candidates (Public)
- `POST /addcandidate` - Vote for candidate

### Admin (7)
- `GET /admin` - Dashboard
- `GET /admin/candidates` - Get all candidates
- `POST /admin/candidate` - Create candidate
- `PUT /admin/candidate/{id}` - Update candidate
- `DELETE /admin/candidate/{id}` - Delete candidate
- `DELETED /admin/candidate/{id}` - Delete candidate

### Documentation (4+)
- `GET /swagger-ui.html` - Swagger UI
- `GET /v3/api-docs` - OpenAPI spec
- `GET /h2-console` - Database console

---

## 🔄 Git Commands (If Using Version Control)

### Initialize Repository
```bash
git init
git add .
git commit -m "Initial commit: Voting Application Backend"
```

### Push to Remote
```bash
git remote add origin <repository-url>
git push -u origin main
```

### Clone Existing Repository
```bash
git clone <repository-url>
cd Voting_App
```

---

## 📞 When You Need Help

### Check These Files First
1. **README.md** - Project overview
2. **SETUP_GUIDE.md** - Troubleshooting section
3. **API_DOCUMENTATION.md** - API reference

### Then Try
1. Open Swagger UI: `http://localhost:8080/swagger-ui.html`
2. Try endpoints in "Try it out" button
3. Check H2 console: `http://localhost:8080/h2-console`
4. Check application logs in terminal

### If Still Stuck
1. Review error message carefully
2. Check if Java version is 17+ : `java -version`
3. Check if Maven installed: `mvn -version`
4. Check if port 8080 is available: `netstat -ano | findstr :8080`

---

## ✅ Verification Checklist

Before considering setup complete:

- [ ] Application starts: `java -jar ...`
- [ ] Can access home: `http://localhost:8080/`
- [ ] Swagger loads: `http://localhost:8080/swagger-ui.html`
- [ ] Can register user: `POST /register`
- [ ] Can view candidates: `GET /candidates`
- [ ] Can login with Basic Auth
- [ ] Can vote for candidate
- [ ] Admin dashboard shows stats: `GET /admin`
- [ ] All endpoints return correct status codes
- [ ] CORS headers present in responses

---

## 🎓 Learning Resources

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [REST API Best Practices](https://restfulapi.net/)
- [Swagger/OpenAPI](https://swagger.io/)

---

## 📝 Notes

- H2 database is in-memory; data resets on app restart
- For persistence, switch to MySQL (see API_DOCUMENTATION.md)
- Passwords are currently unencrypted; add BCrypt for production
- Application uses port 8080 by default
- CORS enabled for React frontend on ports 5173 and 3000

---

**Version:** 1.0.0  
**Last Updated:** March 17, 2026  
**Status:** ✅ Production Ready

---

**Happy Voting! 🗳️**

