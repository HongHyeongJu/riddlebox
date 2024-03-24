window.onload = function () {
//테스트
//     alert('gameStoryPage');

//게임 레벨
    const gameLevel = document.getElementById('gameLevel').textContent;

// 게임 시작 시간을 기록
    let gameStartTime = new Date();

//게임 타이머
    function startTimer(duration, element) {
        let start = Date.now();
        let timerInterval = setInterval(function () {
            let elapsed = Date.now() - start;
            let remainingTime = duration - elapsed;

            if (remainingTime <= 0) {
                clearInterval(timerInterval);
                element.style.width = '0%';

                // 타이머가 종료되면 문제풀이 페이지로 자동 리디렉션
                window.location.href = '/game/'+gamePK+'/solve';
            } else {
                let percentage = (remainingTime / duration) * 100;
                element.style.width = percentage + '%';
            }
        }, 50); // 50 밀리초 간격으로 너비 업데이트
    }


// 상태바 시작
    if (gameLevel == 'EASY') {
        // alert("gameLevel" + gameLevel);
        startTimer(420000, document.getElementById('time-bar')); // 7분 동안 줄어듦
    } else if (gameLevel == 'NORMAL') {
        startTimer(300000, document.getElementById('time-bar')); // 5분 동안 줄어듦
    } else if (gameLevel == 'HARD') {
        startTimer(180000, document.getElementById('time-bar')); // 3분 동안 줄어듦
    }
//1초 = 10,000


// 숨겨진 필드에서 게임 ID 읽기 = 게임 PK
    const gameId = document.getElementById('gameId').textContent;

    // '게임 나가기' 버튼에 이벤트 리스너 추가
    document.getElementById('exitGame').addEventListener('click', function () {
        // 게임 종료 시간을 기록
        let gameEndTime = new Date();

        // 플레이 타임 계산 (초 단위)
        let playTime = (gameEndTime - gameStartTime) / 1000;

        //결과 설정
        let gameResult = "ABANDONED"; // 게임 중도 포기

        // 서버에 기록을 전송하는 함수 호출
        sendGameRecord(gameId, playTime, gameResult);

    });


    // 게임 기록을 서버에 전송하는 함수
    function sendGameRecord(gamePK, playTime, gameResult) {
        // console.log(`게임 PK: ${gamePK}, 플레이 시간: ${playTime}ms, 결과: ${gameResult}`);

        fetch('/api/games/user_exit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    gamePK: gamePK,
                    playTime: playTime,
                    gameResult: gameResult
                }),
            })
            .then(response => response.json())
            .then(data => {
                if (data.redirectUrl) {
                    window.location.href = data.redirectUrl; // 서버에서 받은 URL로 페이지 리디렉션
                }
            })
            .catch(error => console.error('Error:', error));

            }


}