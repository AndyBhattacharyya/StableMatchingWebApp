let messages = [];
let players;
let ranking_json;
let isMale;

//websocket endpoint to receive events when a lobby is created/modified
const lobby_API = "http://localhost:8080/lobby";
const url = "http://localhost:8080/"
const socket = new WebSocket(lobby_API);
let json_lobby;
socket.addEventListener("message", (event) => {
    //web socket returns json representation of lobby state containing array of users
    json_lobby = JSON.parse(event.data);
   //Redisplay of users
    players = [];
    for(let user of json_lobby.users){
        players.push(user);
    }
    updatePlayersSidePanel();

    //update game state
    if(json_lobby.gameState === 'READYUP')
        updateGameStateMessage(json_lobby.gameState, json_lobby.usersReady, json_lobby.maxPlayers);
    else if(json_lobby.gameState === 'SELECT')
        updateGameStateMessage(json_lobby.gameState, json_lobby.usersSelected, json_lobby.maxPlayers);
    else if(json_lobby.gameState === 'MATCHING')
        updateGameStateMessage(json_lobby.gameState, 0, 0)
  console.log(json_lobby);
});

let gameState = '';

//Message displaying the game state to the entire lobby
function updateGameStateMessage(state, part, total) {
    const messageElement = document.getElementById('gameStateMessage');
    gameState = state;

    switch(state) {
        case 'READYUP':
            messageElement.textContent = 'Waiting for '+ part + '/' + total + ' players to ready up...';
            messageElement.style.background = '#4a4a4a';
            closeRanking();
            break;
        case 'SELECT':
            messageElement.textContent = 'Waiting for '+ part + '/' + total + ' players to make a Selection...';
            messageElement.style.background = '#4CAF50';
            ranking_json =[]
            if(isMale!==undefined)
                showRanking();
            break;
        case 'MATCHING':
            messageElement.textContent = 'Processing All Selections, generating Perfect Matches';
            messageElement.style.background = '#2196F3';
            break;
        case 'DISPLAY':
            messageElement.textContent = 'Game has ended';
            messageElement.style.background = '#f44336';
            break;
        default:
            messageElement.textContent = '';
            messageElement.style.display = 'none';
    }
}

/*
Player is a json representation of our user in the backend
{
    "username":
    "isReady":
    "isMale":
    "userimage":
    "hasSelected":
}
 */
function createPlayerElement(player) {
    /*
    toggleReady() needs to update backend
     */
    const playerInfo = document.createElement('div');
    playerInfo.className = 'player-info';
    playerInfo.innerHTML = `
    <img src="${player.userimage}" alt="Player Avatar" class="player-avatar">
    <div class="player-details">
      <h3 class="player-name">${player.username}</h3>
      <p class="player-status">${player.isReady ? 'Ready' : 'Not Ready'}</p>
      <button onclick="toggleReady()" class="ready-button">
        ${player.isReady ? 'Cancel Ready' : 'Ready Up'}
      </button>
    </div>
  `;
    return playerInfo;
}

function toggleReady() {
    //fetch get request to invert isReady which is initially false, and retrieve whether or not user is boy/girl for middle
    let endpoint = url+"ready";
    fetch(endpoint).then(response => {
        return response.json()
    }).then(data => {
        isMale = data.isMale;
    })
}

/*
// Initialize with hardcoded players
players = [
    {
        username: "John",
        isReady: false,
        isMale: true,
        userimage: "https://api.dicebear.com/6.x/avataaars/svg?seed=John"
    },
    {
        username: "Mike",
        isReady: false,
        isMale: true,
        userimage: "https://api.dicebear.com/6.x/avataaars/svg?seed=Mike"
    },
    {
        username: "David",
        isReady: false,
        isMale: true,
        userimage: "https://api.dicebear.com/6.x/avataaars/svg?seed=David"
    },
    {
        username: "Emma",
        isReady: false,
        isMale: false,
        userimage: "https://api.dicebear.com/6.x/avataaars/svg?seed=Emma"
    },
    {
        username: "Sarah",
        isReady: false,
        isMale: false,
        userimage: "https://api.dicebear.com/6.x/avataaars/svg?seed=Sarah"
    },
    {
        username: "Lisa",
        isReady: false,
        isMale: false,
        userimage: "https://api.dicebear.com/6.x/avataaars/svg?seed=Lisa"
    }
];

updatePlayers(players);
 */

