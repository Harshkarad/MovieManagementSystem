// // Function to open add movie modal
// function openAddMovieModal() {
//     document.getElementById('addMovieModal').style.display = 'block';
//     document.body.classList.add('no-scroll');
// }

// // Function to close add movie modal
// function closeAddMovieModal() {
//     document.getElementById('addMovieModal').style.display = 'none';
//     document.body.classList.remove('no-scroll');
// }

// // Close modals when clicking outside
// window.addEventListener('click', function(event) {
//     if (event.target.className === 'modal') {
//         event.target.style.display = 'none';
//         document.body.classList.remove('no-scroll');
//     }

//     // Close dropdowns when clicking outside
//     const dropdowns = document.querySelectorAll('.dropdown-options.show');
//     dropdowns.forEach(dropdown => {
//         if (!dropdown.contains(event.target) && !event.target.closest('.multi-select-container')) {
//             dropdown.classList.remove('show');
//         }
//     });
// });

// // Login/Register button handlers
// document.getElementById('loginBtn').addEventListener('click', function() {
//     document.getElementById('loginModal').style.display = 'block';
//     document.body.classList.add('no-scroll');
// });

// document.getElementById('registerBtn').addEventListener('click', function() {
//     document.getElementById('registerModal').style.display = 'block';
//     document.body.classList.add('no-scroll');
// });

// // Mobile menu toggle functionality
// document.querySelector('.mobile-menu-toggle').addEventListener('click', function() {
//     const sidebar = document.querySelector('.sidebar');
//     const overlay = document.querySelector('.sidebar-overlay');

//     sidebar.classList.toggle('active');
//     overlay.classList.toggle('active');
//     document.body.classList.toggle('no-scroll');

//     // Toggle aria-expanded attribute for accessibility
//     const isExpanded = this.getAttribute('aria-expanded') === 'true';
//     this.setAttribute('aria-expanded', !isExpanded);
// });

// // Close sidebar when clicking overlay
// document.querySelector('.sidebar-overlay').addEventListener('click', function() {
//     document.querySelector('.sidebar').classList.remove('active');
//     this.classList.remove('active');
//     document.body.classList.remove('no-scroll');
//     document.querySelector('.mobile-menu-toggle').setAttribute('aria-expanded', 'false');
// });

// // Close sidebar when clicking on a link
// document.querySelectorAll('.sidebar a').forEach(link => {
//     link.addEventListener('click', function() {
//         document.querySelector('.sidebar').classList.remove('active');
//         document.querySelector('.sidebar-overlay').classList.remove('active');
//         document.body.classList.remove('no-scroll');
//         document.querySelector('.mobile-menu-toggle').setAttribute('aria-expanded', 'false');
//     });
// });

// // Initialize when DOM is loaded
// document.addEventListener('DOMContentLoaded', function() {
//     // Add sidebar overlay to DOM
//     const overlay = document.createElement('div');
//     overlay.className = 'sidebar-overlay';
//     document.body.appendChild(overlay);

//     initializeGenreMultiSelect();
//     setupTagInputs();
//     setupPosterPreview();

//     // Add event listeners to all delete buttons
//     document.querySelectorAll('.btn-delete').forEach(button => {
//         button.addEventListener('click', function(e) {
//             e.preventDefault();
//             if (confirm('Are you sure you want to change the status of this movie?')) {
//                 showLoading();
//                 // Submit the form
//                 this.closest('form').submit();
//             }
//         });
//     });
// });

// // Show loading spinner
// function showLoading() {
//     const spinner = document.createElement('div');
//     spinner.className = 'loading-spinner';
//     spinner.innerHTML = '<div class="spinner"></div>';
//     document.body.appendChild(spinner);
//     spinner.style.display = 'flex';
// }

// // Genre multi-select functionality
// function initializeGenreMultiSelect() {
//     const genreSelect = document.getElementById('movieGenre');
//     if (!genreSelect) return;

//     const genreSelectedOptions = document.getElementById('genreSelectedOptions');
//     const genreDropdownOptions = document.getElementById('genreDropdownOptions');

//     // Create dropdown options
//     Array.from(genreSelect.options).forEach(option => {
//         const optionItem = document.createElement('div');
//         optionItem.className = 'option-item';
//         optionItem.textContent = option.textContent;
//         optionItem.dataset.value = option.value;

//         if (option.selected) {
//             optionItem.classList.add('selected');
//         }

