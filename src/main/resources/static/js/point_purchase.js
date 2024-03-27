document.addEventListener('DOMContentLoaded', function () {
    function hideEmptyMsgRow() {
        let emptyMsgRow = document.getElementById("emptyMsgRow");
        if (emptyMsgRow) {
            emptyMsgRow.hidden = true;
        }
    }

    function showEmptyMessage() {
        let emptyMsgRow = document.getElementById('emptyMsgRow');
        if (emptyMsgRow) {
            emptyMsgRow.hidden = false;
        }
    }

    // 장바구니에 추가 버튼에 대한 클릭 이벤트 리스너
    document.getElementById('point_100_add').addEventListener('click', function () {
        hideEmptyMsgRow();
        // 테이블에 해당 트레이가 있는지 검사
        let tr = document.getElementById('point_100_tr');
        if (tr.hidden) {
            // hidden 속성 제거
            tr.removeAttribute('hidden');

            // 수량을 1로 설정
            let quantityInput = document.getElementById('point_100_Quantity');
            quantityInput.value = 1;

            // 초기 단가를 가져와서 총액 업데이트
            let price = parseInt(document.getElementById('point_100_price').textContent, 10);
            document.getElementById('point_100_totalAmount').innerHTML = `<strong>${price}원</strong>`;
        } else {
            // tr이 이미 보이는 상태면 수량 업데이트
            let quantityInput = document.getElementById('point_100_Quantity');
            let quantity = parseInt(quantityInput.value, 10);
            quantityInput.value = quantity + 1;

            // 총액 업데이트
            let totalAmount = price * quantityInput.value;
            document.getElementById('point_100_totalAmount').innerHTML = `<strong>${totalAmount}원</strong>`;
        }
        changeTotalAmount();
    });

    document.getElementById('point_1000_add').addEventListener('click', function () {
        hideEmptyMsgRow();
        // 테이블에 해당 트레이가 있는지 검사
        let tr = document.getElementById('point_1000_tr');
        if (tr.hidden) {
            // hidden 속성 제거
            tr.removeAttribute('hidden');

            // 수량을 1로 설정
            let quantityInput = document.getElementById('point_1000_Quantity');
            quantityInput.value = 1;

            // 초기 단가를 가져와서 총액 업데이트
            let price = parseInt(document.getElementById('point_1000_price').textContent, 10);
            document.getElementById('point_1000_totalAmount').innerHTML = `<strong>${price}원</strong>`;
        } else {
            // tr이 이미 보이는 상태면 수량 업데이트
            let quantityInput = document.getElementById('point_1000_Quantity');
            let quantity = parseInt(quantityInput.value, 10);
            quantityInput.value = quantity + 1;

            // 총액 업데이트
            let totalAmount = price * quantityInput.value;
            document.getElementById('point_1000_totalAmount').innerHTML = `<strong>${totalAmount}원</strong>`;
        }
        changeTotalAmount();
    });


    // 수량 증가 기능
    document.getElementById('point_100_plus').addEventListener('click', function () {
        updateQuantity('point_100', 1);
    });

    // 수량 감소 기능
    document.getElementById('point_100_minus').addEventListener('click', function () {
        updateQuantity('point_100', -1);
    });

    // 1000 포인트 수량 증가
    document.getElementById('point_1000_plus').addEventListener('click', function () {
        updateQuantity('point_1000', 1);
    });

    // 1000 포인트 수량 감소
    document.getElementById('point_1000_minus').addEventListener('click', function () {
        updateQuantity('point_1000', -1);
    });

    // 수량 업데이트 함수
    function updateQuantity(pointId, change) {
        let quantityInputId = pointId + '_Quantity';
        let quantityInput = document.getElementById(quantityInputId);
        let quantity = parseInt(quantityInput.value, 10) + change;

        // 수량이 0 이하로 내려가지 않도록 조절
        if (quantity < 1) quantity = 1;
        quantityInput.value = quantity;

        // 총액 업데이트
        let pricePerUnit = parseInt(document.getElementById(pointId + '_price').textContent, 10);
        let totalAmount = pricePerUnit * quantity;
        document.getElementById(pointId + '_totalAmount').innerHTML = `<strong>${totalAmount}원</strong>`;
        changeTotalAmount();
    }


    function showEmptyMessage() {
        let emptyMsg = document.getElementById('emptyMsgRow');
        if (emptyMsg.hidden) {
            emptyMsg.removeAttribute('hidden');
        }
    }


    // 상품 100 포인트 삭제 버튼 이벤트 리스너 추가
    document.getElementById('point_100_delete').addEventListener('click', function () {
        hideRow('point_100_tr');
        changeTotalAmount();
    });

    // 상품 1000 포인트 삭제 버튼 이벤트 리스너 추가
    document.getElementById('point_1000_delete').addEventListener('click', function () {
        hideRow('point_1000_tr');
        changeTotalAmount();
    });


    // 로우 숨김 함수
    function hideRow(rowId) {
        let row = document.getElementById(rowId);
        if (row) {
            row.hidden = true; // 상품 로우를 숨깁니다.
            checkTableContent(); // 상품 로우를 숨긴 후 테이블 상태를 재확인합니다.
        }
    }

    // 테이블 상태 확인 및 메시지 업데이트 함수
    function checkTableContent() {
        var tbody = document.querySelector('.table tbody');
        // 테이블에 visible 상태인 상품 행이 있는지 확인
        var hasVisibleItems = tbody.querySelectorAll('tr:not([hidden])').length > 0; // emptyMsgRow와 최소 한 개의 상품 행이 있는지 확인

        if (!hasVisibleItems) {
            showEmptyMessage(); // 상품이 없으면 비어있음 메시지를 보여줍니다.
        } else {
            hideEmptyMsgRow(); // 상품이 있으면 비어있음 메시지를 숨깁니다.
        }
    }

    let tr100 = document.getElementById('point_100_tr');
    let tr1000 = document.getElementById('point_1000_tr');

    function changeTotalAmount() {
        let totalAmountValue = 0;

        // point_100_tr의 숨김 상태를 확인하고 값 추출
        if (!tr100.hidden) {
            let quantity100 = document.getElementById('point_100_Quantity').value;
            totalAmountValue += (quantity100 * 110);
        }

        // point_1000_tr의 숨김 상태를 확인하고 값 추출
        if (!tr1000.hidden) {
            let quantity1000 = document.getElementById('point_1000_Quantity').value;
            totalAmountValue += (quantity1000 * 1000);
        }

        // 총액을 표시
        document.getElementById("totalAmount").innerHTML = totalAmountValue + "원";
    }


    document.getElementById('payingBtn').addEventListener('click', async function () {
        let quantity100 = 0;
        let quantity1000 = 0;
        const selectedPaymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;

        if (!tr100.hidden) {
            quantity100 = document.getElementById('point_100_Quantity').value;
        }
        if (!tr1000.hidden) {
            quantity1000 = document.getElementById('point_1000_Quantity').value;
        }

        try {
            const response = await fetch('/api/games/user_exit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeaderName]: csrfToken
                },
                body: JSON.stringify({
                    point_100_Quantity: quantity100,
                    point_1000_Quantity: quantity1000,
                    paymentMethod: selectedPaymentMethod
                }),
            });

            const data = await response.json();
            if (data.redirectUrl) {
                setupBeforeUnloadListener(false); // 경고 비활성화
                console.log("data.redirectUrl " + data.redirectUrl);
                window.location.href = data.redirectUrl; // 서버에서 받은 URL로 페이지 리디렉션
            }
        } catch (error) {
            console.error('Error:', error);
        }
    });


});