document.addEventListener('DOMContentLoaded', function () {
    // DOM Elements
    const registrationForm = document.getElementById('registrationForm');
    const firstNameInput = document.getElementById('firstName');
    const lastNameInput = document.getElementById('lastName');
    const emailInput = document.getElementById('email');
    const phoneInput = document.getElementById('phone');
    const locationInput = document.getElementById('location');
    const imageInput = document.getElementById('image');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const strengthBar = document.querySelector('.strength-bar');
    const togglePasswordButtons = document.querySelectorAll('.toggle-password');
    const registerBtn = document.getElementById('registerBtn');
    const spinner = registerBtn.querySelector('.spinner-border');

    // OTP Elements
    const sendOtpBtn = document.getElementById('sendOtpBtn');
    const otpVerificationSection = document.getElementById('otpVerificationSection');
    const otpInput = document.getElementById('otp');
    const verifyOtpBtn = document.getElementById('verifyOtpBtn');
    const otpError = document.getElementById('otpError');

    // Alert elements
    const errorAlert = document.querySelector('.alert-error');
    const successAlert = document.querySelector('.alert-success');

    // State variables
    let isEmailVerified = false;
    let resendTimeout = 60; // 60 seconds
    let resendTimer = null;

    // Initialize event listeners
    initEventListeners();

    function initEventListeners() {
        // Password strength meter
        if (passwordInput) {
            passwordInput.addEventListener('input', updatePasswordStrength);
        }

        // Toggle password visibility
        togglePasswordButtons.forEach(button => {
            button.addEventListener('click', togglePasswordVisibility);
        });

        // Phone number validation
        if (phoneInput) {
            phoneInput.addEventListener('input', validatePhoneNumber);
        }

        // Email input changes
        emailInput.addEventListener('input', function () {
            const email = emailInput.value.trim();
            const isValid = validateEmail(email);

            if (isValid) {
                sendOtpBtn.style.display = 'block';
                emailInput.parentElement.classList.add('has-button');
                hideAlert('error');
            } else {
                sendOtpBtn.style.display = 'none';
                emailInput.parentElement.classList.remove('has-button');
                hideOtpSection();
                isEmailVerified = false;
            }
        });

        // Send OTP button
        sendOtpBtn.addEventListener('click', sendOtp);

        // Verify OTP button
        verifyOtpBtn.addEventListener('click', verifyOtp);

        // Form submission
        registrationForm.addEventListener('submit', handleFormSubmit);
    }

    // Email validation
    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }

    // Phone number validation
    function validatePhoneNumber() {
        const phone = phoneInput.value;
        const phoneRegex = /^[6-9]\d{9}$/;

        if (phone && !phoneRegex.test(phone)) {
            phoneInput.classList.add('invalid');
            return false;
        } else {
            phoneInput.classList.remove('invalid');
            return true;
        }
    }

    // Password strength meter
    function updatePasswordStrength() {
        const password = passwordInput.value;
        const strength = calculatePasswordStrength(password);
        updateStrengthBar(strength);
    }

    function calculatePasswordStrength(password) {
        let strength = 0;

        if (password.length > 7) strength += 1;
        if (password.length > 11) strength += 1;
        if (/[A-Z]/.test(password)) strength += 1;
        if (/[0-9]/.test(password)) strength += 1;
        if (/[^A-Za-z0-9]/.test(password)) strength += 1;

        return Math.min(strength, 5);
    }

    function updateStrengthBar(strength) {
        const colors = ['#dc3545', '#fd7e14', '#ffc107', '#28a745', '#20c997'];
        const width = (strength / 5) * 100;

        if (strengthBar) {
            strengthBar.style.width = `${width}%`;
            strengthBar.style.backgroundColor = colors[strength - 1] || colors[0];
        }
    }

    // Toggle password visibility
    function togglePasswordVisibility(e) {
        e.preventDefault();
        const targetId = this.getAttribute('data-target');
        const targetInput = document.getElementById(targetId);

        if (targetInput) {
            const isPassword = targetInput.type === 'password';
            targetInput.type = isPassword ? 'text' : 'password';

            const icon = this.querySelector('i');
            if (icon) {
                icon.classList.toggle('fa-eye');
                icon.classList.toggle('fa-eye-slash');
            }
        }
    }

    // Send OTP function
    function sendOtp() {
        const email = emailInput.value.trim();

        if (!validateEmail(email)) {
            showAlert('Please enter a valid email address', 'error');
            return;
        }

        // Disable button and show loading
        sendOtpBtn.disabled = true;
        sendOtpBtn.textContent = 'Sending...';

        fetch('/send-otp', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `email=${encodeURIComponent(email)}`
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw err; });
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    showOtpSection();
                    startResendTimer();
                    showAlert('OTP sent to your email!', 'success');
                } else {
                    throw new Error(data.message || 'Failed to send OTP');
                }
            })
            .catch(error => {
                showAlert(error.message, 'error');
            })
            .finally(() => {
                sendOtpBtn.disabled = false;
                sendOtpBtn.textContent = 'Resend OTP';
            });
    }

    // Verify OTP function
    function verifyOtp() {
        const email = emailInput.value.trim();
        const otp = otpInput.value.trim();

        if (!otp || otp.length !== 6) {
            showAlert('Please enter a valid 6-digit OTP', 'error');
            return;
        }

        verifyOtpBtn.disabled = true;
        verifyOtpBtn.textContent = 'Verifying...';

        fetch('/verify-otp', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `email=${encodeURIComponent(email)}&otp=${encodeURIComponent(otp)}`
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw err; });
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    isEmailVerified = true;
                    verifyOtpBtn.textContent = 'Verified';
                    verifyOtpBtn.style.backgroundColor = '#28a745';
                    verifyOtpBtn.disabled = true;
                    emailInput.readOnly = true;
                    showAlert('Email verified successfully!', 'success');
                } else {
                    throw new Error(data.message || 'Invalid OTP');
                }
            })
            .catch(error => {
                showAlert(error.message, 'error');
                verifyOtpBtn.disabled = false;
                verifyOtpBtn.textContent = 'Verify OTP';
            });
    }

    // Form submission handler
    function handleFormSubmit(e) {
        e.preventDefault();

        // 1. Check if email is verified
        if (!isEmailVerified) {
            showAlert('Please verify your email before submitting.', 'error');
            emailInput.focus();
            return;
        }

        // 2. Validate all form fields
        if (!validateForm()) {
            return;
        }

        // 3. Show loading state
        registerBtn.disabled = true;
        spinner.style.display = 'inline-block';

        // 4. Prepare form data
        const formData = {
            firstname: firstNameInput.value.trim(),
            lastname: lastNameInput.value.trim(),
            email: emailInput.value.trim(),
            mobile: phoneInput.value.trim(),
            location: locationInput.value.trim(),
            profileUrl: imageInput.value.trim(),
            password: passwordInput.value // No hashing here - will be plain text
        };

        // 5. Submit the form via fetch
        fetch('/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw err; });
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    showAlert('Registration successful! Redirecting...', 'success');
                    setTimeout(() => {
                        window.location.href = '/?registered=true';
                    }, 1500);
                } else {
                    throw new Error(data.message || 'Registration failed');
                }
            })
            .catch(error => {
                showAlert(error.message, 'error');
            })
            .finally(() => {
                registerBtn.disabled = false;
                spinner.style.display = 'none';
            });
    }

    // Form validation
    function validateForm() {
        let isValid = true;
        hideAlert('error');

        // Validate required fields
        const requiredFields = registrationForm.querySelectorAll('[required]');
        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                field.classList.add('invalid');
                const errorMsg = field.closest('.form-group').querySelector('.error-message');
                if (errorMsg) {
                    errorMsg.textContent = 'This field is required';
                    errorMsg.style.display = 'block';
                }
                isValid = false;

                if (isValid) {
                    field.focus();
                    isValid = false;
                }
            } else {
                field.classList.remove('invalid');
                const errorMsg = field.closest('.form-group').querySelector('.error-message');
                if (errorMsg) {
                    errorMsg.style.display = 'none';
                }
            }
        });

        // Validate password match
        if (passwordInput.value !== confirmPasswordInput.value) {
            confirmPasswordInput.classList.add('invalid');
            const errorMsg = confirmPasswordInput.closest('.form-group').querySelector('.error-message');
            if (errorMsg) {
                errorMsg.textContent = 'Passwords do not match';
                errorMsg.style.display = 'block';
            }
            isValid = false;
        } else {
            confirmPasswordInput.classList.remove('invalid');
            const errorMsg = confirmPasswordInput.closest('.form-group').querySelector('.error-message');
            if (errorMsg) {
                errorMsg.style.display = 'none';
            }
        }

        // Validate phone number
        if (!validatePhoneNumber()) {
            isValid = false;
        }

        return isValid;
    }

    // Helper functions
    function showOtpSection() {
        otpVerificationSection.style.display = 'block';
        otpInput.value = '';
        otpInput.focus();
    }

    function hideOtpSection() {
        otpVerificationSection.style.display = 'none';
    }

    function startResendTimer() {
        clearInterval(resendTimer);
        let seconds = resendTimeout;
        updateResendButton(seconds);

        resendTimer = setInterval(() => {
            seconds--;
            updateResendButton(seconds);

            if (seconds <= 0) {
                clearInterval(resendTimer);
            }
        }, 1000);
    }

    function updateResendButton(seconds) {
        if (seconds > 0) {
            sendOtpBtn.disabled = true;
            sendOtpBtn.textContent = `Resend OTP (${seconds}s)`;
        } else {
            sendOtpBtn.disabled = false;
            sendOtpBtn.textContent = 'Resend OTP';
        }
    }

    function showAlert(message, type) {
        const alertDiv = type === 'error' ? errorAlert : successAlert;
        if (alertDiv) {
            alertDiv.textContent = message;
            alertDiv.style.display = 'block';
            setTimeout(() => {
                alertDiv.style.display = 'none';
            }, 5000);
        }
    }

    function hideAlert(type) {
        const alertDiv = type === 'error' ? errorAlert : successAlert;
        if (alertDiv) {
            alertDiv.style.display = 'none';
        }
    }
});