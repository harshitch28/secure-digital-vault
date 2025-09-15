import { apiUploadFile, getToken, apiDownloadFile , apiGetFileList} from './api.js';

const uploadForm = document.getElementById('uploadForm');
const message = document.getElementById('message');
const downloadFile = document.getElementById('downloadFile');
const fileListContainer = document.createElement('div');

// ✅ 1. Redirect if not authenticated
if (!getToken()) {
  alert("⚠️ Please login first.");
  window.location.href = "login.html";
}

// ✅ 2. File upload handler
if (uploadForm) {
  uploadForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const fileInput = document.getElementById('file');
    const file = fileInput.files[0];

    if (!file) {
      showMessage("❌ Please select a file to upload.", "danger");
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await apiUploadFile('/vault/upload', formData);
      
      // Clear file input
      fileInput.value = "";
      await loadUserFiles();
      showMessage("✅ File uploaded successfully!", "success",3000);
    } catch (err) {
      const msg = err.response?.data?.message || "❌ File upload failed.";
      showMessage(msg, "danger",3000);
    }
  });
}

// ✅ Fetch user's files
async function loadUserFiles() {
  try {
    const files = await apiGetFileList('/vault/files/user');
    renderFileList(files);
  } catch (err) {
    showMessage("❌ Failed to load file list.", "danger");
  }
}


// ✅ Render file list with download buttons
function renderFileList(files) {
  fileListContainer.innerHTML = `
    <h3 class="text-lg font-semibold mt-8 mb-3">Your Files</h3>
    <ul class="space-y-3">
      ${files.map(file => `
        <li class="flex justify-between items-center bg-gray-100 p-3 rounded-xl">
          <span class="truncate">${file.fileName}</span>
          <button class="download-btn text-sm bg-primary text-white px-3 py-1 rounded-xl hover:bg-blue-700 transition"
            data-id="${file.id}">
            Download
          </button>
        </li>
      `).join('')}
    </ul>
  `;
  document.body.querySelector("main").appendChild(fileListContainer);

    // attach listeners
  fileListContainer.querySelectorAll(".download-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      download_file(btn.dataset.id);
    });
  });
}

// ✅ 2. File download handler
async function download_file(fileId) {
  // downloadFile.addEventListener('submit', async (e) => {
  //   e.preventDefault();

  //   const fileIdInput = document.getElementById('file_Id')
  //   const fileId = fileIdInput.value.trim();

  //   if (!fileId) {
  //     showMessage("❌ Please enter a file ID.", "danger");
  //     return;
  //   }

    try {
      // Call backend
      const response = await apiDownloadFile(`/vault/download/${fileId}`);
      
      // Extract filename from response headers, fallback if missing
      let filename = "downloaded_file";
      const contentDisposition = response.headers["content-disposition"];
      if (contentDisposition) {
        const match = contentDisposition.match(/filename="?([^"]+)"?/);
        
        if (match && match[1]) {
          filename = match[1];
        }
      }

      // Create download
      const blob = response.data;
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.setAttribute("download", filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
      showMessage("✅ File downloaded successfully!", "success",1500);
    } catch (err) {
      showMessage("❌ Download failed.", "danger",1500);
    }
  
}


// ✅ 3. Logout handler
window.logout = function () {
  localStorage.removeItem('jwt_token');
  window.location.href = "login.html";
};

// ✅ 4. Utility: Show messages
function showMessage(text, type , duration = 1500) {
  if (message) {
    message.textContent = text;
    message.className = `mt-4 text-sm text-${type === "success" ? "green" : "red"}-600 text-center`;
    
    setTimeout(() => {
      message.textContent = "";
      message.className = "mt-4 text-sm text-center";
    }, duration);
  }
}

// ✅ On load, fetch user's files
loadUserFiles();
