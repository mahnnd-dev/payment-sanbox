document.addEventListener('DOMContentLoaded', async function () {
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

    const paymentMethodsSection = document.getElementById('paymentMethodsSection');
    const cardForm = document.getElementById('cardForm');

    // Load templates từ server
    const methodsResponse = await fetch('/payment/methods');
    paymentMethodsSection.innerHTML = await methodsResponse.text();

    const formResponse = await fetch('/payment/form');
    cardForm.innerHTML = await formResponse.text();

    init();

    // ===================== INIT =====================
    function init() {
        setupPaymentMethods();
        setupBankGrid();
        setupCardForm();
        setupNavigation();
        showPaymentMethods();
    }

    // ===================== PAYMENT METHODS =====================
    function setupPaymentMethods() {
        document.querySelectorAll('.payment-method').forEach(method => {
            const header = method.querySelector('.payment-method-header');
            if (!header) return;

            header.addEventListener('click', function () {
                const methodType = method.dataset.method;

                if (methodType === 'domestic_card') {
                    // Accordion toggle
                    document.querySelectorAll('.payment-method.expanded').forEach(m => {
                        if (m !== method) m.classList.remove('expanded', 'selected');
                    });
                    method.classList.toggle('expanded');
                    method.classList.toggle('selected');
                } else {
                    toastr.warning(
                        `Chức năng "${method.querySelector('.payment-method-text').innerText.trim()}" đang phát triển.`
                    );
                }
            });
        });
    }

    // ===================== BANK GRID =====================
    function setupBankGrid() {
        const domesticBankGrid = document.getElementById('domesticBankGrid');
        domesticBankGrid.innerHTML = '';
        banks.forEach(bank => {
            const bankItem = document.createElement('div');
            bankItem.className = 'bank-item';
            bankItem.dataset.bankCode = bank.code;
            bankItem.innerHTML = `<img class="bank-logo" src="${bank.logo}" alt="${bank.name}">`;

            bankItem.addEventListener('click', (e) => {
                e.stopPropagation();
                selectBank(bank);
            });

            domesticBankGrid.appendChild(bankItem);
        });
    }

    function selectBank(bank) {
        selectedBank = bank;
        showCardForm();
    }

    // ===================== CARD FORM =====================
    function setupCardForm() {
        const form = document.getElementById('cardPaymentForm');
        if (!form) return;

        form.addEventListener('submit', e => {
            e.preventDefault();
            const formData = {
                bank: selectedBank?.name,
                cardNumber: document.getElementById('cardNumber').value,
                cardHolder: document.getElementById('cardHolder').value,
            };

            toastr.info('Đang xử lý thanh toán...');

            // Giả lập call API 3s
            setTimeout(() => {
                toastr.success(`Thanh toán thành công qua ${formData.bank}!`);
                showPaymentMethods();
            }, 3000);
        });
    }

    // ===================== NAVIGATION =====================
    function setupNavigation() {
        const backBtn = document.getElementById('backToMethods');
        if (backBtn) {
            backBtn.addEventListener('click', () => {
                showPaymentMethods();
                toastr.info('Đã quay lại màn hình chọn phương thức');
            });
        }

        const cancelBtn = document.querySelector('.cancel-btn');
        if (cancelBtn) {
            cancelBtn.addEventListener('click', () => {
                Swal.fire({
                    title: 'Bạn có chắc chắn muốn hủy giao dịch?',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Có, hủy ngay',
                    cancelButtonText: 'Không'
                }).then((result) => {
                    if (result.isConfirmed) {
                        showPaymentMethods();
                        toastr.warning('Giao dịch đã bị hủy');
                    }
                });
            });
        }
    }

    // ===================== UI CONTROL =====================
    function showPaymentMethods() {
        paymentMethodsSection.classList.remove('hidden');
        cardForm.classList.add('hidden');
        const domesticCard = document.querySelector('.payment-method[data-method="domestic_card"]');
        if (domesticCard) {
            domesticCard.classList.remove('expanded', 'selected');
        }
    }

    function showCardForm() {
        if (selectedBank) {
            const bankNameEl = document.getElementById('selectedBankName');
            if (bankNameEl) {
                bankNameEl.textContent = selectedBank.name;
            }
        }
        paymentMethodsSection.classList.add('hidden');
        cardForm.classList.remove('hidden');
        cardForm.classList.add('active');
    }

    // ===================== TOASTR CONFIG =====================
    toastr.options = {
        "closeButton": true,
        "progressBar": true,
        "positionClass": "toast-top-right",
        "timeOut": "3000"
    };
});
