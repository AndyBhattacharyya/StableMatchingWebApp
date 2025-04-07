//Handle Websocket connection and displaying lobbies

//base url for requests to join/create a lobby
const url = "http://localhost:8080/";
//websocket endpoint to receive events when a lobby is created/modified
const lobbies_API = "http://localhost:8080/lobbies";
//hashmap to store lobbies, indexed by the host name
let lobbies_map = {};

const socket = new WebSocket(lobbies_API);
let json_lobbies;
socket.addEventListener("message", (event) => {
    //web socket returns an array of lobbies
    json_lobbies = JSON.parse(event.data);
    for(let lobby of json_lobbies.lobbies){
        let lobby_key = lobby.host;
        let newLobby = {
            host: lobby.host,
            lobbyName: lobby.lobbyName,
            maxPlayers: lobby.maxPlayers,
            currentPlayers: lobby.currentPlayers
        };
        //store lobby, indexed by hostname, rewrite if already exists to update it
        lobbies_map[lobby_key] = newLobby;
        //lobbies.push(newLobby);
        updateLobbiesList()
    }
    console.log(json_lobbies);
});



function createLobby() {
    const hostName = document.getElementById('playerName').value;
    const hostGender = document.getElementById('playerGender').value;
    const lobbyName = document.getElementById('lobbyName').value;
    const maxPlayers = parseInt(document.getElementById('maxPlayers').value);
    const userFile = document.getElementById('profilePic').files[0];

    if (!hostName || !lobbyName || !maxPlayers || !hostGender || !userFile) {
        alert('Please fill in all fields including uploading a picture');
        return;
    }
    const formData = new FormData();
    formData.append('hostName', hostName);
    formData.append('hostGender', hostGender);
    formData.append('lobbyName', lobbyName);
    formData.append('maxPlayers', maxPlayers);
    formData.append('userFile', userFile);

    let endpoint = url+"create";
    fetch(endpoint, {
        method: "POST",
        // Automatically converted to "username=example&password=password"
        body: formData
        // ...
    }).then(response => {
        if(response.redirected){
            window.location.href = response.url;
        } else{
            return
        }

    });
    clearInputs();
}

function joinLobby(lobbyName) {
    const playerName = document.getElementById('playerName').value;
    const hostGender = document.getElementById('playerGender').value;
    const userFile = document.getElementById('profilePic').files[0];
    if (!playerName || !lobbyName || !hostGender || !userFile) {
        alert('Please fill in all fields including uploading a picture');
        return;
    }
    const formData = new FormData();
    formData.append('hostName', playerName);
    formData.append('hostGender', hostGender);
    formData.append('lobbyName', lobbyName);
    formData.append('userFile', userFile);
    let endpoint = url+"join";
    fetch(endpoint, {
        method: "POST",
        // Automatically converted to "username=example&password=password"
        body: formData
        // ...
    }).then(response => {
        if(response.redirected){
            window.location.href = response.url;
        } else{
            return
        }

    });
}

function updateLobbiesList() {
    const lobbiesList = document.getElementById('lobbiesList');
    //every event/modification made to any lobby will redisplay all lobbies once again
    lobbiesList.innerHTML = '';
    /*
    lobbies.forEach((lobby, index) => {
        const lobbyElement = document.createElement('div');
        lobbyElement.className = 'lobby-card';
        lobbyElement.innerHTML = `
      <div class="lobby-info">
        <strong>Host:</strong> ${lobby.host}<br>
        <strong>Lobby:</strong> ${lobby.lobbyName}<br>
        <strong>Players:</strong> ${lobby.currentPlayers}/${lobby.maxPlayers}
      </div>
      <button onclick="joinLobby(${index})"
              ${lobby.currentPlayers >= lobby.maxPlayers ? 'disabled' : ''}>
        Join Lobby
      </button>
    `;
        lobbiesList.appendChild(lobbyElement);
    });
    */
    for(const lobby in lobbies_map){
        const lobbyElement = document.createElement('div');
        lobbyElement.className = 'lobby-card';
        lobbyElement.innerHTML = `
      <div class="lobby-info">
        <strong>Host:</strong> ${lobbies_map[lobby].host}<br>
        <strong>Lobby:</strong> ${lobbies_map[lobby].lobbyName}<br>
        <strong>Players:</strong> ${lobbies_map[lobby].currentPlayers}/${lobbies_map[lobby].maxPlayers}
      </div> 
      <button onclick="joinLobby('${lobbies_map[lobby].lobbyName}')" 
              ${lobbies_map[lobby].currentPlayers >= lobbies_map[lobby].maxPlayers ? 'disabled' : ''}>
        Join Lobby
      </button>
    `;
    lobbiesList.appendChild(lobbyElement);
    }
}


function clearInputs() {
    document.getElementById('hostName').value = '';
    document.getElementById('lobbyName').value = '';
    document.getElementById('maxPlayers').value = '';
}

// Initialize the lobbies list
updateLobbiesList();



