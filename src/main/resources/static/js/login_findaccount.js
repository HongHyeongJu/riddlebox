function sendVerificationCode() {
    // 여기에 인증번호 전송 로직을 구현
    document.getElementById('verificationSection2').style.display = 'block';
}

function verifyCode() {
    // 실제 애플리케이션에서는 서버에 인증번호 검증 요청을 보내야 함
    // 여기에서는 검증이 성공했다고 가정
    document.getElementById('resultSection').style.display = 'block';
    document.getElementById('accountInfo').innerHTML = '회원님의 아이디: <strong>user123</strong><br>임시 비밀번호: <strong>tempPassword!23</strong><br>로그인 후 비밀번호를 변경해 주세요.';
}