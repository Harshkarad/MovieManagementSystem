// Client-side form validation
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('registrationForm');
    const inputs = form.querySelectorAll('input[required]');
    
    inputs.forEach(input => {
        input.addEventListener('blur', validateInput);
        input.addEventListener('input', clearError);
    });
    
    function validateInput(e) {
        const input = e.target;
        
        if (!input.value.trim()) {
            showError(input, 'This field is required');
            return;
        }
        
        // Specific validations based on input type
        if (input.type === 'email' && !isValidEmail(input.value)) {
            showError(input, 'Please enter a valid email address');
        }
        
        if (input.id === 'phone' && !isValidPhone(input.value)) {
            showError(input, 'Please enter a valid phone number');
        }
        
        if (input.id === 'password' && input.value.length < 8) {
            showError(input, 'Password must be at least 8 characters');
        }
        
        if (input.id === 'confirmPassword' && input.value !== document.getElementById('password').value) {
            showError(input, 'Passwords do not match');
        }
    }
    
    function showError(input, message) {
        const formGroup = input.closest('.form-group');
        if (!formGroup) return;
        
        formGroup.classList.add('has-error');
        
        let errorElement = formGroup.querySelector('.error-message');
        if (!errorElement) {
            errorElement = document.createElement('small');
            errorElement.className = 'error-message';
            formGroup.appendChild(errorElement);
        }
        
        errorElement.textContent = message;
    }
    
    function clearError(e) {
        const input = e.target;
        const formGroup = input.closest('.form-group');
        if (!formGroup) return;
        
        formGroup.classList.remove('has-error');
        
        const errorElement = formGroup.querySelector('.error-message');
        if (errorElement) {
            errorElement.textContent = '';
        }
    }
    
    function isValidEmail(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }
    
    function isValidPhone(phone) {
        return /^[0-9]{10,15}$/.test(phone);
    }
});