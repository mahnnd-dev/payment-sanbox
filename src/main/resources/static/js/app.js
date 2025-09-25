// app.js
document.addEventListener('DOMContentLoaded', function () {
    const card = {card: "0000000000000000000", nameCard: "NGUYEN DUC MANH", cardDate: "09/25"};

    const banks = [
        {code: 'ABBANK', name: 'ABBANK', logo: '/images/banks/abbank.svg'},
        {code: 'ACB', name: 'ACB', logo: '/images/banks/acb.svg'},
        {code: 'AGR', name: 'Agribank', logo: '/images/banks/agribank.svg'},
        {code: 'BAC', name: 'BacABank', logo: '/images/banks/bacabank.svg'},
        {code: 'BVB', name: 'BaovietBank', logo: '/images/banks/baovietbank.svg'},
        {code: 'BIDV', name: 'BIDV', logo: '/images/banks/bidv.svg'},
        {code: 'COOP', name: 'CoopBank', logo: '/images/banks/coopbank.svg'},
        {code: 'EXI', name: 'Eximbank', logo: '/images/banks/eximbank.svg'},
        {code: 'GPB', name: 'GPBank', logo: '/images/banks/gpbank.svg'},
        {code: 'HDB', name: 'HDBank', logo: '/images/banks/hdbank.svg'},
        {code: 'IVB', name: 'IVB', logo: '/images/banks/ivb.svg'},
        {code: 'JCB', name: 'JCB', logo: '/images/banks/jcb.svg'},
        {code: 'KLB', name: 'KienlongBank', logo: '/images/banks/kienlongbank.svg'},
        {code: 'MASTER', name: 'MasterCard', logo: '/images/banks/master.svg'},
        {code: 'MB', name: 'MBBank', logo: '/images/banks/mbbank.svg'},
        {code: 'MSB', name: 'MSB', logo: '/images/banks/msb.svg'},
        {code: 'NAMA', name: 'NamABank', logo: '/images/banks/namabank.svg'},
        {code: 'NCB', name: 'NCB', logo: '/images/banks/ncb.svg'},
        {code: 'OCB', name: 'OCB', logo: '/images/banks/ocb.svg'},
        {code: 'PB', name: 'PublicBank', logo: '/images/banks/publicbank.svg'},
        {code: 'PVCB', name: 'PVcomBank', logo: '/images/banks/pvcombank.svg'},
        {code: 'SACOM', name: 'Sacombank', logo: '/images/banks/sacombank.svg'},
        {code: 'SGB', name: 'SaigonBank', logo: '/images/banks/saigonbank.svg'},
        {code: 'SCB', name: 'SCB', logo: '/images/banks/scb.svg'},
        {code: 'SEAB', name: 'SeABank', logo: '/images/banks/seabank.svg'},
        {code: 'SHB', name: 'SHB', logo: '/images/banks/shb.svg'},
        {code: 'UNION', name: 'UnionPay', logo: '/images/banks/unionpay.svg'},
        {code: 'VISA', name: 'Visa', logo: '/images/banks/visa.svg'}
    ];


    let selectedBank = null;
    let orderInfo = {}; // Lưu thông tin đơn hàng

    const paymentMethodsSection = document.getElementById('paymentMethodsSection');
    const cardForm = document.getElementById('cardForm');

    function init() {
        loadOrderInfo(); // Load thông tin đơn hàng từ hidden fields
        setupPaymentMethods();
        setupBankGrid();
        setupCardForm();
        setupNavigation();
        showPaymentMethods(); // Start at the first step
    }

    // Load thông tin đơn hàng từ hidden fields hoặc từ server
    function loadOrderInfo() {
        // {
        //     "neo_TxnRef": "df67a512-5ab6-408c-8240-2490d82b5f6f",
        //     "neo_TmnCode": "A21558QU",
        //     "neo_OrderInfo": "233|dsdsd|1|1|18|1|1|https://votephi.voting.vn/detail/17147",
        //     "neo_Amount": "10000000",
        //     "neo_IpAddr": "172.30.32.1",
        //     "neo_Locale": "vi-VN",
        //     "neo_OrderType": "billpayment",
        //     "neo_CurrCode": "VND",
        //     "neo_CreateDate": "20250925085806",
        //     "neo_ExpireDate": "20250925091306",
        //     "neo_Command": "pay",
        //     "neo_ReturnUrl": "http://localhost:9001/api/neo/call-back",
        //     "neo_SecureHash": "bbfe977e0f04b69302238a0874afbb0920a3d0e68c8c98bfee67572c855cabc75374da2b68c7bf129d4a6564ae2586d3813119ca9427afd85b0c0349b4fa859e",
        //     "neo_Version": "2.1.0"
        // }
        const request = JSON.parse(document.getElementById("paymentData").dataset.request);
        console.log(request);
        // nhà cung cấp
        const tmnCodeElement = request.neo_TmnCode;
        // mã đơn hàng
        const txnRefElement = request.neo_TxnRef;
        // giá trị đơn hàng
        const amountElement = request.neo_Amount;
        // số tiền thanh toán
        const amountPayElement = request.neo_Amount;
        // định dạng số tiền
        const formatted = new Intl.NumberFormat(request.neo_Locale, {
            style: 'currency',
            currency: request.neo_CurrCode
        }).format(amountElement);
        // gán giá trị vào id
        document.getElementById("tmnCode").textContent = "#" + tmnCodeElement;
        document.getElementById("txnRef").textContent = "#" + txnRefElement;
        document.getElementById("amount").textContent = "#" + formatted;
        document.getElementById("amountPay").textContent = "#" + formatted;

        if (txnRefElement || amountElement || tmnCodeElement) {
            orderInfo = {
                txnRef: request.neo_TxnRef,
                tmnCode: request.neo_TmnCode,
                orderInfo: request.neo_OrderInfo,
                amount: request.neo_Amount,
                amountPay: request.neo_Amount,             // đã format currency
                ipAddr: request.neo_IpAddr,
                locale: request.neo_Locale,
                orderType: request.neo_OrderType,
                currCode: request.neo_CurrCode,
                createDate: request.neo_CreateDate,
                expireDate: request.neo_ExpireDate,
                command: request.neo_Command,
                returnUrl: request.neo_ReturnUrl,
                secureHash: request.neo_SecureHash,
                version: request.neo_Version
            };
        }

        // Log để debug
        console.log('Order Info Loaded:', orderInfo);
    }

    function setupPaymentMethods() {
        document.querySelectorAll('.payment-method').forEach(method => {
            const header = method.querySelector('.payment-method-header');
            if (!header) return;

            header.addEventListener('click', function () {
                const methodType = method.dataset.method;

                if (methodType === 'domestic_card') {
                    // Đóng tất cả các accordion khác
                    document.querySelectorAll('.payment-method.expanded').forEach(expandedMethod => {
                        if (expandedMethod !== method) {
                            expandedMethod.classList.remove('expanded');
                            expandedMethod.classList.remove('selected');
                        }
                    });
                    // Mở hoặc đóng accordion hiện tại
                    method.classList.toggle('expanded');
                    method.classList.toggle('selected');
                } else {
                    // Logic cho các phương thức khác
                    toastr.info(`Chức năng "${method.querySelector('.payment-method-text').innerText.trim()}" đang được phát triển.`);
                }
            });
        });
    }

    function setupBankGrid() {
        const domesticBankGrid = document.getElementById('domesticBankGrid');
        if (!domesticBankGrid) return;

        domesticBankGrid.innerHTML = '';
        banks.forEach(bank => {
            const bankItem = document.createElement('div');
            bankItem.className = 'bank-item';
            bankItem.dataset.bankCode = bank.code;
            bankItem.innerHTML = `<img class="bank-logo" src="${bank.logo}" alt="${bank.name}">`;

            bankItem.addEventListener('click', (event) => {
                event.stopPropagation();
                selectBank(bank);
            });
            domesticBankGrid.appendChild(bankItem);
        });
    }

    function selectBank(bank) {
        selectedBank = bank;
        console.log('Selected Bank:', selectedBank);
        showCardForm();
    }

    function setupCardForm() {
        const cardPaymentForm = document.getElementById('cardPaymentForm');
        if (!cardPaymentForm) return;

        cardPaymentForm.addEventListener('submit', function (e) {
            e.preventDefault();
            const formData = {
                bank: selectedBank?.name,
                cardNumber: document.getElementById('cardNumber')?.value,
                cardHolder: document.getElementById('cardHolder')?.value,
                cardDate: document.getElementById('cardDate')?.value,
                orderInfo: orderInfo // Bao gồm thông tin đơn hàng
            };
            console.log('Payment Data:', formData);
            // Validate form
            if (!formData.cardNumber || !formData.cardHolder || !formData.cardDate) {
                toastr.error('Vui lòng nhập đầy đủ thông tin thẻ.');
                return;
            }
            // Gửi request thanh toán (có thể gọi API ở đây)
            processPayment(formData);
        });

        // Format card number input
        const cardNumberInput = document.getElementById('cardNumber');
        if (cardNumberInput) {
            cardNumberInput.addEventListener('input', function (e) {
                let value = e.target.value.replace(/\D/g, '');
                value = value.replace(/(\d{4})(?=\d)/g, '$1 ');
                e.target.value = value;
            });
        }

        // Format card date input
        const cardDateInput = document.getElementById('cardDate');
        if (cardDateInput) {
            cardDateInput.addEventListener('input', function (e) {
                let value = e.target.value.replace(/\D/g, '');
                if (value.length >= 2) {
                    value = value.substring(0, 2) + '/' + value.substring(2, 4);
                }
                e.target.value = value;
            });
        }
    }

    function processPayment(formData) {
        // Hiển thị loading
        const submitBtn = document.querySelector('.submit-btn');
        const originalText = submitBtn.textContent;
        submitBtn.textContent = 'Đang xử lý...';
        submitBtn.disabled = true;

        // Hiển thị confirm dialog trước khi thanh toán
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                title: 'Xác nhận thanh toán',
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#1976d2',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Xác nhận thanh toán',
                cancelButtonText: 'Hủy',
                didOpen: () => {
                    // Reset button khi mở dialog
                    submitBtn.textContent = originalText;
                    submitBtn.disabled = false;
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    // Thực hiện thanh toán
                    executePaymentFake(formData, submitBtn, originalText);
                }
            });
        } else {
            // Fallback nếu không có SweetAlert2
            const confirmText = `Xác nhận thanh toán?\n\nNgân hàng: ${formData.bank}\nSố tiền: ${formData.orderInfo.amount || 'N/A'}\nMã đơn hàng: ${formData.orderInfo.orderId || 'N/A'}`;
            if (confirm(confirmText)) {
                executePaymentFake(formData, submitBtn, originalText);
            } else {
                submitBtn.textContent = originalText;
                submitBtn.disabled = false;
            }
        }
    }

    function executePaymentFake(formData, submitBtn, originalText) {
        console.log("#executePayment ", formData);

        // Reset trạng thái nút
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;

        if (typeof Swal !== 'undefined') {
            Swal.fire({
                title: 'Kết quả giả lập thanh toán',
                text: 'Chọn trạng thái để lưu vào DB',
                icon: 'info',
                showCancelButton: true,
                showDenyButton: true,
                confirmButtonText: 'Thành công',
                denyButtonText: 'Thất bại',
                cancelButtonText: 'Hủy'
            }).then((result) => {
                console.log("------------------------------", result);
                if (result.isConfirmed) {
                    // Gọi API lưu giao dịch thành công (bảng querydr)
                    saveTransactionResult(formData, true);
                } else if (result.isDenied) {
                    // Gọi API lưu giao dịch thất bại (bảng refund hoặc log lỗi)
                    saveTransactionResult(formData, false);
                }
            });
        } else {
            // Fallback nếu không có SweetAlert2
            const confirmText = "Chọn kết quả thanh toán:\nOK = Thành công\nCancel = Thất bại";
            if (confirm(confirmText)) {
                saveTransactionResult(formData, true);
            } else {
                saveTransactionResult(formData, false);
            }
        }
    }

    function saveTransactionResult(formData, isSuccess) {
        fetch('/api/neo-payment/save-transaction', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                requestId: crypto.randomUUID(),
                version: formData.orderInfo.version,
                command: isSuccess ? "querydr" : "refund",
                tmnCode: formData.orderInfo.tmnCode,
                txnRef: formData.orderInfo.txnRef,
                orderInfo: formData.orderInfo.orderInfo,
                transactionNo: formData.orderInfo.transactionNo,
                transactionDate: formData.orderInfo.transactionDate,
                createDate: new Date().toISOString().replace(/[-:TZ.]/g, '').slice(0, 14),
                ipAddr: window.location.hostname,
                amount: formData.orderInfo.amount,
                amountPay: formData.orderInfo.amountPay,
                bankCode: selectedBank?.code,
                bankName: formData.bank,
                cardNumber: formData.cardNumber?.replace(/\d(?=\d{4})/g, "*"),
                cardHolder: formData.cardHolder,
                cardDate: formData.cardDate,
                refundAmount: isSuccess ? null : formData.orderInfo.amount,
                refundReason: isSuccess ? null : "Thanh toán thất bại",
                status: isSuccess ? "SUCCESS" : "FAILED"
            })
        })
            .then(res => res.json())
            .then(data => {
                Swal.fire({
                    title: isSuccess ? 'Thành công' : 'Thất bại',
                    text: data.message || 'Đã lưu giao dịch',
                    icon: isSuccess ? 'success' : 'error',
                    confirmButtonText: 'Về trang chủ'
                }).then(() => {
                    // Redirect về home sau khi bấm nút
                    window.location.href = "/";
                });
            })
            .catch(err => {
                console.error("Save transaction error:", err);
                Swal.fire({
                    title: 'Lỗi',
                    text: 'Không thể lưu giao dịch',
                    icon: 'error',
                    confirmButtonText: 'Về trang chủ'
                }).then(() => {
                    window.location.href = "/";
                });
            });
    }


    function setupNavigation() {
        const backToMethodsBtn = document.getElementById('backToMethods');
        if (backToMethodsBtn) {
            backToMethodsBtn.addEventListener('click', showPaymentMethods);
        }

        const cancelBtn = document.querySelector('.cancel-btn');
        if (cancelBtn) {
            cancelBtn.addEventListener('click', () => {
                // Sử dụng SweetAlert2 cho thông báo chuyên nghiệp
                if (typeof Swal !== 'undefined') {
                    Swal.fire({
                        title: 'Xác nhận hủy giao dịch',
                        text: 'Bạn có chắc chắn muốn hủy giao dịch này không?',
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#d33',
                        cancelButtonColor: '#3085d6',
                        confirmButtonText: 'Hủy giao dịch',
                        cancelButtonText: 'Tiếp tục thanh toán'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            toastr.info('Giao dịch đã được hủy');
                            showPaymentMethods();
                        }
                    });
                } else {
                    // Fallback nếu SweetAlert2 không có
                    if (confirm('Bạn có chắc chắn muốn hủy giao dịch?')) {
                        toastr.info('Giao dịch đã được hủy');
                        showPaymentMethods();
                    }
                }
            });
        }
    }

    function showPaymentMethods() {
        if (paymentMethodsSection) {
            paymentMethodsSection.classList.remove('hidden');
        }
        if (cardForm) {
            cardForm.classList.remove('active');
        }

        // Đảm bảo accordion được đóng lại khi quay về
        const domesticCard = document.querySelector('.payment-method[data-method="domestic_card"]');
        if (domesticCard) {
            domesticCard.classList.remove('expanded');
            domesticCard.classList.remove('selected');
        }
    }

    function showCardForm() {
        if (selectedBank) {
            const selectedBankNameEl = document.getElementById('selectedBankName');
            if (selectedBankNameEl) {
                selectedBankNameEl.textContent = selectedBank.name;
            }
        }

        if (paymentMethodsSection) {
            paymentMethodsSection.classList.add('hidden');
        }
        if (cardForm) {
            cardForm.classList.add('active');
        }
    }

    // Khởi tạo ứng dụng
    init();
});