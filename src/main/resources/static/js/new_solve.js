window.onbeforeunload = function () {
    //사이트에서 나가시겠습니까?
    //변경사항이 저장되지 않을 수 있습니다.
    return true;
    //문제 다 풀면  window.onbeforeunload = null; (페이지 이동 가능 할까말까)
};



window.onload = function () {

    /*게임 난이도에 따라서 초기 목숨 개수 설정*/
    let gameLevel  = document.getElementById('lifeCount').textContent;
    let lifeCount;

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


    /*틀릴때 마다 life-cube 차감(색깔 변화 + 전부 소진시 현재 정답률 가지고 실패결과 페이지로 redirect)*/
    function onWrongAnswerDeleteOneLife() {
        if (lifeCount > 0) {
            // 아이콘 상태 업데이트
            let lifeCubeElement = document.getElementById(`lifeCube${lifeCount + 1}`);
            if (lifeCubeElement) {
                lifeCubeElement.classList.remove('life-cube'); // 기존 클래스 제거
                lifeCubeElement.classList.add('fail-cube');    // 새로운 클래스 추가
            }
        }

        if (lifeCount === 0) {
            // 실패 페이지로 이동 todo : 게임 기회 전부 소모시 어떻게 할것인가.. 일단 페이지 이동
            setTimeout(() => {
                window.location.href = '/game/' + gameId + '/result?isFail=true&totalQuestions=' + totalQuestions + '&correctAnswersCount=' + currentCorrectAnswers;
            }, 5000); // 5초 딜레이
        }
    }


    /*게임 식별자 얻기*/
    const gameId = document.getElementById('gameId').textContent;
    console.log('gameId ' + gameId);
    console.log('gameLevel2 ' + gameLevel2);

    /*공통으로 사용하는 변수 선언: 총 문제수, 정답수, 오답수*/
    let totalQuestions;
    let currentCorrectAnswers = 0;
    let currentIncorrectAnswers = 0;


    /*문제 하나씩 제시하고 채점
    * 만든 input 태그에 채점 비동기 event 걸기
    * 틀리면 onWrongAnswerDeleteOneLife()로 기회 차감
    * input태그 아래에 displayResult로 결과 표시
    * */
    async function processQuestion(question, index) {
        createQuestionTag(question, index + 1);
        const gameContentId = question.gameContentId;
        // Promise를 반환하는 함수를 사용하여 사용자의 입력을 기다림
        await new Promise((resolve, reject) => {
            const inputTagElement = document.getElementById('questionInput' + (index + 1));
            inputTagElement.addEventListener('keyup', async function (event) {
                if (event.key === 'Enter') {
                    // 앞뒤 공백 제거
                    const trimmedAnswer = this.value.trim();
                    // 입력값이 비어있는지 확인
                    if (trimmedAnswer === '') {
                        return; // 입력값이 비어있으면 함수 실행 중단
                    }
                    const answerResponse = await submitUserAnswerEvent(gameContentId, this.value, this);
                    console.log('isCorrect ' + answerResponse.correct);
                    if (answerResponse.correct) {
                        currentCorrectAnswers++;
                    } else {
                        currentIncorrectAnswers++;
                        lifeCount--;
                        console.log('틀림 '+lifeCount);
                        onWrongAnswerDeleteOneLife();
                    }

                    const inputElement = document.getElementById('questionInput' + (index + 1));
                    displayResult(answerResponse.correct, inputElement);
                    resolve();
                }
            });
        });
    }


    /*시작 버튼을 누르면 비동기-문제 List 받아오기
    * function fetchQuestions(){}
    * */
    async function fetchQuestions() {
        //시작버튼은 한 번 누르면 비활성화
        document.getElementById('solveStartBtn').disabled = true;


        try {
            const response = await fetch('/api/games/' + gameId + '/getQuestions');
            if (!response.ok) {
                throw new Error('fetchQuestions Error!! Network response was not ok');
            }
            const questions = await response.json(); // 응답을 JSON 형식으로 변환
            /*
            * data : List<Question>  : Question(Long gameContentId, String question, Integer ordering)
            * 문제 하나 추출 => 제시하기(문제, input 태그 추가)
            * 사용자 입력 enter에 이벤트 함수 => 비동기로 결과 받기 (공백이나 아무것도 입력 안하고 엔터치는건 막기 -> 답을 입력하세여)
            * 받은 결과에 따라 표시
            * (반복
            * 그러나 마지막 문제인 경우
            * // 마지막 문제 채점 후...
            * */
            totalQuestions = questions.length;
            for (let index = 0; index < questions.length; index++) {
                await processQuestion(questions[index], index);
            }

            // 마지막 문제 처리 후 결과 페이지로 이동
            setTimeout(() => {
                window.location.href = '/game/' + gameId + '/result?totalQuestions=' + totalQuestions + '&correctAnswersCount=' + currentCorrectAnswers;
            }, 5000); // 5초 딜레이


        } catch (error) {
            console.error('There has been a problem with your fetch operation:', error);
        }
    }

    document.getElementById('solveStartBtn').addEventListener('click', fetchQuestions);


    const leftQAdiv = document.getElementById('left_QA_div');
    const rightQAdiv = document.getElementById('right_QA_div');

    /*다음 문제를 표시하기
     * function createQuestionTag(question, questionNumber){}
     * 매개변수 : String question, number questionNumber
     * */
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
        questionLabel.textContent = " Quiz" + questionNumber + ". " + questionText;
        questionLabel.classList.add("mb-2");

        // 입력 필드 생성
        const answerInput = document.createElement("input");
        answerInput.id = "questionInput" + questionNumber;
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


    /*사용자가 입력 후 엔터 이벤트로 입력한 값 받기. 앞뒤 공백 자르고 문자열("-") 붙여서 전달
    * function submitUserAnswerEvent(){}
    * 매개변수 : gameContentId, userAnswer, inputElement
    * 반환값 : true/false
    * */
    async function submitUserAnswerEvent(gameContentId, userAnswer, inputElement) {
        try {
            const response = await fetch('/api/games/submitAnswer'
                , {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({gameContentId: gameContentId, userAnswer: '-'+userAnswer+'-'})
                });

            if (!response.ok) {
                throw new Error('submitUserAnswerEvent Error!! Network response was not ok');
            }

            inputElement.disabled = true; // 입력 필드 비활성화
            const answerResponse = await response.json();

            console.log('Answer Response:', answerResponse);

            return answerResponse; // 변환된 JSON 객체 반환

        } catch (error) {
            console.error('There has been a problem with your fetch operation:', error);
            throw error; // 오류를 상위로 전파
        }
    }


    /*비동기 채점으로 얻은 결과값 표시해주기, 오답인경우 life-cube 차감하고 모두 소진시 실패 함수 호출
    * function displayResult(){}
    * 매개변수 : 채점결과
    * */
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


}