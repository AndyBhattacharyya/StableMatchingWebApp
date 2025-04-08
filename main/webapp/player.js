let messages = [];
let players;
let ranking_json;

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
    updateGameStateMessage(json_lobby.gameState);

    console.log(json_lobby);
});

let gameState = '';

//Message displaying the game state to the entire lobby
function updateGameStateMessage(state) {
    const messageElement = document.getElementById('gameStateMessage');
    gameState = state;

    switch(state) {
        case 'READYUP':
            messageElement.textContent = 'Waiting for players to ready up...';
            messageElement.style.background = '#4a4a4a';
            clearMiddle();
            break;
        case 'SELECT':
            messageElement.textContent = 'Game is starting...Please make a Selection';
            messageElement.style.background = '#4CAF50';
            updatePlayersMiddle();
            ranking_json =[]
            break;
        case 'DISPLAY':
            messageElement.textContent = 'Game in progress';
            messageElement.style.background = '#2196F3';
            break;
        case 'FINISHED':
            messageElement.textContent = 'Game has ended';
            messageElement.style.background = '#f44336';
            break;
        default:
            messageElement.textContent = '';
            messageElement.style.display = 'none';
    }
}
// Set initial game state
updateGameStateMessage('READYUP');


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

let isMale;
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
function clearMiddle() {
    const playersGrid = document.getElementById('playersGrid');
    playersGrid.innerHTML = '';
}
function updatePlayersMiddle(){

    // For center grid, only show opposite gender. We init isMale from above function of readying up
    const oppositeGenderPlayers = players.filter(player => !(player.isMale===isMale));
    /*
    oppositeGenderPlayers.forEach(player => {
        const playerContainer = document.createElement('div');
        playerContainer.className = 'player-container';

        const playerImg = document.createElement('img');
        playerImg.src = player.userimage;
        playerImg.className = 'player-thumbnail';

        const rankingContainer = document.createElement('div');
        rankingContainer.className = 'ranking';

        const totalPlayers = players.length;
        const rankingButtons = Math.floor(totalPlayers / 2);
        for (let i = 1; i <= rankingButtons; i++) {
            const button = document.createElement('button');
            button.className = 'rank-button';
            const ordinal = i === 1 ? '1st' : i === 2 ? '2nd' : i === 3 ? '3rd' : `${i}th`;
            button.textContent = ordinal;
            button.onclick = () => rankPlayer(player.username, ordinal);
            rankingContainer.appendChild(button);
        }

        playerContainer.appendChild(playerImg);
        playerContainer.appendChild(rankingContainer);
        document.getElementById('playersGrid').appendChild(playerContainer);
    });
    */
    const form = document.createElement('form');
    form.className = 'ranking-form';
    form.onsubmit = (e) => {
        e.preventDefault();
        console.log(form)
    };

    oppositeGenderPlayers.forEach(player => {
        const playerContainer = document.createElement('div');
        playerContainer.className = 'player-container';

        const playerImg = document.createElement('img');
        playerImg.src = player.userimage;
        playerImg.className = 'player-thumbnail';

        const rankSelect = document.createElement('select');
        rankSelect.className = 'rank-select';
        rankSelect.name = player.username;
        rankSelect.required = true;

        const defaultOption = document.createElement('option');
        defaultOption.value = '';
        defaultOption.textContent = 'Select rank';
        rankSelect.appendChild(defaultOption);

        const totalPlayers = players.length;
        const rankingOptions = Math.floor(totalPlayers / 2);
        for (let i = 1; i <= rankingOptions; i++) {
            const option = document.createElement('option');
            option.value = i.toString();
            option.textContent = i.toString();
            rankSelect.appendChild(option);
        }
        playerContainer.appendChild(playerImg);
        playerContainer.appendChild(rankSelect);
        form.appendChild(playerContainer);
    });

    // Add a single submit button at the bottom
    const submitButton = document.createElement('button');
    submitButton.type = 'submit';
    submitButton.textContent = 'Submit All Rankings';
    submitButton.className = 'submit-rankings-button';
    form.appendChild(submitButton);

    document.getElementById('playersGrid').appendChild(form);
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
