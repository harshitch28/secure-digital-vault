import { apiUploadFile, getToken } from './api.js';

const uploadForm = document.getElementById('uploadForm');
const message = document.getElementById('message');
const fileListContainer = document.createElement('div');
fileCardContainer.id = 'fileCardContainer';
fileCardContainer.className = 'mt-10 grid grid-cols-1 sm:grid-cols-2 gap-4';

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
      const response = await apiUploadFile('/files/upload', formData);
      showMessage("✅ File uploaded successfully!", "success");

      // Clear file input
      fileInput.value = "";
      await loadUserFiles();
    } catch (err) {
      const msg = err.response?.data?.message || "❌ File upload failed.";
      showMessage(msg, "danger");
    }
  });
}

// ✅ Fetch user's files
async function loadUserFiles() {
  try {
    const files = await apiGet('/files/user');
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
          <span class="truncate">${file.originalFilename}</span>
          <button class="text-sm bg-primary text-white px-3 py-1 rounded-xl hover:bg-blue-700 transition"
            onclick="downloadFile('${file.id}', '${file.originalFilename}')">
            Download
          </button>
        </li>
      `).join('')}
    </ul>
  `;
  document.body.querySelector("main").appendChild(fileListContainer);
}

// ✅ Download handler
window.downloadFile = async function (fileId, filename) {
  try {
    const response = await apiDownloadFile(`/files/download/${fileId}`);
    const blob = new Blob([response.data]);
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = filename;
    link.click();
  } catch (err) {
    showMessage("❌ Download failed.", "danger");
  }
}


// ✅ 3. Logout handler
window.logout = function () {
  localStorage.removeItem('jwt_token');
  window.location.href = "login.html";
};

// ✅ 4. Utility: Show messages
function showMessage(text, type = "danger") {
  if (message) {
    message.textContent = text;
    message.className = `mt-4 text-sm text-${type === "success" ? "green" : "red"}-600 text-center`;
  }
}
// ✅ On load, fetch user's files
loadUserFiles();
