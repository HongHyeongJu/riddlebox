window.onload = function () {

    // CSRF 토큰 값과 헤더 이름 가져오기
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeaderName  = document.querySelector('meta[name="_csrf_header"]').content;


    /* 이메일 유효성 검사 */
    let emailInput = document.getElementById('emailInput');
    let domainSelect = document.getElementById('custom-select');

    emailInput.addEventListener("input", validateEmail );
    domainSelect.addEventListener('change' , validateEmail );

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



    /* 인증번호 전송 */
    document.getElementById('verifyButton').addEventListener('click',async function () {

        const fullEmail = emailInput.value + '@' + domainSelect.value;
        console.log("fullEmail "+ fullEmail);


        try {
            const response = await fetch('/signup/send-email', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({toEmail: fullEmail})
            });

            const result = await response.text(); // 서버로부터의 응답 메시지를 텍스트로 받음

            if (response.ok) {
                console.log(result); // 성공 메시지 로깅
                document.getElementById('verificationSection').classList.remove('hidden'); // 인증번호 입력 섹션 표시
            } else {
                throw new Error(result);
            }
        } catch (error) {
            console.error('Error:', error.message);
        }
    });



    /* 인증번호 검사 */
    document.getElementById('verifyCodeBtn').addEventListener('click', async function () {

        const fullEmail = emailInput.value + '@' + domainSelect.value; // 이메일 입력 필드의 ID를 'emailInput'이라고 가정합니다.
        const code = document.getElementById('verificationCode').value;

        try {
            const response = await fetch('/signup/validate-code', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({email: fullEmail, code: code})
            });

            if (response.ok) {
                const isValid = await response.json(); // 서버가 boolean 값을 반환한다고 가정
                if (isValid) {
                    console.log('Verification successful');
                    document.getElementById('verificationSection').classList.add('hidden'); // 인증 섹션 숨기기
                    // 추가적으로, 인증 성공 시 다른 UI 요소를 비활성화할 수 있습니다.
                } else {
                    console.log('Invalid verification code');
                    // 사용자에게 유효하지 않은 코드임을 알리는 UI 처리를 할 수 있습니다.
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
        const nickname = document.getElementById('inputNickname').value;
        try {
            const response = await fetch('/signup/validate-nickname', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({nickname: nickname})
            });
            const isAvailable = await response.json();
            if (isAvailable) {
                document.getElementById('nicknameCheckResult').textContent = '사용 가능한 닉네임입니다.';
            } else {
                document.getElementById('nicknameCheckResult').textContent = '사용할 수 없는 닉네임입니다.';
            }
        } catch (error) {
            console.error('Error:', error);
            document.getElementById('nicknameCheckResult').textContent = '닉네임 검사 중 오류가 발생했습니다.';
        }
    })







    /* 비밀번호 */

    let inputPassword1 = document.getElementById('inputPassword1');
    let inputPassword2 = document.getElementById('inputPassword2');

    inputPassword1.addEventListener('input', validatePassword);
    inputPassword2.addEventListener('input', matchPasswords);

    function validatePassword() {
        let password = inputPassword1.value;
        let passwordHelpBlock = document.getElementById('passwordHelpBlock');

        // 영문자와 숫자가 모두 포함하며, 길이가 8자 이상 12자 이하인지 정규식을 통해 검사
        let regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,12}$/;

        if (!regex.test(password)) {
            // 조건에 맞지 않으면 경고 메시지 표시
            passwordHelpBlock.textContent = '비밀번호는 영문자와 숫자를 포함한 8~12글자로 제한됩니다.';
            passwordHelpBlock.style.display = 'block';
            passwordHelpBlock.style.color = 'red'; // 경고 메시지 색상을 빨간색 으로 설정
        } else {
            // 조건에 맞으면 경고 메시지 숨김
            passwordHelpBlock.style.display = 'none';
        }
    }


    function matchPasswords() {
        let password1 = inputPassword1.value;
        let password2 = document.getElementById('inputPassword2').value;
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


}
