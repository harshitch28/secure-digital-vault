// auth.js
import { apiPost } from './api.js';

const registerForm = document.getElementById('registerForm');
const loginForm = document.getElementById('loginForm');
const message = document.getElementById('message');

// ✅ Register Handler
if (registerForm) {
  registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();

    if (!username || !email || !password) {
      showMessage("All fields are required.", "danger");
      return;
    }

    try {
      await apiPost('/auth/register', { username, email, password });
      showMessage("✅ Registration successful! Redirecting to login...", "success");
      setTimeout(() => {
        window.location.href = "login.html";
      }, 200);
    } catch (err) {
      const msg = err.response?.data?.message || "Registration failed.";
      showMessage(`❌ ${msg}`, "danger");
    }
  });
}

// ✅ Login Handler
if (loginForm) {
  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();

    if (!username || !password) {
      showMessage("Please enter both fields.", "danger");
      return;
    }

    try {
      const data = await apiPost('/auth/login', { username, password });

      // Save JWT token
      localStorage.setItem('jwt_token', data.token);

      showMessage("✅ Login successful! Redirecting...", "success");
      setTimeout(() => {
        window.location.href = "dashboard.html";
      }, 150);
    } catch (err) {
      const msg = err.response?.data?.message || "Login failed.";
      showMessage(`❌ ${msg}`, "danger");
    }
  });
}

// ✅ Common Message UI
function showMessage(text, type = "danger") {
  if (message) {
    message.textContent = text;
    message.className = `mt-4 text-sm text-${type === "success" ? "green" : "red"}-600 text-center`;
  }
}
