const state = {
    products: [],
    cart: new Map(),
    loading: false,
    submitting: false,
};
const elements = {
    productGrid: document.getElementById('productGrid'),
    loadingState: document.getElementById('loadingState'),
    errorState: document.getElementById('errorState'),
    productSummary: document.getElementById('productSummary'),
    cartItems: document.getElementById('cartItems'),
    cartCount: document.getElementById('cartCount'),
    cartTotal: document.getElementById('cartTotal'),
    clearCartBtn: document.getElementById('clearCartBtn'),
    checkoutForm: document.getElementById('checkoutForm'),
    submitOrderBtn: document.getElementById('submitOrderBtn'),
    messageBox: document.getElementById('messageBox'),
    orderResult: document.getElementById('orderResult'),
    reloadBtn: document.getElementById('reloadBtn'),
    customerName: document.getElementById('customerName'),
    phone: document.getElementById('phone'),
    address: document.getElementById('address'),
};
const currencyFormatter = new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
});
function formatCurrency(value) {
    return currencyFormatter.format(Number(value || 0));
}
function getProductById(productId) {
    return state.products.find((product) => product.id === productId);
}
function getCartQuantity(productId) {
    return state.cart.get(productId) || 0;
}
function setMessage(type, message) {
    elements.messageBox.textContent = message;
    elements.messageBox.className = `message-box ${type}`;
}
function hideMessage() {
    elements.messageBox.className = 'message-box hidden';
    elements.messageBox.textContent = '';
}
function showOrderResult(order) {
    const itemsHtml = order.items.map((item) => `<li>${item.productName} x ${item.quantity} - ${formatCurrency(item.lineTotal)}</li>`).join('');
    elements.orderResult.innerHTML = `
        <h3>Đặt hàng thành công</h3>
        <p><strong>Mã đơn:</strong> #${order.id}</p>
        <p><strong>Khách hàng:</strong> ${escapeHtml(order.customerName)}</p>
        <p><strong>Tổng tiền:</strong> ${formatCurrency(order.totalAmount)}</p>
        <p><strong>Thời gian:</strong> ${new Date(order.createdAt).toLocaleString('vi-VN')}</p>
        <ul>${itemsHtml}</ul>
    `;
    elements.orderResult.classList.remove('hidden');
}
function clearOrderResult() {
    elements.orderResult.classList.add('hidden');
    elements.orderResult.innerHTML = '';
}
function renderProducts() {
    elements.loadingState.classList.toggle('hidden', !state.loading);
    elements.productGrid.innerHTML = '';
    if (state.loading) return;
    if (!state.products.length) {
        elements.productSummary.textContent = 'Không có sản phẩm nào.';
        elements.productGrid.innerHTML = '<div class="state-card">Chưa có dữ liệu sản phẩm.</div>';
        return;
    }
    const availableCount = state.products.filter((product) => product.stock > 0).length;
    elements.productSummary.textContent = `${state.products.length} sản phẩm, ${availableCount} sản phẩm còn hàng.`;
    elements.productGrid.innerHTML = state.products.map((product) => {
        const cartQuantity = getCartQuantity(product.id);
        const remainingStock = Math.max(product.stock - cartQuantity, 0);
        const stockClass = remainingStock === 0 ? 'out' : remainingStock <= 5 ? 'low' : '';
        return `
            <article class="product-card">
                <div>
                    <h3>${escapeHtml(product.name)}</h3>
                    <p class="product-description">${escapeHtml(product.description || 'Chưa có mô tả')}</p>
                </div>
                <div class="price-row">
                    <span class="price">${formatCurrency(product.price)}</span>
                    <span class="stock ${stockClass}">Còn ${remainingStock}</span>
                </div>
                <div class="cart-row">
                    <div class="qty-controls">
                        <button class="qty-btn" type="button" data-action="decrease" data-product-id="${product.id}">-</button>
                        <span class="qty-value">${cartQuantity}</span>
                        <button class="qty-btn" type="button" data-action="increase" data-product-id="${product.id}" ${remainingStock === 0 ? 'disabled' : ''}>+</button>
                    </div>
                    <button class="primary-btn" type="button" data-action="add" data-product-id="${product.id}" ${remainingStock === 0 ? 'disabled' : ''}>Thêm vào giỏ</button>
                </div>
            </article>
        `;
    }).join('');
}
function renderCart() {
    const cartEntries = Array.from(state.cart.entries())
        .map(([productId, quantity]) => ({ product: getProductById(productId), quantity }))
        .filter((entry) => entry.product && entry.quantity > 0);
    const totalCount = cartEntries.reduce((sum, entry) => sum + entry.quantity, 0);
    const totalPrice = cartEntries.reduce((sum, entry) => sum + Number(entry.product.price) * entry.quantity, 0);
    elements.cartCount.textContent = String(totalCount);
    elements.cartTotal.textContent = formatCurrency(totalPrice);
    elements.clearCartBtn.disabled = cartEntries.length === 0;
    if (!cartEntries.length) {
        elements.cartItems.className = 'cart-list empty';
        elements.cartItems.textContent = 'Chưa có sản phẩm nào trong giỏ.';
        return;
    }
    elements.cartItems.className = 'cart-list';
    elements.cartItems.innerHTML = cartEntries.map(({ product, quantity }) => `
        <article class="cart-item">
            <div class="cart-item-header">
                <div>
                    <h4>${escapeHtml(product.name)}</h4>
                    <p>${formatCurrency(product.price)} / sản phẩm</p>
                </div>
                <strong>${formatCurrency(Number(product.price) * quantity)}</strong>
            </div>
            <div class="meta-row">
                <div class="qty-controls">
                    <button class="qty-btn" type="button" data-action="decrease" data-product-id="${product.id}">-</button>
                    <span class="qty-value">${quantity}</span>
                    <button class="qty-btn" type="button" data-action="increase" data-product-id="${product.id}" ${quantity >= product.stock ? 'disabled' : ''}>+</button>
                </div>
                <button class="ghost-btn" type="button" data-action="remove" data-product-id="${product.id}">Xóa</button>
            </div>
        </article>
    `).join('');
}
function updateCart(productId, nextQuantity) {
    const product = getProductById(productId);
    if (!product) return;
    if (nextQuantity <= 0) {
        state.cart.delete(productId);
    } else {
        state.cart.set(productId, Math.min(nextQuantity, product.stock));
    }
    renderProducts();
    renderCart();
}
function handleProductAction(event) {
    const button = event.target.closest('[data-action]');
    if (!button) return;
    const action = button.dataset.action;
    const productId = Number(button.dataset.productId);
    const currentQuantity = getCartQuantity(productId);
    if (action === 'add' || action === 'increase') updateCart(productId, currentQuantity + 1);
    if (action === 'decrease') updateCart(productId, currentQuantity - 1);
    if (action === 'remove') updateCart(productId, 0);
}
async function loadProducts() {
    state.loading = true;
    hideMessage();
    clearOrderResult();
    elements.errorState.classList.add('hidden');
    elements.loadingState.classList.remove('hidden');
    renderProducts();
    try {
        const response = await fetch('/api/products', { headers: { Accept: 'application/json' } });
        if (!response.ok) throw new Error(`Không thể tải sản phẩm. HTTP ${response.status}`);
        state.products = await response.json();
        for (const [productId, quantity] of Array.from(state.cart.entries())) {
            const product = getProductById(productId);
            if (!product || product.stock <= 0) {
                state.cart.delete(productId);
            } else if (quantity > product.stock) {
                state.cart.set(productId, product.stock);
            }
        }
    } catch (error) {
        state.products = [];
        elements.errorState.textContent = error.message || 'Có lỗi khi tải dữ liệu.';
        elements.errorState.classList.remove('hidden');
    } finally {
        state.loading = false;
        renderProducts();
        renderCart();
    }
}
function buildOrderPayload() {
    const items = Array.from(state.cart.entries())
        .filter(([, quantity]) => quantity > 0)
        .map(([productId, quantity]) => ({ productId, quantity }));
    return {
        customerName: elements.customerName.value.trim(),
        phone: elements.phone.value.trim(),
        address: elements.address.value.trim(),
        items,
    };
}
function readApiError(payload, fallbackMessage) {
    if (!payload) return fallbackMessage;
    if (typeof payload.message === 'string' && payload.message.trim()) {
        if (payload.fieldErrors && typeof payload.fieldErrors === 'object') {
            const details = Object.values(payload.fieldErrors).join(' | ');
            return `${payload.message}: ${details}`;
        }
        return payload.message;
    }
    return fallbackMessage;
}
async function submitOrder(event) {
    event.preventDefault();
    const payload = buildOrderPayload();
    if (!payload.items.length) {
        setMessage('error', 'Giỏ hàng đang trống. Hãy thêm ít nhất 1 sản phẩm.');
        clearOrderResult();
        return;
    }
    state.submitting = true;
    elements.submitOrderBtn.disabled = true;
    elements.submitOrderBtn.textContent = 'Đang gửi đơn...';
    hideMessage();
    clearOrderResult();
    try {
        const response = await fetch('/api/orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
            body: JSON.stringify(payload),
        });
        const responseBody = await response.json().catch(() => null);
        if (!response.ok) throw new Error(readApiError(responseBody, `Đặt hàng thất bại. HTTP ${response.status}`));
        setMessage('success', `Tạo đơn hàng thành công cho ${responseBody.customerName}.`);
        showOrderResult(responseBody);
        state.cart.clear();
        elements.checkoutForm.reset();
        await loadProducts();
    } catch (error) {
        setMessage('error', error.message || 'Không thể tạo đơn hàng.');
    } finally {
        state.submitting = false;
        elements.submitOrderBtn.disabled = false;
        elements.submitOrderBtn.textContent = 'Đặt hàng ngay';
        renderCart();
    }
}
function clearCart() {
    state.cart.clear();
    renderProducts();
    renderCart();
    hideMessage();
    clearOrderResult();
}
function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}
elements.productGrid.addEventListener('click', handleProductAction);
elements.cartItems.addEventListener('click', handleProductAction);
elements.checkoutForm.addEventListener('submit', submitOrder);
elements.clearCartBtn.addEventListener('click', clearCart);
elements.reloadBtn.addEventListener('click', loadProducts);
loadProducts();
