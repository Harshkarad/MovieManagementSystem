document.addEventListener('DOMContentLoaded', function() {
    // Initialize genre selection
    const genreSelect = document.getElementById('movieGenre');
    const genreOptions = document.getElementById('genreDropdownOptions');
    const genreSelectedOptions = document.getElementById('genreSelectedOptions');
    
    // Populate genre dropdown
    Array.from(genreSelect.options).forEach(option => {
        const genreOption = document.createElement('div');
        genreOption.className = 'dropdown-option';
        genreOption.textContent = option.text;
        genreOption.dataset.value = option.value;
        
        // Check if this option is selected
        if (option.selected) {
            genreOption.classList.add('selected');
        }
        
        genreOption.addEventListener('click', function() {
            this.classList.toggle('selected');
            option.selected = !option.selected;
            updateSelectedGenres();
        });
        
        genreOptions.appendChild(genreOption);
    });
    
    function updateSelectedGenres() {
        genreSelectedOptions.innerHTML = '';
        const selectedOptions = Array.from(genreSelect.selectedOptions);
        
        if (selectedOptions.length === 0) {
            genreSelectedOptions.innerHTML = '<span class="placeholder">Select genres...</span>';
            return;
        }
        
        selectedOptions.forEach(option => {
            const selectedOption = document.createElement('span');
            selectedOption.className = 'selected-option';
            selectedOption.innerHTML = `
                <span>${option.text}</span>
                <span class="remove-option" onclick="removeGenre(this)">×</span>
            `;
            genreSelectedOptions.appendChild(selectedOption);
        });
    }
    
    // Initialize cast tags
    const castInput = document.getElementById('movieCastInput');
    const castTags = document.getElementById('castTags');
    const castHiddenInput = document.getElementById('movieCast');
    
    castInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            const value = this.value.trim();
            if (value) {
                addCastTag(value);
                this.value = '';
                updateCastHiddenInput();
            }
        }
    });
    
    function addCastTag(value) {
        const tag = document.createElement('span');
        tag.className = 'tag';
        tag.innerHTML = `
            <span>${value}</span>
            <span class="tag-remove" onclick="removeTag(this)">×</span>
        `;
        castTags.insertBefore(tag, castInput);
    }
    
    window.removeTag = function(element) {
        element.parentElement.remove();
        updateCastHiddenInput();
    };
    
    function updateCastHiddenInput() {
        const tags = Array.from(document.querySelectorAll('#castTags .tag span:first-child'))
            .map(span => span.textContent);
        castHiddenInput.value = tags.join(',');
    }
    
    // Poster preview
    const posterInput = document.getElementById('moviePoster');
    const posterPreview = document.getElementById('posterPreview');
    
    posterInput.addEventListener('input', function() {
        if (this.value) {
            posterPreview.innerHTML = `<img src="${this.value}" alt="Poster Preview" style="max-width: 200px;">`;
        } else {
            posterPreview.innerHTML = '';
        }
    });
});

function addScreening() {
    const container = document.getElementById('screeningContainer');
    const newEntry = document.createElement('div');
    newEntry.className = 'screening-entry';
    newEntry.innerHTML = `
        <div class="form-row">
            <div class="form-group">
                <label>Date</label>
                <input type="date" name="screeningDates" class="screening-date" required>
            </div>
            <div class="form-group">
                <label>Time</label>
                <input type="time" name="screeningTimes" class="screening-time" required>
            </div>
            <div class="form-group">
                <label>Screen</label>
                <input type="text" name="screens" class="screening-screen" placeholder="Screen number" required>
            </div>
            <div class="form-group">
                <label>Price</label>
                <input type="number" step="0.01" name="prices" class="screening-price" placeholder="Price" required>
            </div>
            <div class="form-group">
                <label>Seats</label>
                <input type="number" name="availableSeats" class="screening-seats" placeholder="Available seats" required>
            </div>
            <button type="button" class="btn-remove-screening" onclick="removeScreening(this)">×</button>
        </div>
    `;
    container.appendChild(newEntry);
}

function removeScreening(button) {
    if (confirm('Are you sure you want to remove this screening?')) {
        button.closest('.screening-entry').remove();
    }
}

function removeGenre(element) {
    const genreText = element.previousElementSibling.textContent;
    const genreSelect = document.getElementById('movieGenre');
    
    // Find and unselect the corresponding option
    Array.from(genreSelect.options).forEach(option => {
        if (option.text === genreText) {
            option.selected = false;
        }
    });
    
    // Update the UI
    element.parentElement.remove();
    const genreOptions = document.querySelectorAll('#genreDropdownOptions .dropdown-option');
    genreOptions.forEach(option => {
        if (option.textContent === genreText) {
            option.classList.remove('selected');
        }
    });
    
    // Show placeholder if no genres left
    if (document.querySelectorAll('#genreSelectedOptions .selected-option').length === 0) {
        document.querySelector('#genreSelectedOptions .placeholder').style.display = 'inline';
    }
}