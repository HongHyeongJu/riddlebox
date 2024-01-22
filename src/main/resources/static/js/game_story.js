window.onload = function () {
//테스트
//     alert('gameStoryPage');

//게임 레벨
    const gameLevel = document.getElementById('gameLevel').textContent;

//게임 타이머
    function startTimer(duration, element) {
        let start = Date.now();
        let timerInterval = setInterval(function () {
            let elapsed = Date.now() - start;
            let remainingTime = duration - elapsed;

            if (remainingTime <= 0) {
                clearInterval(timerInterval);
                element.style.width = '0%';
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

}