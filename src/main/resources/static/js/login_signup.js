window.onload = function () {

    // CSRF 토큰 값과 헤더 이름 가져오기
    const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
    const csrfHeaderName = document.querySelector("meta[name='_csrf_header']").getAttribute("content");


    /* 이메일 유효성 검사 */
    let emailInput = document.getElementById('emailHead');
    let domainSelect = document.getElementById('custom-select');

    emailInput.addEventListener("input", validateEmail);
    domainSelect.addEventListener('change', validateEmail);

    function validateEmail() {
        let verifyButton = document.getElementById('verifyButton');

        // 함수 내에서 값을 매번 새로 가져옴
        let emailInputValue = emailInput.value;
        let domainSelectValue = domainSelect.value;

        // 이메일 형식 검증을 위한 정규 표현식
        let emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        // 입력된 이메일과 선택된 도메인을 합쳐서 검증
        let fullEmail = emailInputValue + '@' + domainSelectValue;

        if (emailRegex.test(fullEmail) && domainSelectValue !== "") {
            verifyButton.disabled = false; // 버튼 활성화
        } else {
            verifyButton.disabled = true; // 버튼 비활성화
        }
    }


    let debounceTimer;
    let allowRequest = true;



    /* 인증번호 전송 */
    document.getElementById('verifyButton').addEventListener('click', async function () {

        let fullEmail = emailInput.value + '@' + domainSelect.value;
        console.log("fullEmail " + fullEmail);

        if (!allowRequest) {
            console.log("잠시 후 다시 시도해주세요.");
            return;
        }

        allowRequest = false; // 추가 요청을 무시하기 위해 false로 설정
        clearTimeout(debounceTimer); // 이전 타이머를 초기화


        // 일정 시간(예: 5000ms = 5초) 후에 다시 요청을 허용
        debounceTimer = setTimeout(() => {
            allowRequest = true;
        }, 5000);

        try {
            const response = await fetch('/signup/send-email', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeaderName]: csrfToken
                },
                body: JSON.stringify({toEmail: fullEmail})
            });

            const result = await response.text(); // 서버로부터의 응답 메시지를 텍스트로 받음

            if (response.ok) {
                console.log(result); // 성공 메시지 로깅
                document.getElementById('verificationSection').classList.remove('hidden'); // 인증번호 입력 섹션 표시

            } else {
                alert("이미 사용중인 이메일 입니다");
            }
        } catch (error) {
            console.error('Error:', error.message);
        }
    });


    /* 인증번호 검사 */
    document.getElementById('verifyCodeBtn').addEventListener('click', async function () {

        const fullEmail = emailInput.value + '@' + domainSelect.value;
        let codeInput = document.getElementById('inputVerificationCode');
        const code = codeInput.value;
        // alert("code " + code);

        try {
            const response = await fetch('/signup/validate-code', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeaderName]: csrfToken
                },
                body: JSON.stringify({toEmail: fullEmail, code: code})
            });

            if (response.ok) {
                const isValid = await response.json(); // 서버가 boolean 값을 반환한다고 가정
                if (isValid) {
                    codeInput.disabled = true;
                    this.disabled = true;
                } else {
                    alert('인증번호가 유효하지 않습니다.');
                }
            } else {
                throw new Error('Verification failed');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    });


    /* 닉네임 중복 검사 */
    document.getElementById('nicknameBtn').addEventListener('click', async function checkNickname() {
        const nickname = document.getElementById('nickname').value;
        let nicknameCheckResult = document.getElementById('nicknameCheckResult');

        try {
            const response = await fetch('/signup/validate-nickname', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeaderName]: csrfToken
                },
                body: JSON.stringify({nickname: nickname})
            });
            const isAvailable = await response.json();
            if (isAvailable) {
                nicknameCheckResult.textContent = '사용 가능한 닉네임입니다.';
            } else {
                nicknameCheckResult.textContent = '사용할 수 없는 닉네임입니다.';
                nicknameCheckResult.style.color = 'blue'; // 경고 메시지 색상을 빨간색 으로 설정
            }
        } catch (error) {
            console.error('Error:', error);
            nicknameCheckResult.textContent = '닉네임 검사 중 오류가 발생했습니다.';
        }
    })


    /* 비밀번호 */

    let inputPassword1 = document.getElementById('password1');
    let inputPassword2 = document.getElementById('password2');

    inputPassword1.addEventListener('input', validatePassword);
    inputPassword2.addEventListener('input', matchPasswords);

    function validatePassword() {
        let password = inputPassword1.value;
        let passwordHelpBlock = document.getElementById('passwordHelpBlock');

        // 영문자와 숫자가 모두 포함하며, 길이가 8자 이상 12자 이하인지 정규식을 통해 검사
        let regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,12}$/;

        if (!regex.test(password)) {
            // 조건에 맞지 않으면 경고 메시지 표시
            passwordHelpBlock.textContent = '비밀번호는 영문자, 특수문자, 숫자를 포함한 8~12글자로 입력해주세요';
            passwordHelpBlock.style.display = 'block';
            passwordHelpBlock.style.color = 'red'; // 경고 메시지 색상을 빨간색 으로 설정
        } else {
            // 조건에 맞으면 경고 메시지 숨김
            passwordHelpBlock.style.display = 'none';
        }
    }


    function matchPasswords() {
        let password1 = inputPassword1.value;
        let password2 = document.getElementById('password2').value;
        let confirmHelpBlock = document.getElementById('confirmHelpBlock');

        if (password1 !== password2) {
            // 패스워드가 일치하지 않으면 경고 메시지 표시
            confirmHelpBlock.textContent = '동일한 패스워드를 입력하세요.';
            confirmHelpBlock.className = 'validation-message';
            confirmHelpBlock.style.display = 'block';
        } else {
            // 패스워드가 일치하면 성공 메시지 표시
            confirmHelpBlock.textContent = '비밀번호가 일치합니다.';
            confirmHelpBlock.className = 'validation-message success';
            confirmHelpBlock.style.display = 'block';
        }
    }

    document.getElementById('signupForm').addEventListener('submit', function (e) {
        let combinedEmail = emailInput.value + '@' + domainSelect.value;
        document.getElementById('hiddenEmailInput').value = combinedEmail;
    });


}
