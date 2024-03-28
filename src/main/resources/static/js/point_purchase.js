document.addEventListener('DOMContentLoaded', function () {

    const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
    const csrfHeaderName = document.querySelector("meta[name='_csrf_header']").getAttribute("content");


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

        //초기 단가
        let price = parseInt(document.getElementById('point_100_price').textContent, 10);

        // 테이블에 해당 트레이가 있는지 검사
        let tr = document.getElementById('point_100_tr');
        if (tr.hidden) {
            // hidden 속성 제거
            tr.removeAttribute('hidden');

            // 수량을 1로 설정
            let quantityInput = document.getElementById('point_100_Quantity');
            quantityInput.value = 1;

            // 초기 단가를 가져와서 총액 업데이트
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

        let price = parseInt(document.getElementById('point_1000_price').textContent, 10);

        // 테이블에 해당 트레이가 있는지 검사
        let tr = document.getElementById('point_1000_tr');
        if (tr.hidden) {
            // hidden 속성 제거
            tr.removeAttribute('hidden');

            // 수량을 1로 설정
            let quantityInput = document.getElementById('point_1000_Quantity');
            quantityInput.value = 1;

            // 초기 단가를 가져와서 총액 업데이트
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
        let totalPoint = 0;

        // point_100_tr의 숨김 상태를 확인하고 값 추출
        if (!tr100.hidden) {
            let quantity100 = document.getElementById('point_100_Quantity').value;
            totalAmountValue += (quantity100 * 110);
            totalPoint += (quantity100 * 100);
        }

        // point_1000_tr의 숨김 상태를 확인하고 값 추출
        if (!tr1000.hidden) {
            let quantity1000 = document.getElementById('point_1000_Quantity').value;
            totalAmountValue += (quantity1000 * 1000);
            totalPoint += (quantity1000 * 1000);
        }

        // 총액을 표시
        document.getElementById("totalAmount").innerHTML = totalAmountValue + "원";
        // 총포인트 표시
        document.getElementById("totalPoint").innerHTML = '🪙 ' + totalPoint + ' 포인트 ';
    }


    function generatePaymentId() {
        const now = new Date();
        const timestamp = now.getTime(); // 현재 시간을 밀리초 단위로 반환
        const randomPart = Math.floor(Math.random() * 1000000); // 0에서 999999 사이의 무작위 숫자
        return `pay-${timestamp}-${randomPart}`;
    }


    const paymentId = generatePaymentId();
    console.log(paymentId); // 예: pay-1615542263721-387463


    document.getElementById('payingBtn').addEventListener('click', async function () {
        let quantity100 = 0;
        let quantity1000 = 0;
        const selectedPaymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;

        // tr100과 tr1000이 정의되어 있지 않습니다. 해당 요소를 얻는 코드가 필요할 수 있습니다.
        if (!tr100.hidden) {
            quantity100 = document.getElementById('point_100_Quantity').value;
        }
        if (!tr1000.hidden) {
            quantity1000 = document.getElementById('point_1000_Quantity').value;
        }

        // 장바구니에 아무 것도 없는 경우
        if (tr100.hidden && tr1000.hidden) {
            alert("장바구니에 포인트가 없습니다.");
            return; // 함수 종료
        }

        const currentPaymentId = generatePaymentId();

        try {
            const response = await fetch('/api/pay/kakaopay', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeaderName]: csrfToken
                },
                body: JSON.stringify({
                    point_100_Quantity: quantity100,
                    point_1000_Quantity: quantity1000,
                    paymentMethod: selectedPaymentMethod,
                    paymentId: currentPaymentId
                }),
            });

            const data = await response.json();
            const paymentPopup = window.open(data.nextRedirectPcUrl, 'paymentPopup', 'width=1000,height=600');

            // 팝업 창이 닫힘을 감지
            const checkPopupClosed = setInterval(() => {
                if (paymentPopup.closed) {
                    clearInterval(checkPopupClosed); // 중복 실행을 방지하기 위해 인터벌을 멈춥니다.
                    window.location.href = '/'; // 메인 페이지(인덱스 페이지)로 리다이렉트
                }
            }, 1000);

        } catch
            (error) {
            console.error('An error occurred:', error);
        }
    });


});


// async function checkPaymentStatusAndRedirect() {
//     try {
//         const response = await fetch('/api/pay/kakaopay', {
//                                         method: 'POST',
//                                         headers: {
//                                             'Content-Type': 'application/json',
//                                             [csrfHeaderName]: csrfToken
//                                         },
//                                         body: JSON.stringify({
//                                             paymentId: currentPaymentId
//                                         }),
//                                     });
//         const resultData = await response.json();
//
//         if (resultData.paymentStatus === 'Success') {
//             // 결제 성공 시, 결제 완료 페이지로 리다이렉션
//             window.location.href = '/point/payment-completed?totalPointAmount=' + resultData.totalPointAmount + '&paymentResult='+resultData.paymentStatus;
//         } else {
//             // 결제 실패 혹은 취소 처리
//             window.location.href = '/point/payment-completed?paymentResult='+resultData.paymentStatus;
//         }
//     } catch (error) {
//         console.error('Error checking payment status:', error);
//     }
// }
//
// // 팝업 창 상태를 주기적으로 확인
// const checkPopupClosed = setInterval(() => {
//     if (paymentPopup.closed) {
//         clearInterval(checkPopupClosed);
//         checkPaymentStatusAndRedirect(); // 결제 완료 여부 확인 및 처리
//     }
// }, 1000);

