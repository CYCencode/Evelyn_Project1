const orderResponse = localStorage.getItem('orderResponse');
if (orderResponse) {
    console.log('API Response:', JSON.parse(orderResponse));
    localStorage.removeItem('orderResponse');
} else {
    console.warn('沒有收到API的oder response。');
}