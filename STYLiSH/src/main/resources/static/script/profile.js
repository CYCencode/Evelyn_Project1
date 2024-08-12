document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const profileSection = document.getElementById('profile-section');
    const authSection = document.getElementById('auth-section');
    const signinForm = document.getElementById('signin-form');
    const signupForm = document.getElementById('signup-form');

    if (token) {
        fetch('/api/1.0/user/profile', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error(`HTTP status code: ${response.status}`);
                }
            })
            .then(data => {
                if (data.data) {
                    profileSection.style.display = 'block';
                    document.getElementById('user-name').innerText = data.data.name;
                    document.getElementById('user-picture').src = data.data.picture;
                    document.getElementById('user-email').innerText = data.data.email;
                }
            })
            .catch(error => {
                console.error('Error:', error);
                localStorage.removeItem('token');
                showAuthForms();
            });
    } else {
        showAuthForms();
    }

    function showAuthForms() {
        authSection.style.display = 'block';
        signinForm.style.display = 'block';
        signupForm.style.display = 'block';

        // Set redirect URL if exists
        const urlParams = new URLSearchParams(window.location.search);
        const redirectUrl = urlParams.get('redirect') || '/index.html';
        document.getElementById('redirect-url').value = redirectUrl;
    }
});

document.getElementById('signin-form').addEventListener('submit', function (event) {
    event.preventDefault();
    const email = document.getElementById('signin-email').value;
    const password = document.getElementById('signin-password').value;
    const redirectUrl = document.getElementById('redirect-url').value;

    fetch('/api/1.0/user/signin', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({email, password, provider: 'native'})
    })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            if (data.data.access_token) {
                localStorage.setItem('token', data.data.access_token);
                window.location.href = redirectUrl;
            }
        })
        .catch(error => {
            console.error('Error signing in:', error);
        });
});

document.getElementById('signup-form').addEventListener('submit', function (event) {
    event.preventDefault();
    const name = document.getElementById('signup-name').value;
    const email = document.getElementById('signup-email').value;
    const password = document.getElementById('signup-password').value;
    const redirectUrl = document.getElementById('redirect-url').value;

    fetch('/api/1.0/user/signup', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name, email, password})
    })
        .then(response => response.json())
        .then(data => {
            if (data.data.access_token) {
                localStorage.setItem('token', data.data.access_token);
                window.location.href = redirectUrl;
            }
        })
        .catch(error => {
            console.error('Error signing up:', error);
        });
});