//         optionItem.addEventListener('click', function(e) {
//             e.stopPropagation();
//             option.selected = !option.selected;
//             optionItem.classList.toggle('selected', option.selected);
//             updateSelectedGenres();
//         });

//         genreDropdownOptions.appendChild(optionItem);
//     });

//     function updateSelectedGenres() {
//         genreSelectedOptions.innerHTML = '';
//         const selectedOptions = Array.from(genreSelect.selectedOptions);

//         if (selectedOptions.length === 0) {
//             genreSelectedOptions.innerHTML = '<span class="placeholder">Select genres...</span>';
//             return;
//         }

//         selectedOptions.forEach(option => {
//             const tag = document.createElement('div');
//             tag.className = 'genre-tag';
//             tag.innerHTML = `
//                 ${option.textContent}
//                 <span class="remove-tag" data-value="${option.value}">&times;</span>
//             `;
//             genreSelectedOptions.appendChild(tag);
//         });

//         // Add event listeners to remove buttons
//         document.querySelectorAll('.genre-tag .remove-tag').forEach(btn => {
//             btn.addEventListener('click', function(e) {
//                 e.stopPropagation();
//                 const value = this.dataset.value;
//                 const option = Array.from(genreSelect.options).find(opt => opt.value === value);
//                 if (option) {
//                     option.selected = false;
//                     updateSelectedGenres();
//                     const dropdownOption = Array.from(genreDropdownOptions.children)
//                         .find(opt => opt.dataset.value === value);
//                     if (dropdownOption) {
//                         dropdownOption.classList.remove('selected');
//                     }
//                 }
//             });
//         });
//     }

//     // Toggle dropdown
//     document.querySelector('.multi-select-container').addEventListener('click', function(e) {
//         e.stopPropagation();
//         genreDropdownOptions.classList.toggle('show');
//     });

//     // Initialize
//     updateSelectedGenres();
// }

// // Tag inputs for directors and cast
// function setupTagInputs() {
//     setupTagInput('movieDirector', 'directorTags');
//     setupTagInput('movieCastInput', 'castTags', 'movieCast');
// }

// function setupTagInput(inputId, tagsContainerId, hiddenInputId = null) {
//     const container = document.getElementById(tagsContainerId);
//     if (!container) return;

//     let input = container.querySelector('.tag-input');
//     if (!input) {
//         input = document.createElement('input');
//         input.type = 'text';
//         input.className = 'tag-input';
//         input.placeholder = inputId === 'movieDirector' ? 
//             'Add director and press Enter' : 'Add cast member and press Enter';
//         container.appendChild(input);
//     }

//     input.addEventListener('keydown', function(e) {
//         if ((e.key === 'Enter' || e.key === ',') && this.value.trim()) {
//             e.preventDefault();
//             addTag(this.value.trim(), container, hiddenInputId);
//             this.value = '';
//         } else if (e.key === 'Backspace' && this.value === '') {
//             const tags = container.querySelectorAll('.tag');
//             if (tags.length > 0) {
//                 const lastTag = tags[tags.length - 1];
//                 if (hiddenInputId) {
//                     updateHiddenInput(hiddenInputId, lastTag.textContent.trim().replace('×', ''), true);
//                 }
//                 lastTag.remove();
//             }
//         }
//     });

//     // Initialize with existing tags if any
//     const existingTags = container.querySelectorAll('.tag');
//     existingTags.forEach(tag => {
//         tag.querySelector('.remove-tag').addEventListener('click', function() {
//             if (hiddenInputId) {
//                 updateHiddenInput(hiddenInputId, tag.textContent.trim().replace('×', ''), true);
//             }
//             tag.remove();
//         });
//     });
// }

// function addTag(value, container, hiddenInputId = null) {
//     if (!value) return;

//     const tag = document.createElement('div');
//     tag.className = 'tag';
//     tag.innerHTML = `
//         ${value}
//         <span class="remove-tag">&times;</span>
//     `;

//     const removeBtn = tag.querySelector('.remove-tag');
//     removeBtn.addEventListener('click', function() {
//         if (hiddenInputId) {
//             updateHiddenInput(hiddenInputId, value, true);
//         }
//         tag.remove();
//     });

//     const input = container.querySelector('.tag-input');
//     container.insertBefore(tag, input);

//     if (hiddenInputId) {
//         updateHiddenInput(hiddenInputId, value);
//     }
// }

// function updateHiddenInput(hiddenInputId, value, remove = false) {
//     const hiddenInput = document.getElementById(hiddenInputId);
//     if (!hiddenInput) return;

