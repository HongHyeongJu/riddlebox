function showVerification() {
    document.getElementById('verificationSection').style.display = 'block';
}

function validatePassword() {
    var password = document.getElementById('inputPassword1').value;
    var passwordHelpBlock = document.getElementById('passwordHelpBlock');

    // 영문자와 숫자가 모두 포함하며, 길이가 8자 이상 12자 이하인지 정규식을 통해 검사
    var regex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,12}$/;

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
    var password1 = document.getElementById('inputPassword1').value;
    var password2 = document.getElementById('inputPassword2').value;
    var confirmHelpBlock = document.getElementById('confirmHelpBlock');

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