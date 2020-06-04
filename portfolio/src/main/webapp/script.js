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
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

//Prints Hello Anika on button click 
function sayHello(){
    fetch('/data').then(response => response.text()).then((greeting) => {
    document.getElementById('hello-container').innerText = greeting;
  });
}

window.onload = loadComments();

//Retrieves json comments from server 
function loadComments(){
    
    var commentsLimit = commentAmount();
    var order = document.getElementById("order").value;

    fetch('/data?number=' + commentsLimit + '&order=' + order).then(response => response.json()).then((comments) => {
      const allCommentsList = document.getElementById('comments-container');
      allCommentsList.innerHTML = '';
      
      for(var i = 0; i < commentsLimit; i++){
        let singleComment = document.createElement('p');
        singleComment.innerText = `Name: ${comments[i].name} 
                                    Comment: ${comments[i].comment} ${comments[i].mood}`;
        allCommentsList.append(singleComment);
      }
    });
}


//Performs POST request to /delete-data and fetches data again so comments are deleted
function deleteComments(){
    fetch('delete-data', {method: 'POST'})
        .then(loadComments());
}

//Returns choosen value of comments
function commentAmount(){
    var amount = document.getElementById("number");
    var value = amount.value
    return value
}





