// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */


window.onload = init();

let userLoggedIn = false;

function init() {
  authentication();
  fetchBlobstoreUrlAndShowForm();
}

// Prevents HTML injection by removing markup from code
function sanitizeHTML(str) {
  const temp = document.createElement('div');
  temp.textContent = str;
  return temp.innerHTML;
}

// Gets URL for uploaded image
function fetchBlobstoreUrlAndShowForm() {
  fetch('/blobstore-handler')
    .then((response) => {
    return response.text();
    })
    .then((imageUploadUrl) => {
    const messageForm = document.getElementById('comments-form');
    messageForm.action = imageUploadUrl;
    //messageForm.classList.remove('hidden');
  });
}

// Displays whether user is logged in based on servlet response
function authentication() {
  fetch('/login').then(response => response.json()).then(loginData => {
    const loginContainer = document.getElementById("login-text");
    loginContainer.className = "helper-text";
    if (loginData.email === null) {
      loginContainer.innerHTML = "<p>please login to view comments. login <a href=\"" + loginData.url + "\">here</a>.</p>";
      userLoggedIn = false;
    } else {
      loginContainer.innerHTML = "<p>you are logged in as " + loginData.email + "!\nlogout <a href=\""
        + loginData.url + "\">here</a>.</p>";
      userLoggedIn = true;
    }
  }).then(() => hideOrShowCommentsSection());
}

// Displays comments section if user is logged in 
function hideOrShowCommentsSection() {
  const commentsSection = document.getElementById("comments-area");
  if (userLoggedIn) {
    commentsSection.style.display = "block";
    loadComments();
  } else {
    commentsSection.style.display = "none";
  }
}

let currentPage = 0;
let numberOfPages;

// Disables button based on current page 
function disableButton() {
  const nextButton = document.getElementById('btnNext');
  const prevButton = document.getElementById('btnPrev');
  nextButton.disabled = false;
  prevButton.disabled = false;
  if (currentPage === 0) {
    nextButton.disabled = false;
    prevButton.disabled = true;
  } else if (currentPage === numberOfPages - 1 && currentPage != 0) {
    nextButton.disabled = true;
    prevButton.disabled = false;
  } else if (currentPage === numberOfPages - 1  && currentPage === 0) {
    nextButton.disabled = true;
    prevButton.disabled = true;
  }
}

// Moves to next page if available
function nextPage() {
  if (currentPage < numberOfPages - 1) {
    currentPage += 1;
    disableButton();
    loadComments();
  }
}

// Moves to previous page if available
function previousPage() {
  if (currentPage > 0) {
    currentPage -= 1;
    disableButton();
    loadComments();
  }
}