//Modal Ranking Component
function showRanking() {
    document.getElementById("rankingModal").style.display = "flex";
    loadRankingUI();
}

function closeRanking() {
    document.getElementById("rankingModal").style.display = "none";
}


let oppositeGenderPlayers;
function loadRankingUI() {
    const rankingHTML = `
    <h2 style="text-align:center;">Rank the Players</h2>
    <div class="player-list" id="playerList"></div>
    <button id="submitBtn">Submit Rankings</button>
  `;

    document.getElementById("rankingContainer").innerHTML = rankingHTML;


    if(isMale)
        oppositeGenderPlayers=players.filter(player => !player.isMale)
    else
        oppositeGenderPlayers=players.filter(player => player.isMale)



    const playerList = document.getElementById('playerList');

    function renderPlayers() {
        playerList.innerHTML = "";
        oppositeGenderPlayers.forEach((player, index) => {
            console.log("Index: " + index+ ", Player: " +player);
            const card = document.createElement('div');
            card.className = 'player-card';
            card.innerHTML = `
        <div class="player-info">
          <img src="${player.userimage}" alt="${player.username}" />
          <span>${index + 1}. ${player.username}</span>
        </div>
        <div class="controls">
          <button onclick="moveUp(${index})">⬆</button>
          <button onclick="moveDown(${index})">⬇</button>
        </div>
      `;
            playerList.appendChild(card);
        });
    }

    window.moveUp = (index) => {
        if (index > 0) {
            [oppositeGenderPlayers[index - 1], oppositeGenderPlayers[index]] = [oppositeGenderPlayers[index], oppositeGenderPlayers[index - 1]];
            renderPlayers();
        }
    };

    window.moveDown = (index) => {
        if (index < oppositeGenderPlayers.length - 1) {
            [oppositeGenderPlayers[index + 1], oppositeGenderPlayers[index]] = [oppositeGenderPlayers[index], oppositeGenderPlayers[index + 1]];
            renderPlayers();
        }
    };

    document.getElementById("submitBtn").addEventListener("click", () => {
        const payload = {
            rankings: oppositeGenderPlayers.map(p => p.username)
        };
        fetch("/select", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        }).then(() => {
            alert("Rankings submitted!");
            closeRanking();
        }).catch(err => console.error("Submission failed, Please try again", err));
    });

    renderPlayers();
}

function updatePlayersSidePanel() {
    // Clear existing players
    document.getElementById('malePlayers').innerHTML = '';
    document.getElementById('femalePlayers').innerHTML = '';
    document.getElementById('playersGrid').innerHTML = '';

    // Add players to side panels
    players.forEach(player => {
        // Add to the appropriate side based on gender
        const container = player.isMale ?
            document.getElementById('malePlayers') :
            document.getElementById('femalePlayers');

        const playerElement = createPlayerElement(player);
        container.appendChild(playerElement);
    });
}


function sendMessage() {
    const input = document.getElementById('messageInput');
    const message = input.value.trim();

    if (message) {
        const messageDiv = document.createElement('div');
        messageDiv.textContent = `Player: ${message}`;
        document.getElementById('chatMessages').appendChild(messageDiv);
        input.value = '';

        // Auto scroll to bottom
        const chatMessages = document.getElementById('chatMessages');
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }
}

// Handle enter key in chat
document.getElementById('messageInput').addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        sendMessage();
    }
});
