window.onload = function () {

    /*게임 난이도에 따라서 초기 목숨 개수 설정*/
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


    /*틀릴때 마다 life-cube 차감(색깔 변화 + 전부 소진시 현재 정답률 가지고 실패결과 페이지로 redirect)*/
    function onWrongAnswerDeleteOneLife() {
        lifeCount--;
        if (lifeCount >= 0) {
            // 아이콘 상태 업데이트
            let lifeCube = document.getElementById(`lifeCube${lifeCount + 1}`);
            if (lifeCube) {
                lifeCube.className("fail-cube"); // 아이콘 회색빛
            }
        }

        if (lifeCount === 0) {
            // 실패 페이지로 이동 todo : 게임 기회 전부 소모시 어떻게 할것인가.. 일단 페이지 이동
            window.location.href = '/game/result/fail';
        }
    }


    /*게임 식별자 얻기*/

    /*공통으로 사용하는 변수 선언: 총 문제수, 정답수, 오답수*/

    /*시작 버튼을 누르면 비동기-문제 List 받아오기
    * function fetchQuestions(){}
    * 매개변수 : 문제 식별자
    * */





    /*주어진 문제 배열을 foreach로 문제 출제하기
    * function DisplayNextQuestion(){}
    * 매개변수 : 문제 List(혹은 배열)
    * */




    /*사용자가 입력 후 엔터 이벤트로 입력한 값 받기. 앞뒤 공백 자르고 전달
    * function submitUserAnswerEvent(){}
    * 매개변수 :
    * */





    /*사용자의 입력 값에 -제출답-에 구분자 붙이고 비동기 채점하기, 전체문제수, 정답수, 오답수 계속 같이 갱신
    * function checkAnswerAsync(){}
    * 매개변수 : 사용자 입력값, [전체문제수, 정답수, 오답수]
    * */




    /*비동기 채점으로 얻은 결과값 표시해주기, 오답인경우 life-cube 차감하고 모두 소진시 실패 함수 호출
    * function displayResult(){}
    * 매개변수 : 채점결과
    * */




    /*오답으로 인한 생명차감과, 전체소진시 현재 결과를 가지고 redirect 시켜버리는 함수
    * function decrementLifeAndRedirect(){}
    * 매개변수 : [전체문제수, 정답수, 오답수]
    * */




    //문제List 받아오는 fetchQuestions() 호출 시작!

}