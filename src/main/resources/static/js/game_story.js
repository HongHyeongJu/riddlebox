window.onload = function () {
//테스트
//     alert('gameStoryPage');

// 게임 시작 시간을 기록
    let gameStartTime = new Date();




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
                    setupBeforeUnloadListener(false); // 경고 비활성화
                    console.log("data.redirectUrl "+ data.redirectUrl)
                    window.location.href = data.redirectUrl; // 서버에서 받은 URL로 페이지 리디렉션
                }
            })
            .catch(error => console.error('Error:', error));
    }

    function beforeUnloadHandler(event) {
        event.returnValue = "변경사항이 저장되지 않을 수 있습니다.";
        return event.returnValue;
    }

    function setupBeforeUnloadListener(shouldWarn) {
        window.removeEventListener('beforeunload', beforeUnloadHandler);

        if (shouldWarn) {
            window.addEventListener('beforeunload', beforeUnloadHandler);
        }
    }


}