document.addEventListener('DOMContentLoaded', function() {
    var currentPage = 0;
    var totalPages = parseInt(document.getElementById('totalPages').value); // 총 페이지 수
    var searchKeyword = document.getElementById('searchKeyword').value; // 검색 키워드를 가져오는 방법을 추가해야 합니다.

    document.getElementById('loadMore').addEventListener('click', function() {
        if (currentPage + 1 < totalPages) {
            currentPage++;
            fetch(`/search?searchKeyword=${encodeURIComponent(searchKeyword)}&page=${currentPage}`, {
                method: 'GET'
            })
            .then(response => response.json()) // JSON 응답을 처리
            .then(data => {
                // JSON 데이터를 HTML로 변환하고 페이지에 추가
                data.results.forEach(gameDto => {
                    const cardHtml = `
                        <div class="gameCard-cards">
                            <a href="/game/${gameDto.id}/${gameDto.gameUrlType}" class="text-decoration-none">
                                <div class="gameCard-card card position-relative">
                                    <span class="position-absolute top-0 end-0 gameCard-badge badge rounded-pill bg-danger">NEW</span>
                                    <img src="${gameDto.thumbnailUrl}" alt="Game Image">
                                    <div class="gameCard-card-body mx-auto p-0 d-flex align-items-center">
                                        <h3 class="gameCard-card-title mx-auto p-0 m-0 px-2">${gameDto.title}</h3>
                                    </div>
                                </div>
                            </a>
                        </div>`;
                    document.getElementById('gameListContainer').insertAdjacentHTML('beforeend', cardHtml);
                });
                if (currentPage + 1 >= totalPages) {
                    document.getElementById('loadMore').style.display = 'none'; // 마지막 페이지에 도달하면 버튼 숨김
                }
            })
            .catch(error => console.error('Error loading more games:', error));
        }
    });
});
