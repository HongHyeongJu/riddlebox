window.onload = function () {
    // CSRF 토큰과 헤더 이름
    const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
    const csrfHeaderName = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

    // 게임 정보와 상태 변수
    const gameId = document.getElementById('gameId').textContent;
    let gameStartTime = new Date();
    let lifeCount;
    let totalQuestions;
    let currentCorrectAnswers = 0;
    let currentIncorrectAnswers = 0;

    // 문제 표시 관련 요소
    const leftQAdiv = document.getElementById('left_QA_div');
    const rightQAdiv = document.getElementById('right_QA_div');

    // 게임 시작 시 난이도에 따라 초기 목숨 설정
    initializeLifeCount();

    // '문제풀기' 버튼에 이벤트 리스너 추가
    document.getElementById('solveStartBtn').addEventListener('click', fetchQuestions);
    // '중도포기' 버튼에 이벤트 리스너 추가
    document.getElementById('isAbandoned').addEventListener('click', handleAbandonment);

    // 게임의 문제들을 서버에서 가져오는 함수
    async function fetchQuestions() {
        const solveStartBtn = document.getElementById('solveStartBtn');
        solveStartBtn.disabled = true; // 버튼을 비활성화
        solveStartBtn.classList.remove('shine-button'); // shine-button 클래스를 제거하여 반짝임 효과를 제거

        try {
            const response = await fetch(`/api/games/${gameId}/getQuestions`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeaderName]: csrfToken
                }
            });
            if (!response.ok) throw new Error('Network response was not ok');

            const questions = await response.json();
            totalQuestions = questions.length;

            // questions.forEach((question, index) => processQuestion(question, index));
            await processQuestionsSequentially(questions); // 순차적으로 문제를 처리하는 함수 호출

        } catch (error) {
            console.error('There has been a problem with your fetch operation:', error);
        }
    }

    // 게임 난이도에 따라 초기 목숨 개수 설정하는 함수
    function initializeLifeCount() {
        let gameLevel = document.getElementById('lifeCount').textContent;
        switch (gameLevel) {
            case 'EASY':
                lifeCount = 5;
                break;
            case 'MIDDLE':
                lifeCount = 3;
                break;
            case 'HARD':
                lifeCount = 2;
                break;
            default:
                lifeCount = 0; // 기본값 설정, 또는 오류 처리
        }
    }

    // 문제를 순차적으로 처리하는 함수
    async function processQuestionsSequentially(questions) {
        for (let i = 0; i < questions.length; i++) {
            // 현재 문제를 처리하고, 처리가 완료될 때까지 기다립니다.
            await processQuestion(questions[i], i + 1);
        }

        let numberOfQuestions = currentCorrectAnswers + currentIncorrectAnswers;

        //마지막 문제일시 기록 -> 결과페이지로
        if (totalQuestions === numberOfQuestions) {
            // 플레이 타임 계산 (초 단위)
            let gameEndTime = new Date();
            let playTime = (gameEndTime - gameStartTime) / 1000;
            //마지막 문제일시 기록!
            sendGameResult(gameId, playTime, false);
        }
    }

    // 개별 문제를 처리하는 함수
    async function processQuestion(question, index) {
        createQuestionTag(question, index );
        const gameContentId = question.gameContentId;
        // Promise를 반환하는 함수를 사용하여 사용자의 입력을 기다림
        await new Promise((resolve, reject) => {
            const inputTagElement = document.getElementById('questionInput' + (index));
            inputTagElement.addEventListener('keyup', async function (event) {
                if (event.key === 'Enter') {
                    // 앞뒤 공백 제거
                    const trimmedAnswer = this.value.trim();
                    // 입력값이 비어있는지 확인
                    if (trimmedAnswer === '') {
                        return; // 입력값이 비어있으면 함수 실행 중단
                    }
                    const answerResponse = await submitUserAnswerEvent(gameContentId, this.value, this);
                    // console.log('isCorrect ' + answerResponse.correct);

                    if (answerResponse) {
                        currentCorrectAnswers++;
                    } else {
                        currentIncorrectAnswers++;

                        console.log('틀림 ' + lifeCount);
                        onWrongAnswerDeleteOneLife();
                    }

                    const inputElement = document.getElementById('questionInput' + (index));
                    displayResult(answerResponse, inputElement);
                    resolve();


                }
            });
        });
    }


    // 오답 선택 시 목숨을 감소시키는 함수
    function onWrongAnswerDeleteOneLife() {
        lifeCount--;
        if (lifeCount > 0) {
            // 아이콘 상태 업데이트
            let lifeCubeElement = document.getElementById(`lifeCube${lifeCount + 1}`);
            if (lifeCubeElement) {
                lifeCubeElement.classList.remove('life-cube'); // 기존 클래스 제거
                lifeCubeElement.classList.add('fail-cube');    // 새로운 클래스 추가
            }
        } else {
            let gameEndTime = new Date();
            // 플레이 타임 계산 (초 단위)
            let playTime = (gameEndTime - gameStartTime) / 1000;
            // 실패 페이지로 이동 todo : 게임 기회 전부 소모시 어떻게 할것인가.. 일단 페이지 이동
            sendGameResult(gameId, playTime, true);
        }
    }

    // 문제 태그를 생성하는 함수
    function createQuestionTag(question, questionNumber) {
        const questionText = question.question;
        const gameContentId = question.gameContentId
        console.log('questionText ' + questionText);
        console.log('gameContentId ' + gameContentId);

        // 새로운 질문 및 입력 필드를 포함하는 div 요소 생성
        const newQuestionDiv = document.createElement("div");
        newQuestionDiv.classList.add("p-3", "pb-1", "pt-1", "d-flex", "flex-column");

        // 질문 레이블 생성
        const questionLabel = document.createElement("label");
        questionLabel.textContent = " Quiz" + (questionNumber) + ". " + questionText;
        questionLabel.classList.add("mb-2");

        // 입력 필드 생성
        const answerInput = document.createElement("input");
        answerInput.id = "questionInput" + (questionNumber);
        answerInput.type = "text";
        answerInput.classList.add("form-control");
        answerInput.placeholder = "정답을 입력하고 Enter";


        // 질문 레이블과 입력 필드를 새로운 div에 추가
        newQuestionDiv.appendChild(questionLabel);
        newQuestionDiv.appendChild(answerInput);

        // 새로운 질문 div를 부모 div에 추가
        if (questionNumber <= 5) {
            leftQAdiv.appendChild(newQuestionDiv);
        } else {
            rightQAdiv.appendChild(newQuestionDiv);
        }
    }

    // 사용자의 답변을 서버로 전송하고 결과를 받는 함수
    async function submitUserAnswerEvent(gameContentId, userAnswer, inputElement) {
        try {
            const response = await fetch('/api/games/submitAnswer'
                , {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeaderName]: csrfToken
                    },

                    body: JSON.stringify({gameContentId: gameContentId, userAnswer: '-' + userAnswer + '-'})
                });

            if (!response.ok) {
                throw new Error('submitUserAnswerEvent Error!! Network response was not ok');
            }

            inputElement.disabled = true; // 입력 필드 비활성화
            const data = await response.json();

            return data.isCorrect;

        } catch (error) {
            console.error('There has been a problem with your fetch operation:', error);
            throw error; // 오류를 상위로 전파
        }
    }

    // 문제의 정답 여부에 따라 결과를 표시하는 함수
    function displayResult(isCorrect, inputTagElement) {
        const resultTag = document.createElement("label");
        resultTag.classList.add("form-label", "mt-2", "pl-1");

        if (isCorrect) {
            //inputTagElement 아래에 정답입니다.
            // resultTag.classList.add("text-secondary"); // 회색 텍스트
            resultTag.textContent = "정답";
        } else {
            //inputTagElement 아래에 틀렸습니다.
            resultTag.classList.add("text-danger", "fst-italic"); // 빨간색, 기울인 텍스트
            resultTag.textContent = "틀렸습니다";
            //그로인해 생명기회 차감
        }
        // 입력태그 바로 다음에 결과태그 삽입
        inputTagElement.after(resultTag);
    }

    // 중도포기 처리를 하는 함수
    function handleAbandonment(gameResult) {
        // 게임 종료 시간을 기록
        let gameEndTime = new Date();
        // 플레이 타임 계산 (초 단위)
        let playTime = (gameEndTime - gameStartTime) / 1000;
        // 서버에 기록을 전송하는 함수 호출
        recordGameAbandonment(gameId, playTime, "ABANDONED");
    }

    // 중도 포기 결과를 서버에 전송하는 함수
    // @PostMapping("/user_exit")
    // public ResponseEntity<?> UserExitGate(@RequestBody GameExitRequest gameExitRequest)
    // GameExitRequest(Long gamePK, int playTime, String gameResult) 미채택
    // GameExitRequest(Long gameId, int playTime, int correctAnswers, int incorrectAnswers, String gameResult) 채택
    function recordGameAbandonment(gameId, playTime, gameResult) {
        fetch('/api/games/user_exit', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeaderName]: csrfToken
            },
            body: JSON.stringify({
                gameId: gameId,
                playTime: playTime,
                correctAnswers: currentCorrectAnswers,
                incorrectAnswers: currentIncorrectAnswers,
                gameResult: gameResult
            }),
        })
            .then(response => response.json())
            .then(data => {
                if (data.redirectUrl) {
                    redirectToGameResultPage(data.redirectUrl); // 경고 비활성화 + 서버에서 받은 URL로 페이지 리디렉션
                }
            })
            .catch(error => console.error('Error:', error));
    }


    // 게임 결과를 서버에 전송하는 함수
    // @PostMapping("/{gameId}/result")
    // public ResponseEntity<?> recordGameResult(@PathVariable("gameId") Long gameId,
    //                                           @RequestBody GameCompletionRequest gameCompletionRequest)
    // GameCompletionRequest(int totalQuestions, int correctAnswersCount, int playTime, boolean isFail)
    function sendGameResult(gameId, playTime, isFail) {
        console.log(gameId + "  " + totalQuestions + "  " + currentCorrectAnswers + "  " + playTime + "  " + isFail);
        fetch('/api/games/' + gameId + '/result', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeaderName]: csrfToken
            },
            body: JSON.stringify({
                gameId: gameId,
                totalQuestions: totalQuestions,
                correctAnswersCount: currentCorrectAnswers,
                playTime: playTime,
                isFail: isFail
            }),
        })
            .then(response => response.json())
            .then(data => {
                if (data.redirectUrl) {
                    redirectToGameResultPage(data.redirectUrl); // 경고 비활성화 + 서버에서 받은 URL로 페이지 리디렉션
                }
            })
            .catch(error => console.error('Error:', error));
    }

    // 결과 페이지로 리다이렉트 시키는 함수
    function redirectToGameResultPage(redirectUrl) {
        setupBeforeUnloadListener(false); // 경고 비활성화
        window.location.href = redirectUrl; // 서버에서 받은 URL로 페이지 리디렉션
    }


    //페이지 이동 관련 함수
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

};