//     let currentValues = hiddenInput.value ? hiddenInput.value.split(',') : [];

//     if (remove) {
//         currentValues = currentValues.filter(v => v.trim() !== value.trim());
//     } else {
//         currentValues.push(value.trim());
//     }

//     hiddenInput.value = currentValues.join(',');
// }

// // Poster preview functionality
// function setupPosterPreview() {
//     const posterInput = document.getElementById('moviePoster');
//     if (!posterInput) return;

//     const posterPreview = document.getElementById('posterPreview');

//     posterInput.addEventListener('input', function() {
//         const url = this.value.trim();
//         if (url) {
//             const img = document.createElement('img');
//             img.src = url;
//             img.alt = 'Poster Preview';
//             img.onerror = function() {
//                 posterPreview.innerHTML = '<span>Invalid image URL</span>';
//             };
//             img.onload = function() {
//                 posterPreview.innerHTML = '';
//                 posterPreview.appendChild(img);
//             };
//             posterPreview.innerHTML = 'Loading...';
//         } else {
//             posterPreview.innerHTML = '';
//         }
//     });
// }

// // Screening management
// function addScreening() {
//     const container = document.getElementById('screeningContainer');
//     const newScreening = document.createElement('div');
//     newScreening.className = 'screening-entry';
//     newScreening.innerHTML = `
//         <div class="form-row">
//             <div class="form-group">
//                 <label>Date</label>
//                 <input type="date" name="screeningDates" class="screening-date" required>
//             </div>
//             <div class="form-group">
//                 <label>Time</label>
//                 <input type="time" name="screeningTimes" class="screening-time" required>
//             </div>
//             <div class="form-group">
//                 <label>Screen</label>
//                 <input type="text" name="screens" class="screening-screen" placeholder="Screen number" required>
//             </div>
//             <div class="form-group">
//                 <label>Price</label>
//                 <input type="number" step="0.01" name="prices" class="screening-price" placeholder="Price" required>
//             </div>
//             <div class="form-group">
//                 <label>Seats</label>
//                 <input type="number" name="availableSeats" class="screening-seats" placeholder="Available seats" required>
//             </div>
//             <button type="button" class="btn-remove-screening" onclick="removeScreening(this)">×</button>
//         </div>
//     `;
//     container.appendChild(newScreening);
// }

// function removeScreening(button) {
//     const container = document.getElementById('screeningContainer');
//     if (container.children.length > 1) {
//         button.closest('.screening-entry').remove();
//     } else {
//         alert("At least one screening is required.");
//     }
// }

// // Handle form submission with fetch
// document.getElementById('addMovieForm')?.addEventListener('submit', function(e) {
//     e.preventDefault();
//     showLoading();

//     // Submit the form normally (you can replace this with fetch API if needed)
//     this.submit();
// });
// Function to open add movie modal
function openAddMovieModal() {
    document.getElementById('addMovieModal').style.display = 'block';
    document.body.classList.add('no-scroll');
}

// Function to close add movie modal
function closeAddMovieModal() {
    document.getElementById('addMovieModal').style.display = 'none';
    document.body.classList.remove('no-scroll');
}

// Close modals when clicking outside
window.addEventListener('click', function (event) {
    if (event.target.className === 'modal') {
        event.target.style.display = 'none';
        document.body.classList.remove('no-scroll');
    }

    // Close dropdowns when clicking outside
    const dropdowns = document.querySelectorAll('.dropdown-options.show');
    dropdowns.forEach(dropdown => {
        if (!dropdown.contains(event.target) && !event.target.closest('.multi-select-container')) {
            dropdown.classList.remove('show');
        }
    });
});

// Login/Register button handlers
document.getElementById('loginBtn').addEventListener('click', function () {
    document.getElementById('loginModal').style.display = 'block';
    document.body.classList.add('no-scroll');
});

document.getElementById('registerBtn').addEventListener('click', function () {
    document.getElementById('registerModal').style.display = 'block';
    document.body.classList.add('no-scroll');
});

// With this new version:
document.querySelector('.mobile-menu-toggle').addEventListener('click', function () {
    const mobileSidebar = document.querySelector('.mobile-sidebar');
    const overlay = document.querySelector('.sidebar-overlay');

    mobileSidebar.classList.add('show');
    overlay.classList.add('active');
    document.body.classList.add('no-scroll');
    this.setAttribute('aria-expanded', 'true');
});

