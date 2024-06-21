// Initialize STOMP client
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/chat',
    connectHeaders: {
        'Authorization': "Bearer " + '유효한 토큰 값을 넣어주세요.'
    },
});

// 연결 설정 함수
function connect() {
    stompClient.activate();
    stompClient.onConnect = (frame) => {
        setConnected(true);
        console.log('Connected: ' + frame);
        // room_id는 UUID이고, 아래 예시는 글로벌 채팅방일 때를 가정
        const room_id = 'global';
        // 토픽에 대한 구독
        stompClient.subscribe('/topic/room/'+room_id, (message) => {
            showGreeting(JSON.parse(message.body).content);
        });
    };

    // WebSocket 오류 처리 함수
    stompClient.onWebSocketError = (error) => {
        console.error('WebSocket Error:', error);
    };

    // STOMP 프로토콜 오류 처리 함수
    stompClient.onStompError = (frame) => {
        console.error('Broker reported error:', frame.headers['message']);
        console.error('Additional details:', frame.body);
    };
}

// 연결 해제 함수
function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log('Disconnected');
}

// 입장 전송 함수
function enterRoom() {
    const room_id = 'global'; // Replace with the actual chat room ID
    const messageContent = '입장했습니다.'; // 메시지 내용 입력 필드에서 가져옴

    const message = {
        chatroom_id: room_id,
        content: messageContent,
        type: 'ENTER'
    };

    stompClient.publish({
        destination: '/app/enter-room/' + room_id, // Adjust the endpoint as needed
        body: JSON.stringify(message) // You can pass additional data if needed
    });
}

// 메시지 전송 함수
function sendMessage() {
    const name = $('#name').val(); // 이름 입력 필드에서 가져옴
    const messageContent = $('#message').val(); // 메시지 내용 입력 필드에서 가져옴
    const room_id = 'global'; // 실제 채팅방 ID로 교체

    const message = {
        chatroom_id: room_id,
        content: messageContent,
        type: 'TALK'
    };

    stompClient.publish({
        destination: '/app/send-message', // 메시지 매핑 엔드포인트
        body: JSON.stringify(message)
    });
}

// 수신된 메시지를 화면에 표시하는 함수
function showGreeting(message) {
    console.log(message);
    $('#greetings').append('<tr><td>' + message + '</td></tr>');
}

// 연결 상태에 따라 UI를 업데이트하는 함수
function setConnected(connected) {
    $('#connect').prop('disabled', connected);
    $('#disconnect').prop('disabled', !connected);
    if (connected) {
        $('#conversation').show();
    } else {
        $('#conversation').hide();
    }
}

// 버튼에 클릭 이벤트 리스너 등록
$(function() {
    $('#connect').click(() => connect());
    $('#disconnect').click(() => disconnect());
    $('#send').click(() => sendMessage());
    $('#enter-room').click(() => enterRoom());
});
