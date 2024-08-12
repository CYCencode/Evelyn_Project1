document.addEventListener("DOMContentLoaded", () => {
    fetchCampaign();
    fetchProductsByCategory();
});

function fetchCampaign() {
    fetch('/api/1.0/marketing/campaigns')
        .then(response => response.json())
        .then(data => {
            console.log("campaign data", data);
            const firstCampaign = data.data[0];
            const campaignContainer = document.getElementById("campaign-container");
            campaignContainer.innerHTML = `
            <div class="image-container">
            <a href="/product.html?id=${firstCampaign.product_id}" id="campaignHref"><img id="campaignImage" src="${firstCampaign.picture}" alt="Campaign Image"></a>
            </div>
            <div class="story-container">
            <p id="story">${firstCampaign.story}</p>
            </div>`
        })
        .catch(error => console.error('Error fetching campaign data:', error));
}

function fetchProductsByCategory() {
    const params = new URLSearchParams(window.location.search);
    const category = params.get('category');
    let url = '/api/1.0/products/';
    if (category) {
        url += `${encodeURIComponent(category)}`;
    } else {
        url += "all";
    }

    fetch(url, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            renderProducts(data.data);
        })
        .catch(error => console.error('Error fetching products:', error));
}

function searchProducts() {
    const query = document.getElementById('searchInput').value;
    fetch(`/api/1.0/products/search?keyword=${encodeURIComponent(query)}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            renderProducts(data.data);
        })
        .catch(error => console.error('Error fetching products:', error));
}

function renderProducts(products) {
    const resultsContainer = document.getElementById('results');
    resultsContainer.innerHTML = '';
    products.forEach(product => {
        const productElement = document.createElement('div');
        productElement.className = 'product';
        // create color block
        let colorBoxes = '';
        if (product.colors && product.colors.length > 0) {
            colorBoxes = product.colors.map(color => `
                <span class="color-box" style="background-color:${color.code};"></span>
            `).join('');
        }
        productElement.innerHTML = `
                 <a href="/product.html?id=${product.id}">
                <img src="${product.main_image}" alt="${product.title}">
                </a>
                <div>
                    <div class="color-container">
                     ${colorBoxes}
                     </div>
                    <h3>${product.title}</h3>
                    <p>TWD ${product.price}</p>
                </div>
            `;
        resultsContainer.appendChild(productElement);
    });
}