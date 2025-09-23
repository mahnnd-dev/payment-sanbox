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
        const orderIdElement = document.getElementById('orderId');
        const amountElement = document.getElementById('amount');
        const supplierElement = document.getElementById('supplierName');

        if (orderIdElement && amountElement && supplierElement) {
            orderInfo = {
                orderId: orderIdElement.value || '#000000',
                amount: amountElement.value || '0 VND',
                supplier: supplierElement.value || 'NEO'
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

        cardPaymentForm.addEventListener('submit', function(e) {
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
            cardNumberInput.addEventListener('input', function(e) {
                let value = e.target.value.replace(/\D/g, '');
                value = value.replace(/(\d{4})(?=\d)/g, '$1 ');
                e.target.value = value;
            });
        }

        // Format card date input
        const cardDateInput = document.getElementById('cardDate');
        if (cardDateInput) {
            cardDateInput.addEventListener('input', function(e) {
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

        // Simulate API call
        setTimeout(() => {
            toastr.success('Thanh toán đang được xử lý!');
            console.log('Payment processed:', formData);

            // Reset button
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;

            // Có thể redirect hoặc show kết quả ở đây
        }, 2000);
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