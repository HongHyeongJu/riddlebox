window.onbeforeunload = function () {
    return "문제 풀기를 완료하지 않았습니다. 페이지를 떠나시겠습니까?";
//문제 다 풀면  window.onbeforeunload = null; (페이지 이동 가능)
};

window.onload = function () {

// 게임 난이도에 따라 초기 목숨 개수 설정
    let lifeCount;
    const gameLevel2 = document.getElementById('gameLevel2').textContent;
    switch (gameLevel2) {
        case 'EASY':
            lifeCount = 5;
            break;
        case 'NORMAL':
            lifeCount = 3;
            break;
        case 'HARD':
            lifeCount = 1;
            break;
        default:
            lifeCount = 1; // 기본값 설정
    }

// 틀릴 때마다 호출되는 함수(life-cube 차감)
    function onWrongAnswer() {
        lifeCount--;
        if (lifeCount >= 0) {
            // 아이콘 상태 업데이트
            let lifeCube = document.getElementById(`lifeCube${lifeCount + 1}`);
            if (lifeCube) {
                lifeCube.style.opacity = '0.3'; // 아이콘 투명도 변경
            }
        }

        if (lifeCount === 0) {
            // 실패 페이지로 이동 todo : 게임 기회 전부 소모시 어떻게 할것인가
            window.location.href = '/game-failure';
        }
    }

// todo : 정답은 마이페이지에서 내가 해결한 문제 페이지에서 표시해주기?? (상단 일러스트, 아래는 해답, 1일 1번만 볼 수 있음?
//  아니면 이벤트나 설문조사로 볼 수 있는 해금의 기회 제공)

//게임 식별자
    const gameId = document.getElementById('gameId').textContent;

//비동기로 게임 contentList받아오기
    function getGameContentList(gameId) {

        fetch('/' + gameId + '/solve/contents')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(questionList => {
                //시작하기 버튼 비활성화
                let solveStartBtn = document.getElementById("solveStartBtn");
                solveStartBtn.setAttribute('disabled', 'disabled');

                //questionList를 처리
                console.log(questionList);
                // 예: 각 질문을 화면에 표시하기
                questionList.forEach(question => { addNewQuestionSet(question);

                });
            })
            .catch(error => {
                console.error('There was a problem with the fetch operation:', error);
            });
    }


    let questionCounter = 0; // 질문 순서를 나타내는 변수

//정답 체크
    function checkAnswer(event) {
        if (event && event.key !== "Enter") {
            return;
        }

        let userAnswer = event.target.value;
        let correctAnswer = "정답"; // 실제 정답을 여기에 입력하세요.

        let resultContainer = document.createElement("div");
        resultContainer.classList.add("quiz_result");

        let resultMessage = document.createElement("p");

        if (userAnswer === correctAnswer) {
            resultMessage.textContent = "정답"
            resultContainer.appendChild(resultMessage);
        } else {

        }

        resultMessage.textContent = userAnswer === correctAnswer ? "정답입니다!" : "오답입니다. 다시 시도하세요.";
        resultContainer.appendChild(resultMessage);

        let questionContainer = event.target.parentElement;
        questionContainer.appendChild(resultContainer);

        addNewQuestionSet(); // 다음 질문/정답 세트를 추가합니다.
    }

    function addNewQuestionSet() {
        if (questionCounter < 5) {
            let questionContainer = document.querySelector(".quiz_set_container");
            let newQuestionSet = document.createElement("div");
            newQuestionSet.classList.add("quiz_answer_set");

            let questionLabel = document.createElement("label");
            //class="question-custom fs-4 fw-bold mb-2 text-light custom-font"
            questionLabel.classList.add("fs-5", "fw-medium", "mb-2", "text-light", "custom-font");
            questionLabel.textContent = "질문 " + (questionCounter + 1) + ": 이 문제의 정답은 무엇인가요?";
            newQuestionSet.appendChild(questionLabel);

            let answerInput = document.createElement("input");
            answerInput.type = "text";
            answerInput.classList.add("form-control");
            answerInput.addEventListener("keyup", checkAnswer);

            newQuestionSet.appendChild(answerInput);
            questionContainer.appendChild(newQuestionSet);

            questionCounter++;
        } else {
            alert("총 10문제를 푸셨습니다.");
        }
    }

    // 페이지 로딩시 초기 질문/정답 세트를 추가합니다.
    addNewQuestionSet();


}




