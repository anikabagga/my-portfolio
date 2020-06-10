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

// Prints Hello Anika on button click 
function sayHello() {
  fetch('/data').then(response => response.text()).then((greeting) => {
    document.getElementById('hello-container').innerText = greeting;
  });
}

window.onload = authentication();

// Retrieves json comments from server 
function loadComments() {
    
  const commentsLimit = commentAmount();
  const order = document.getElementById("order").value;

  fetch('/data?number=' + commentsLimit + '&order=' + order).then(response => response.json()).then((comments) => {
    const allCommentsList = document.getElementById('comments-container');
    allCommentsList.innerHTML = '';
      
    for (var i = 0; i < commentsLimit; i++) {
      allCommentsList.appendChild(createSingleComment(comments[i]));
    }
  });
}

function createSingleComment(comment) {
  const commentDiv = document.createElement('div');

  const nameTitle = document.createElement('h3');
  nameTitle.className = "comments-name"
  nameTitle.innerText = sanitizeHTML(comment.name);
  commentDiv.appendChild(nameTitle);

  const emailContent = document.createElement('p');
  emailContent.className = "comments-text"
  emailContent.innerText = comment.email;
  commentDiv.appendChild(emailContent);

  const commentContent = document.createElement('p');
  commentContent.className = "comments-text"
  const moodReceived = comment.mood;
  if (moodReceived === "happy") {
    commentContent.innerHTML = sanitizeHTML(comment.comment) + "  😊 ";
  } else if (moodReceived === "heart") {
    commentContent.innerHTML = sanitizeHTML(comment.comment) + "  😍 ";
  } else if (moodReceived === "surprised") {
    commentContent.innerHTML = sanitizeHTML(comment.comment) + "  😯 ";
  } else if (moodReceived === "sad") {
    commentContent.innerHTML = sanitizeHTML(comment.comment) + "  😥 ";
  } else {
    commentContent.innerHTML = sanitizeHTML(comment.comment);
  }
  commentDiv.appendChild(commentContent);

  const deleteCommentBtn = document.createElement("button");
  deleteCommentBtn.className = "single-delete-btn"
  deleteCommentBtn.innerText = "delete";
  deleteCommentBtn.addEventListener('click', () => {
    deleteSingleComment(comment);
  });
  commentDiv.appendChild(deleteCommentBtn);
  return commentDiv;
}

// Prevents HTML injection by removing markup from code
function sanitizeHTML(str) {
  const temp = document.createElement('div');
  temp.textContent = str;
  return temp.innerHTML;
}

// 
function fetchBlobstoreUrlAndShowForm() {
  fetch('/blobstore-handler')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        const messageForm = document.getElementById('comments-form');
        //messageForm.action = imageUploadUrl;
        //messageForm.classList.remove('hidden');
      });
}

// Performs POST request to /delete-data and fetches data again so comments are deleted
function deleteComments() {
  fetch("/delete-data", {method: 'POST'})
        .then(loadComments());
}

// Performs POST request to /delete-single-comment and fetches remaining comments 
function deleteSingleComment(comment) {
  const id = comment.id;
  const email = comment.email;
  console.log(email);
  fetch('/delete-single-comment?id=' + id + '&email=' + email, {method: 'POST'})
        .then(loadComments());
}

// Returns choosen value of comments
function commentAmount() {
  const amount = document.getElementById("number");
  const value = amount.value
  return value
}

let userLoggedIn = false;

// Displays whether user is logged in based on servlet response
function authentication() {
  fetch('/login').then(response => response.json()).then(loginData => {
    const loginContainer = document.getElementById("login-text");
    loginContainer.className = "helper-text";
    if (loginData.email === null) {
      loginContainer.innerHTML = "<p>please login to view comments. login <a href=\"" + loginData.url + "\">here</a>.</p>";
      userLoggedIn = false;
    } else {
      console.log(loginData.email);
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
const pages = 5;
const numberOfPages = 5;

// Disables button based on current page 
function disableButton() {
  const nextButton = document.getElementById('btnNext');
  const prevButton = document.getElementById('btnPrev');
  nextButton.disabled = false;
  prevButton.disabled = false;
  if (currentPage === 0) {
    btnNext.disabled = false;
    btnPrev.disabled = true;
  } else if (currentPage === numberOfPages - 1) {
    btnNext.disabled = true;
    btnPrev.disabled = false;
  } 
}

// Moves to next page if available
function nextPage() {
  if (currentPage < numberOfPages - 1) {
    currentPage += 1;
    buttonDisabler();
    generatePage(currentPage);
  }
}

// Moves to previous page if available
function previousPage() {
  if (currentPage > 0) {
    currentPage -= 1;
    buttonDisabler();
    generatePage(currentPage);
  }
}




