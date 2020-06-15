
let commentsLimit = 5;

// Retrieves json comments from server 
function loadComments() {

  const order = document.getElementById("order").value;

  fetch('/data?number=' + commentsLimit + '&order=' + order).then(response => response.json()).then((comments) => {
    const allCommentsList = document.getElementById('comments-container');
    allCommentsList.innerHTML = '';

    if (commentsLimit === Number.MAX_VALUE) {
      numberOfPages = 1;
      for (var i = 0; i < comments.length; i++) {
        allCommentsList.appendChild(createSingleComment(comments[i]));
      }
    } else {
      numberOfPages = Math.ceil(comments.length / commentsLimit);
      for (var i = currentPage * commentsLimit; i < currentPage * commentsLimit + commentsLimit; i++) {
        allCommentsList.appendChild(createSingleComment(comments[i]));
      }
    }
  });
  disableButton();
}

function createSingleComment(comment) {
  const commentDiv = document.createElement('div');
  commentDiv.className = "row";

  const imageDiv = document.createElement('div');
  imageDiv.className = "col-md-3";
  const contentDiv = document.createElement('div');
  if (comment.imageURL != null) {
    let imageContent = document.createElement('img');
    imageContent.className = "uploadedImage";
    imageContent.src = "serve?blob-key=" + comment.imageURL;;
    imageDiv.appendChild(imageContent);
    commentDiv.appendChild(imageDiv);
    contentDiv.className = "col-md-9";
  } else {
    contentDiv.className = "col-md-12";
  }

  const nameTitle = document.createElement('h3');
  nameTitle.className = "comments-name"
  nameTitle.innerText = sanitizeHTML(comment.name);
  contentDiv.append(nameTitle);

  const emailContent = document.createElement('p');
  emailContent.className = "comments-text"
  emailContent.innerText = comment.email;
  contentDiv.appendChild(emailContent);

  const commentContent = document.createElement('p');
  commentContent.className = "comments-text"
  const moodReceived = comment.mood;
  if (moodReceived === "happy") {
    commentContent.innerHTML = sanitizeHTML(comment.comment + "  😊 ");
  } else if (moodReceived === "heart") {
    commentContent.innerHTML = sanitizeHTML(comment.comment + "  😍 ");
  } else if (moodReceived === "surprised") {
    commentContent.innerHTML = sanitizeHTML(comment.comment + "  😯 ");
  } else if (moodReceived === "sad") {
    commentContent.innerHTML = sanitizeHTML(comment.comment + "  😥 ");
  } else {
    commentContent.innerHTML = sanitizeHTML(comment.comment);
  }
  contentDiv.appendChild(commentContent);

  const deleteCommentBtn = document.createElement("button");
  deleteCommentBtn.className = "single-delete-btn"
  deleteCommentBtn.innerText = "delete";
  deleteCommentBtn.addEventListener('click', () => {
    deleteSingleComment(comment);
  });
  contentDiv.appendChild(deleteCommentBtn);
  commentDiv.appendChild(contentDiv);
  return commentDiv;
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
  fetch('/delete-single-comment?id=' + id + '&email=' + email, {method: 'POST'})
        .then(loadComments());
}

// Returns choosen value of comments
function commentAmount() {
  const amount = document.getElementById("number");
  currentPage = 0;
  const value = amount.value;
  if (value === "5") {
      commentsLimit = 5;
    } else if (value === "10") {
      commentsLimit = 10;
    } else if (value === "15") {
        commentsLimit = 15;
    } else if(value === "20") {
        commentsLimit = 20;
    } else {
        commentsLimit = Number.MAX_VALUE;
    }
    loadComments();
}
