window.onbeforeunload = function() {
    return "문제 풀기를 완료하지 않았습니다. 페이지를 떠나시겠습니까?";
//문제 다 풀면  window.onbeforeunload = null; (페이지 이동 가능)
};

window.onload = function () {


    // null확인
    if (game && Array.isArray(game)) {
        game.forEach(function(item) {
            // 각 'gameContent' 항목에 대한 작업 수행
            console.log(item);
        });
    }






}




