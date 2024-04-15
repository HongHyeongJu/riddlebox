window.onload = function () {
    // 숨겨진 입력에서 종료 시간을 파싱합니다
    const endTime = document.getElementById('end-time').value;
    const end = new Date(endTime).getTime();

    // 1초마다 카운트다운을 업데이트합니다
    const interval = setInterval(function () {
        const now = new Date().getTime();
        const distance = end - now;

        // 시간, 분, 초 계산
        const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);

        // id="timer"인 요소에 결과를 출력합니다
        document.getElementById("timer").textContent =
            (hours < 10 ? '0' : '') + hours + ":" +
            (minutes < 10 ? '0' : '') + minutes + ":" +
            (seconds < 10 ? '0' : '') + seconds;

        // 카운트다운이 끝나면 /index로 리디렉트합니다
        if (distance < 0) {
            clearInterval(interval);
            document.getElementById("timer").textContent = "EXPIRED";
            window.location.href = '/index';
        }
    }, 1000);


    //댓글 작성 비동기 API
    let newCommentButton = document.getElementById("new-comment-btn");

    loadMoreButton.addEventListener("click", function () {

        // 서버에서 댓글 데이터를 비동기적으로 가져오는 요청을 보냅니다.
        fetch("/load-more-comments")
            .then(response => response.json())
            .then(data => {
                // 받아온 데이터를 기존 댓글 목록에 추가합
                let commentList = document.getElementById("comment-list");
                data.forEach(comment => {
                    var commentHTML = "<div class='comment-box d-flex align-items-center mb-2'>" +
                        "<div class='nickname text-primary-emphasis'><strong>" + comment.nickName + "</strong></div>" +
                        "<div>" + comment.content + "</div>" +
                        "</div>";
                    commentList.innerHTML += commentHTML;
                });
            })
            .catch(error => console.error("Error loading more comments:", error));
    });


    //댓글 작성


    //댓글 페이지 이동
    function fetchComments(element, event) {
        event.preventDefault(); // 링크 기본 동작 방지
        let page = element.getAttribute('data-page'); // 페이지 번호 가져오기

        fetch(`/your-endpoint?gameId=${gameId}&page=${page}`) // 적절한 엔드포인트 설정 필요
            .then(response => response.json())
            .then(data => {
                // `data`에는 새로운 댓글 리스트가 포함되어 있습니다.
                updateCommentSection(data);
            })
            .catch(error => console.error('Error loading new comments:', error));
    }

    function updateCommentSection(data) {
        let commentListDiv = document.getElementById('comment-list');
        commentListDiv.innerHTML = ''; // 기존 댓글 내용 지우기

        data.commentDtoList.forEach(comment => {
            let commentDiv = document.createElement('div');
            commentDiv.className = 'comment-box d-flex align-items-center mb-2';
            commentDiv.innerHTML = `
            <div class="nickname text-primary-emphasis">
                <strong>${comment.nickName}</strong>
            </div>
            <div>${comment.content}</div>
        `;
            commentListDiv.appendChild(commentDiv);
        });
    }


};
