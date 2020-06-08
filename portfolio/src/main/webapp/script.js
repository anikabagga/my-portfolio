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

//Prints Hello Anika on button click 
function sayHello(){
    fetch('/data').then(response => response.text()).then((greeting) => {
    document.getElementById('hello-container').innerText = greeting;
  });
}

window.onload = loadComments();

//Retrieves json comments from server 
function loadComments(){
    
    const commentsLimit = commentAmount();
    const order = document.getElementById("order").value;

    fetch('/data?number=' + commentsLimit + '&order=' + order).then(response => response.json()).then((comments) => {
      const allCommentsList = document.getElementById('comments-container');
      allCommentsList.innerHTML = '';
      
      for(var i = 0; i < commentsLimit; i++){
        allCommentsList.append(createSingleComment[i]);
      }
    });
}

function createSingleComment(comment){
    const commentDiv = document.createElement('div');

    const nameTitle = document.createElement('h3');
    nameTitle.innerHTML = "Name: " + sanitizeHTML(comment.name);
    commentDiv.appendChild(nameTitle);

    const commentContent = document.createElement('p');
    const moodReceived = comment.mood;
    if (moodReceived === "happy"){
        commentContent.innerHTML = "Comment: " + sanitizeHTML(comment.comment) + "  😊 ";
    } else if(moodReceived === "heart"){
        commentContent.innerHTML = "Comment: " + sanitizeHTML(comment.comment) + "  😍 ";
    } else if(moodReceived === "surprised"){
        commentContent.innerHTML = "Comment: " + sanitizeHTML(comment.comment) + "  😯 ";
    } else if(moodReceived === "sad"){
        commentContent.innerHTML = "Comment: " + sanitizeHTML(comment.comment) + "  😥 ";
    } else {
            commentContent.innerHTML = "Comment: " + sanitizeHTML(comment.comment);
    }
    commentDiv.appendChild(commentContent);

    const deleteCommentBtn = document.createElement("button");
    deleteCommentBtn.innerHTML("<p> delete </p>");
    deleteCommentBtn.addEventListener('click', () => {
        deleteSingleComment(comment);
    });
    commentDiv.appendChild(deleteCommentBtn);

    return commentDiv;
}

//Prevents HTML injection by removing markup from code
function sanitizeHTML(str) {
    const temp = document.createElement('div');
    temp.textContent = str;
    return temp.innerHTML;
}

//Performs POST request to /delete-data and fetches data again so comments are deleted
function deleteComments(){
    fetch('delete-data', {method: 'POST'})
        .then(loadComments());
}

//Performs POST request to /delete-single-comment and fetches remaining comments 
function deleteSingleComment(comment){
    const id = comment.id;
     fetch('delete-single-comment?id=' + id, {method: 'POST'})
        .then(loadComments());
}

//Returns choosen value of comments
function commentAmount(){
    const amount = document.getElementById("number");
    const value = amount.value
    return value
}

let currentPage = 0;
const pages;
const numberOfPages;

//Disables button based on current page 
function diableButton(){
    const nextButton = document.getElementById('btnNext');
    const prevButton = document.getElementById('btnPrev');
    nextButton.disabled=false;
    prevButton.disabled=false;
    if (currentPage === 0) {
        btnNext.disabled=false;
        btnPrev.disabled=true;
    } else if (currentPage === numberOfPages - 1) {
        btnNext.disabled=true;
        btnPrev.disabled=false;
  }
}

//Moves to next page if available
function nextPage(){
    if (currentPage < numberOfPages - 1) {
        currentPage += 1;
        buttonDisabler();
        generatePage(currentPage);
    }
}

//Moves to previous page if available
function previousPage(){
    if(currentPage > 0){
        currentPage -= 1;
        buttonDisabler();
        generatePage(currentPage);
    }
}




