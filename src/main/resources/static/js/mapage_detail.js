document.addEventListener('DOMContentLoaded', function () {
    adjustOverlay();
    window.addEventListener('resize', adjustOverlay);

    function adjustOverlay() {
        var img = document.querySelector('.desk-image'); // 이미지 선택
        var overlay = document.querySelector('.desk-overlay'); // 오버레이 선택

        // 이미지 크기에 따라 오버레이 및 내부 컨텐츠 조정
        overlay.style.width = img.offsetWidth + 'px';
        overlay.style.height = img.offsetHeight + 'px';

        // 추가적인 내부 요소 위치 조정이 필요한 경우 여기에 코드 추가
    }
});