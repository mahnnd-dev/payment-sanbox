// app.js
document.addEventListener('DOMContentLoaded', function () {
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

        // Định nghĩa các trạng thái giao dịch
        const TRANSACTION_STATUS = {
            SUCCESS: '00',    // Thành công
            FAILED: '01',     // Thất bại
            CANCELLED: '02'   // Hủy giao dịch
        };

        let selectedBank = null;
        let orderInfo = {}; // Lưu thông tin đơn hàng

        const paymentMethodsSection = document.getElementById('paymentMethodsSection');
        const cardForm = document.getElementById('cardForm');
        const paymentData = document.getElementById("paymentData");

        function init() {
            loadOrderInfo(); // Load thông tin đơn hàng từ hidden fields
            setupPaymentMethods();
            setupBankGrid();
            setupCardForm();
            setupNavigation();
            showPaymentMethods(); // Start at the first step
            loadPaymentResult();
        }

        function loadPaymentResult() {
            const element = document.getElementById("paymentResult");
            if (element != null) {
                const data = JSON.parse(element.dataset.request);
                console.log("PaymentResult from server:", data);
                document.getElementById("statusTitle").textContent = data.status;
                document.getElementById("statusMessage").textContent = data.message;
            }
        }

        // Load thông tin đơn hàng từ hidden fields hoặc từ server
        function loadOrderInfo() {
            if (!paymentData || !paymentData.dataset.request) {
                console.warn("No paymentData or request dataset found");
                return;
            }
            const request = JSON.parse(paymentData.dataset.request);
            console.log(request);
            // nhà cung cấp
            const tmnCodeElement = request.neo_TmnCode;
            // mã đơn hàng
            const txnRefElement = request.neo_TxnRef;
            // giá trị đơn hàng
            const amountElement = request.neo_Amount;
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
                    version: request.neo_Version,           // vnp_Version
                    command: request.neo_Command,           // vnp_Command
                    tmnCode: request.neo_TmnCode,           // vnp_TmnCode
                    amount: request.neo_Amount,             // vnp_Amount
                    bankCode: request.neo_BankCode,         // vnp_BankCode (tùy chọn)
                    createDate: request.neo_CreateDate,     // vnp_CreateDate
                    currCode: request.neo_CurrCode,         // vnp_CurrCode
                    ipAddr: request.neo_IpAddr,             // vnp_IpAddr
                    locale: request.neo_Locale,             // vnp_Locale
                    orderInfo: request.neo_OrderInfo,       // vnp_OrderInfo
                    orderType: request.neo_OrderType,       // vnp_OrderType
                    returnUrl: request.neo_ReturnUrl,       // vnp_ReturnUrl
                    expireDate: request.neo_ExpireDate,     // vnp_ExpireDate
                    txnRef: request.neo_TxnRef,             // vnp_TxnRef
                    domain: request.domain,             // domain
                    secureHash: request.neo_SecureHash      // vnp_SecureHash
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
            console.log(paymentData);
            if (!paymentData || !paymentData.dataset.request) {
                console.warn("No paymentData or request dataset found");
                return;
            }
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

        // Function để gọi API kiểm tra thẻ hợp lệ
        async function validateCard(formData) {
            try {
                const cleanCardNumber = formData.cardNumber.replace(/\s/g, ''); // Loại bỏ khoảng trắng
                const response = await fetch('/api/neo-payment/validate-card', { // Giả sử endpoint là /api/validate-card, bạn có thể thay đổi
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        cardNumber: cleanCardNumber,
                        cardHolder: formData.cardHolder,
                        cardDate: formData.cardDate
                    })
                });
                if (!response.ok) {
                    throw new Error('API call failed');
                }
                return await response.json();
            } catch (error) {
                console.error('Validation error:', error);
                toastr.error('Lỗi khi kiểm tra thẻ. Vui lòng thử lại.');
                return false;
            }
        }

        function setupCardForm() {
            const cardPaymentForm = document.getElementById('cardPaymentForm');
            if (!cardPaymentForm) return;

            cardPaymentForm.addEventListener('submit', async function (e) { // Làm async để await validate
                e.preventDefault();
                const formData = {
                    bank: selectedBank?.name,
                    cardNumber: document.getElementById('cardNumber')?.value,
                    cardHolder: document.getElementById('cardHolder')?.value,
                    cardDate: document.getElementById('cardDate')?.value,
                    orderInfo: orderInfo // Bao gồm thông tin đơn hàng
                };
                console.log('Payment Data:', formData);
                // Validate form cơ bản
                if (!formData.cardNumber || !formData.cardHolder || !formData.cardDate) {
                    toastr.error('Vui lòng nhập đầy đủ thông tin thẻ.');
                    return;
                }
                // Gọi API kiểm tra thẻ hợp lệ
                const isValid = await validateCard(formData);
                console.log(isValid);

                if (!isValid.valid) {
                    toastr.error(isValid.message);
                    return;
                }
                // Nếu hợp lệ, tiếp tục xử lý thanh toán
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
                        executePaymentSimulation(formData, submitBtn, originalText);
                    } else {
                        // User đã hủy giao dịch
                        saveTransactionResult(formData, TRANSACTION_STATUS.CANCELLED);
                    }
                });
            } else {
                // Fallback nếu không có SweetAlert2
                const confirmText = `Xác nhận thanh toán?\n\nNgân hàng: ${formData.bank}\nSố tiền: ${formData.orderInfo.amount || 'N/A'}\nMã đơn hàng: ${formData.orderInfo.orderId || 'N/A'}`;
                if (confirm(confirmText)) {
                    executePaymentSimulation(formData, submitBtn, originalText);
                } else {
                    submitBtn.textContent = originalText;
                    submitBtn.disabled = false;
                    saveTransactionResult(formData, TRANSACTION_STATUS.CANCELLED);
                }
            }
        }

        function executePaymentSimulation(formData, submitBtn, originalText) {
            console.log("#executePaymentSimulation ", formData);

            // Reset trạng thái nút
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;

            if (typeof Swal !== 'undefined') {
                Swal.fire({
                    title: 'Mô phỏng kết quả thanh toán',
                    text: 'Chọn trạng thái để lưu vào CSDL',
                    icon: 'info',
                    showCancelButton: true,
                    showDenyButton: true,
                    confirmButtonText: 'Thành công (00)',
                    denyButtonText: 'Thất bại (01)',
                    cancelButtonText: 'Hủy giao dịch (02)',
                    confirmButtonColor: '#28a745',
                    denyButtonColor: '#dc3545',
                    cancelButtonColor: '#6c757d'
                }).then((result) => {
                    if (result.isConfirmed) {
                        // Trạng thái 00: Thành công
                        saveTransactionResult(formData, TRANSACTION_STATUS.SUCCESS);
                    } else if (result.isDenied) {
                        // Trạng thái 01: Thất bại
                        saveTransactionResult(formData, TRANSACTION_STATUS.FAILED);
                    } else {
                        // Trạng thái 02: Hủy giao dịch
                        saveTransactionResult(formData, TRANSACTION_STATUS.CANCELLED);
                    }
                });
            } else {
                // Fallback nếu không có SweetAlert2
                const choice = prompt("Chọn trạng thái:\n00 = Thành công\n01 = Thất bại\n02 = Hủy giao dịch", "00");
                if (choice === "00") {
                    saveTransactionResult(formData, TRANSACTION_STATUS.SUCCESS);
                } else if (choice === "01") {
                    saveTransactionResult(formData, TRANSACTION_STATUS.FAILED);
                } else {
                    saveTransactionResult(formData, TRANSACTION_STATUS.CANCELLED);
                }
            }
        }

        function saveTransactionResult(formData, statusCode) {
            const statusInfo = getStatusInfo(statusCode);

            const transactionData = {
                // mã tự sinh từ hệ thống
                requestId: crypto.randomUUID(),
                version: formData.orderInfo.version,
                command: getCommandByStatus(statusCode),
                tmnCode: formData.orderInfo.tmnCode,
                txnRef: formData.orderInfo.txnRef,
                orderInfo: formData.orderInfo.orderInfo,
                transactionNo: generateTransactionNo(),
                transactionDate: getDate(),
                ipAddr: formData.orderInfo.ipAddr,
                returnUrl: formData.orderInfo.returnUrl,
                domain: formData.orderInfo.domain,
                amount: formData.orderInfo.amount,
                bankCode: formData.orderInfo.bankCode,
                createDate: formData.orderInfo.createDate,
                currCode: formData.orderInfo.currCode,
                locale: formData.orderInfo.locale,
                orderType: formData.orderInfo.orderType,
                expireDate: formData.orderInfo.expireDate,
                bankName: formData.bank,
                cardNumber: formData.cardNumber?.replace(/\d(?=\d{4})/g, "*"),
                cardHolder: formData.cardHolder,
                cardDate: formData.cardDate,
                refundAmount: statusCode !== TRANSACTION_STATUS.SUCCESS ? formData.orderInfo.amount : null,
                refundReason: statusCode !== TRANSACTION_STATUS.SUCCESS ? statusInfo.reason : null,
                status: statusCode,
                statusMessage: statusInfo.message
            };
            console.log('Sending transaction data:', transactionData);
            fetch('/api/neo-payment/save-transaction', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(transactionData)
            })
                .then(res => res.json())
                .then(data => {
                    Swal.fire({
                        title: statusInfo.title,
                        text: data.message || statusInfo.message,
                        icon: statusInfo.icon,
                        confirmButtonText: 'Về trang chủ'
                    }).then(() => {
                        // Redirect về home sau khi bấm nút
                        window.location.href = data.url;
                    });
                })
                .catch(err => {
                    console.error("Save transaction error:", err);
                    Swal.fire({
                        title: 'Lỗi hệ thống',
                        text: 'Không thể lưu giao dịch vào CSDL',
                        icon: 'error',
                        confirmButtonText: 'Về trang chủ'
                    }).then(() => {
                        window.location.href = data.url;
                    });
                });
        }


        function getDate() {
            const transactionDate = new Date();
            return transactionDate.toLocaleString('en-GB', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit',
                hour12: false
            }).replace(',', '');
        }

        function getStatusInfo(statusCode) {
            switch (statusCode) {
                case TRANSACTION_STATUS.SUCCESS:
                    return {
                        title: 'Thanh toán thành công',
                        message: 'Giao dịch đã được xử lý thành công',
                        icon: 'success',
                        reason: null
                    };
                case TRANSACTION_STATUS.FAILED:
                    return {
                        title: 'Thanh toán thất bại',
                        message: 'Giao dịch không thành công',
                        icon: 'error',
                        reason: 'Lỗi trong quá trình xử lý thanh toán'
                    };
                case TRANSACTION_STATUS.CANCELLED:
                    return {
                        title: 'Giao dịch bị hủy',
                        message: 'Người dùng đã hủy giao dịch',
                        icon: 'warning',
                        reason: 'Người dùng hủy giao dịch'
                    };
                default:
                    return {
                        title: 'Trạng thái không xác định',
                        message: 'Trạng thái giao dịch không hợp lệ',
                        icon: 'question',
                        reason: 'Trạng thái không xác định'
                    };
            }
        }

        function getCommandByStatus(statusCode) {
            switch (statusCode) {
                case TRANSACTION_STATUS.SUCCESS:
                    return "pay";
                case TRANSACTION_STATUS.FAILED:
                    return "error";
                case TRANSACTION_STATUS.CANCELLED:
                    return "cancel";
                default:
                    return "unknown";
            }
        }

        function generateTransactionNo() {
            return Date.now().toString() + Math.random().toString(36).substr(2, 5);
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
                                // Lưu trạng thái hủy vào CSDL
                                const formData = {
                                    bank: selectedBank?.name,
                                    orderInfo: orderInfo
                                };
                                saveTransactionResult(formData, TRANSACTION_STATUS.CANCELLED);
                            }
                        });
                    } else {
                        // Fallback nếu SweetAlert2 không có
                        if (confirm('Bạn có chắc chắn muốn hủy giao dịch?')) {
                            const formData = {
                                bank: selectedBank?.name,
                                orderInfo: orderInfo
                            };
                            saveTransactionResult(formData, TRANSACTION_STATUS.CANCELLED);
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
    }
);