// api.js

// ✅ Base URL for your backend API
const BASE_URL = 'http://localhost:8080/api'; // Change to your Render URL in production

// ✅ Get JWT token from localStorage
function getToken() {
  return localStorage.getItem('jwt_token');
}

// ✅ Create a centralized Axios instance
const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// ✅ Automatically attach Authorization header before each request
api.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ✅ Optional: Handle global response errors
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      alert("⚠️ Unauthorized. Please login again.");
      window.location.href = "login.html";
    } else if (error.response && error.response.status === 403) {
      alert("⛔ Forbidden. You don’t have permission.");
    } else {
      console.error("❌ API Error:", error);
    }
    return Promise.reject(error);
  }
);

// ✅ Reusable GET request
function apiGet(url) {
  return api.get(url).then(res => res.data);
}

// ✅ Reusable POST request with JSON body
function apiPost(url, data) {
  return api.post(url, data).then(res => res.data);
}

// ✅ Reusable POST request with file (multipart/form-data)
function apiUploadFile(url, formData) {
  return api.post(url, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then(res => res.data);
}

// ✅ Get Owners File names along with file IDs
function apiGetFileList(url) {
  return api.get(url).then(res => res.data);
}

//File Download 
async function apiDownloadFile(url) {
  return api.get(url, {
    responseType: 'blob'   // ✅ tells Axios to handle binary properly
  });
}


// ✅ Expose all functions for use in other files
export {
  apiGet,
  apiPost,
  apiUploadFile,
  getToken,
  apiGetFileList,
  apiDownloadFile
};
