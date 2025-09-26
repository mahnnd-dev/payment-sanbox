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
                amountPay: request.neo_Amount,
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
            requestId: crypto.randomUUID(),
            version: formData.orderInfo.version,
            command: getCommandByStatus(statusCode),
            tmnCode: formData.orderInfo.tmnCode,
            txnRef: formData.orderInfo.txnRef,
            orderInfo: formData.orderInfo.orderInfo,
            transactionNo: generateTransactionNo(),
            transactionDate: new Date().toISOString().replace(/[-:TZ.]/g, '').slice(0, 14),
            createDate: new Date().toISOString().replace(/[-:TZ.]/g, '').slice(0, 14),
            ipAddr: window.location.hostname,
            amount: formData.orderInfo.amount,
            amountPay: formData.orderInfo.amountPay,
            bankCode: selectedBank?.code,
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
                    window.location.href = "/";
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
                    window.location.href = "/";
                });
            });
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
                return "refund";
            case TRANSACTION_STATUS.CANCELLED:
                return "cancel";
            default:
                return "error";
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
});