// Add this for closing the mobile sidebar:
document.querySelector('.close-sidebar').addEventListener('click', function () {
    const mobileSidebar = document.querySelector('.mobile-sidebar');
    const overlay = document.querySelector('.sidebar-overlay');

    mobileSidebar.classList.remove('show');
    overlay.classList.remove('active');
    document.body.classList.remove('no-scroll');
    document.querySelector('.mobile-menu-toggle').setAttribute('aria-expanded', 'false');
});

// Update the overlay click handler:
document.querySelector('.sidebar-overlay').addEventListener('click', function () {
    document.querySelector('.mobile-sidebar').classList.remove('show');
    this.classList.remove('active');
    document.body.classList.remove('no-scroll');
    document.querySelector('.mobile-menu-toggle').setAttribute('aria-expanded', 'false');
});

// Close sidebar when clicking overlay
document.querySelector('.sidebar-overlay').addEventListener('click', function () {
    document.querySelector('.sidebar').classList.remove('active');
    this.classList.remove('active');
    document.body.classList.remove('no-scroll');
    document.querySelector('.mobile-menu-toggle').setAttribute('aria-expanded', 'false');
});

// Close sidebar when clicking on a link
document.querySelectorAll('.sidebar a').forEach(link => {
    link.addEventListener('click', function () {
        document.querySelector('.sidebar').classList.remove('active');
        document.querySelector('.sidebar-overlay').classList.remove('active');
        document.body.classList.remove('no-scroll');
        document.querySelector('.mobile-menu-toggle').setAttribute('aria-expanded', 'false');
    });
});

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function () {
    // Add sidebar overlay to DOM
    const overlay = document.createElement('div');
    overlay.className = 'sidebar-overlay';
    document.body.appendChild(overlay);

    initializeGenreMultiSelect();
    setupTagInputs();
    setupPosterPreview();

    // Add event listeners to all delete buttons
    document.querySelectorAll('.btn-delete').forEach(button => {
        button.addEventListener('click', function (e) {
            e.preventDefault();
            if (confirm('Are you sure you want to change the status of this movie?')) {
                showLoading();
                // Submit the form
                this.closest('form').submit();
            }
        });
    });
});

// Show loading spinner
function showLoading() {
    const spinner = document.createElement('div');
    spinner.className = 'loading-spinner';
    spinner.innerHTML = '<div class="spinner"></div>';
    document.body.appendChild(spinner);
    spinner.style.display = 'flex';
}

// Genre multi-select functionality
function initializeGenreMultiSelect() {
    const genreSelect = document.getElementById('movieGenre');
    if (!genreSelect) return;

    const genreSelectedOptions = document.getElementById('genreSelectedOptions');
    const genreDropdownOptions = document.getElementById('genreDropdownOptions');

    // Create dropdown options
    Array.from(genreSelect.options).forEach(option => {
        const optionItem = document.createElement('div');
        optionItem.className = 'option-item';
        optionItem.textContent = option.textContent;
        optionItem.dataset.value = option.value;

        if (option.selected) {
            optionItem.classList.add('selected');
        }

        optionItem.addEventListener('click', function (e) {
            e.stopPropagation();
            option.selected = !option.selected;
            optionItem.classList.toggle('selected', option.selected);
            updateSelectedGenres();
        });

        genreDropdownOptions.appendChild(optionItem);
    });

    function updateSelectedGenres() {
        genreSelectedOptions.innerHTML = '';
        const selectedOptions = Array.from(genreSelect.selectedOptions);

        if (selectedOptions.length === 0) {
            genreSelectedOptions.innerHTML = '<span class="placeholder">Select genres...</span>';
            return;
        }

        selectedOptions.forEach(option => {
            const tag = document.createElement('div');
            tag.className = 'genre-tag';
            tag.innerHTML = `
                ${option.textContent}
                <span class="remove-tag" data-value="${option.value}">&times;</span>
            `;
            genreSelectedOptions.appendChild(tag);
        });

        // Add event listeners to remove buttons
        document.querySelectorAll('.genre-tag .remove-tag').forEach(btn => {
            btn.addEventListener('click', function (e) {
                e.stopPropagation();
                const value = this.dataset.value;
                const option = Array.from(genreSelect.options).find(opt => opt.value === value);
                if (option) {
                    option.selected = false;
                    updateSelectedGenres();
                    const dropdownOption = Array.from(genreDropdownOptions.children)
                        .find(opt => opt.dataset.value === value);
                    if (dropdownOption) {
                        dropdownOption.classList.remove('selected');
                    }
                }
            });
        });
    }

    // Toggle dropdown
    document.querySelector('.multi-select-container').addEventListener('click', function (e) {
        e.stopPropagation();
        genreDropdownOptions.classList.toggle('show');
    });

    // Initialize
    updateSelectedGenres();
}

