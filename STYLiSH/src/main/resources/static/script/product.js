// TapPay SDK
const APP_ID = '12348';
const APP_KEY = 'app_pa1pQcKoY22IlnSXq5m5WP5jFKzoRG58VEXpT7wU62ud7mMbDOGzCYIlzzLF';
TPDirect.setupSDK(APP_ID, APP_KEY, 'sandbox');

document.addEventListener('DOMContentLoaded', function () {
    const fields = {
        number: {
            element: '#card-number',
            placeholder: '**** **** **** ****'
        },
        expirationDate: {
            element: '#card-expiration-date',
            placeholder: 'MM / YY'
        },
        ccv: {
            element: '#card-ccv',
            placeholder: 'ccv'
        }
    };

    const styles = {
        'input': {
            'color': 'gray'
        },
        ':focus': {
            'color': 'black'
        },
        '.valid': {
            'color': 'green'
        },
        '.invalid': {
            'color': 'red'
        }
    };

    TPDirect.card.setup({
        fields: fields,
        styles: styles
    });
});

let productPrice = 0;
let selectedColor = null;
let selectedSize = null;
let productId = null;
document.addEventListener("DOMContentLoaded", () => {

    const params = new URLSearchParams(window.location.search);
    productId = params.get('id');

    if (productId) {
        fetch(`/api/1.0/products/details?id=${productId}`)
            .then(response => response.json())
            .then(data => {
                const product = data.data[0];
                productPrice = product.price;
                document.getElementById('mainImage').src = product.main_image;
                document.getElementById('title').innerText = product.title;
                document.getElementById('productId').innerText = `產品編號: ${product.id}`;
                document.getElementById('productPrice').innerText = `TWD. ${product.price}`
                document.getElementById('note').innerText = `注意事項: ${product.note}`;
                document.getElementById('texture').innerText = `材質: ${product.texture}`;
                document.getElementById('wash').innerText = `洗滌方式: ${product.wash}`;
                document.getElementById('place').innerText = `產地: ${product.place}`;
                document.getElementById('story').innerText = product.story;

                // for color choice
                const colorContainer = document.getElementById('colorContainer');
                product.colors.forEach((color) => {
                    const colorBox = document.createElement('span');
                    colorBox.className = 'color-box';
                    colorBox.style.backgroundColor = color.code;
                    colorBox.addEventListener('click', () => {
                        selectedColor = color;
                        updateSelection('color');
                    });
                    colorContainer.appendChild(colorBox);
                });

                // for size choice
                const sizeContainer = document.getElementById('sizeContainer');
                product.sizes.forEach(size => {
                    const sizeOption = document.createElement('span');
                    sizeOption.className = 'size-option';
                    sizeOption.innerText = size;
                    sizeOption.addEventListener('click', () => {
                        selectedSize = size;
                        updateSelection('size');
                    });
                    sizeContainer.appendChild(sizeOption);
                });

                // other img
                const additionalImages = document.getElementById('additionalImages');
                product.images.forEach(image => {
                    const imgElement = document.createElement('img');
                    imgElement.src = image;
                    additionalImages.appendChild(imgElement);
                });

                function updateSelection(type) {
                    // remove selected
                    if (type === 'color') {
                        document.querySelectorAll('.color-box').forEach(box => box.classList.remove('selected'));
                        const selectedBox = Array.from(document.querySelectorAll('.color-box')).find(box =>
                            box.style.backgroundColor === hexToRgb(selectedColor.code));
                        if (selectedBox) selectedBox.classList.add('selected');
                    } else if (type === 'size') {
                        document.querySelectorAll('.size-option').forEach(option => option.classList.remove('selected'));
                        const selectedOption = Array.from(document.querySelectorAll('.size-option')).find(option =>
                            option.innerText === selectedSize);
                        if (selectedOption) selectedOption.classList.add('selected');
                    }

                    // enable quantity selection
                    if (selectedColor && selectedSize) {
                        const variant = product.variants.find(v => v.color_code === selectedColor.code && v.size === selectedSize);
                        if (variant) {
                            document.getElementById('quantity').disabled = false;
                            document.getElementById('quantity').max = variant.stock;
                        }
                    } else {
                        document.getElementById('quantity').disabled = true;
                        document.getElementById('quantity').value = 1;
                    }
                }
            })
            .catch(error => console.error('Error fetching product details:', error));
    } else {
        console.error('No product ID specified');
    }
});

function showCheckout() {
    const token = localStorage.getItem('token');
    if (!token) {
        const currentUrl = window.location.pathname + window.location.search;
        window.location.href = '/profile.html?redirect=' + encodeURIComponent(currentUrl);
        return;
    }

    if (selectedColor && selectedSize && document.getElementById('quantity').value > 0) {
        // show checkout
        const checkoutSection = document.getElementById('checkoutSection');
        checkoutSection.style.display = 'block';

        // renew order
        const orderPreview = document.getElementById('orderPreview');
        const quantity = parseInt(document.getElementById('quantity').value);
        const subtotal = productPrice * quantity;
        const freight = 100;
        const total = subtotal + freight;

        orderPreview.innerHTML = `
                <p>商品名: ${document.getElementById('title').innerText}</p>
                <p>數量: ${quantity}</p>
                <p>價格: TWD ${productPrice}</p>
                <p>小計: TWD ${subtotal}</p>
                <p>運費: TWD ${freight}</p>
                <p>總計: TWD ${total}</p>
            `;

        // scroll to checkout
        checkoutSection.scrollIntoView({behavior: 'smooth'});
    } else {
        alert('請選擇顏色、尺寸及數量');
    }
}

function onSubmit() {
    TPDirect.card.getPrime(function (result) {
        if (result.status !== 0) {
            alert('取得 Prime 失敗：' + result.msg);
            return;
        }

        // get prime
        const prime = result.card.prime;
        console.log("Prime : " + prime);

        // build order
        const quantity = parseInt(document.getElementById('quantity').value);
        const subtotal = productPrice * quantity;
        const freight = 100;
        const total = subtotal + freight;

        const orderData = {
            prime: prime,
            order: {
                shipping: "delivery",
                payment: "credit_card",
                subtotal: subtotal,
                freight: freight,
                total: total,
                recipient: {
                    name: document.getElementById('recipientName').value,
                    phone: document.getElementById('phone').value,
                    email: document.getElementById('email').value,
                    address: document.getElementById('address').value,
                    time: document.querySelector('input[name="deliveryTime"]:checked').value
                },
                list: [
                    {
                        id: productId,
                        name: document.getElementById('title').innerText,
                        price: productPrice,
                        color: {
                            code: selectedColor.code,
                            name: selectedColor.name
                        },
                        size: selectedSize,
                        qty: quantity
                    }
                ]
            }
        };

        // post request to order api
        fetch('/api/1.0/order/checkout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}` // 使用本地儲存的token
            },
            body: JSON.stringify(orderData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    alert('提交訂單失敗：' + data.error);
                } else {
                    localStorage.setItem('orderResponse', JSON.stringify(data));
                    // success -> thank you page
                    window.location.href = '/thankyou.html';
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    });
}

function hexToRgb(hex) {
    let result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? `rgb(${parseInt(result[1], 16)}, ${parseInt(result[2], 16)}, ${parseInt(result[3], 16)})` : null;
}

