document.addEventListener("DOMContentLoaded", function() {
    // 모든 FAQ 질문 요소에 대해 이벤트 리스너 추가
    document.querySelectorAll('.faq-question').forEach(function(question) {
        question.addEventListener('click', function() {
            // 클릭된 질문에 대한 답변 요소의 표시 상태를 토글
            let answer = this.nextElementSibling; // card-body가 바로 다음 요소이므로 nextElementSibling 사용
            if (answer.style.display === "none") {
                answer.style.display = "";
            } else {
                answer.style.display = "none";
            }
        });
    });
});