// Tag inputs for directors and cast
function setupTagInputs() {
    setupTagInput('movieDirector', 'directorTags');
    setupTagInput('movieCastInput', 'castTags', 'movieCast');
}

function setupTagInput(inputId, tagsContainerId, hiddenInputId = null) {
    const container = document.getElementById(tagsContainerId);
    if (!container) return;

    let input = container.querySelector('.tag-input');
    if (!input) {
        input = document.createElement('input');
        input.type = 'text';
        input.className = 'tag-input';
        input.placeholder = inputId === 'movieDirector' ?
            'Add director and press Enter' : 'Add cast member and press Enter';
        container.appendChild(input);
    }

    input.addEventListener('keydown', function (e) {
        if ((e.key === 'Enter' || e.key === ',') && this.value.trim()) {
            e.preventDefault();
            addTag(this.value.trim(), container, hiddenInputId);
            this.value = '';
        } else if (e.key === 'Backspace' && this.value === '') {
            const tags = container.querySelectorAll('.tag');
            if (tags.length > 0) {
                const lastTag = tags[tags.length - 1];
                if (hiddenInputId) {
                    updateHiddenInput(hiddenInputId, lastTag.textContent.trim().replace('×', ''), true);
                }
                lastTag.remove();
            }
        }
    });

    // Initialize with existing tags if any
    const existingTags = container.querySelectorAll('.tag');
    existingTags.forEach(tag => {
        tag.querySelector('.remove-tag').addEventListener('click', function () {
            if (hiddenInputId) {
                updateHiddenInput(hiddenInputId, tag.textContent.trim().replace('×', ''), true);
            }
            tag.remove();
        });
    });
}

function addTag(value, container, hiddenInputId = null) {
    if (!value) return;

    const tag = document.createElement('div');
    tag.className = 'tag';
    tag.innerHTML = `
        ${value}
        <span class="remove-tag">&times;</span>
    `;

    const removeBtn = tag.querySelector('.remove-tag');
    removeBtn.addEventListener('click', function () {
        if (hiddenInputId) {
            updateHiddenInput(hiddenInputId, value, true);
        }
        tag.remove();
    });

    const input = container.querySelector('.tag-input');
    container.insertBefore(tag, input);

    if (hiddenInputId) {
        updateHiddenInput(hiddenInputId, value);
    }
}

function updateHiddenInput(hiddenInputId, value, remove = false) {
    const hiddenInput = document.getElementById(hiddenInputId);
    if (!hiddenInput) return;

    let currentValues = hiddenInput.value ? hiddenInput.value.split(',') : [];

    if (remove) {
        currentValues = currentValues.filter(v => v.trim() !== value.trim());
    } else {
        currentValues.push(value.trim());
    }

    hiddenInput.value = currentValues.join(',');
}

// Poster preview functionality
function setupPosterPreview() {
    const posterInput = document.getElementById('moviePoster');
    if (!posterInput) return;

    const posterPreview = document.getElementById('posterPreview');

    posterInput.addEventListener('input', function () {
        const url = this.value.trim();
        if (url) {
            const img = document.createElement('img');
            img.src = url;
            img.alt = 'Poster Preview';
            img.onerror = function () {
                posterPreview.innerHTML = '<span>Invalid image URL</span>';
            };
            img.onload = function () {
                posterPreview.innerHTML = '';
                posterPreview.appendChild(img);
            };
            posterPreview.innerHTML = 'Loading...';
        } else {
            posterPreview.innerHTML = '';
        }
    });
}

// Screening management
function addScreening() {
    const container = document.getElementById('screeningContainer');
    const newScreening = document.createElement('div');
    newScreening.className = 'screening-entry';
    newScreening.innerHTML = `
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
    container.appendChild(newScreening);
}

function removeScreening(button) {
    const container = document.getElementById('screeningContainer');
    if (container.children.length > 1) {
        button.closest('.screening-entry').remove();
    } else {
        alert("At least one screening is required.");
    }
}

// Handle form submission with fetch
document.getElementById('addMovieForm')?.addEventListener('submit', function (e) {
    e.preventDefault();
    showLoading();

    // Submit the form normally (you can replace this with fetch API if needed)
    this.submit();
});