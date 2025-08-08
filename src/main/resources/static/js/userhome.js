document.addEventListener('DOMContentLoaded', function () {
            // Only enable dropdown on desktop
            if (window.innerWidth > 767) {
                const userMenu = document.querySelector('.user-menu');
                if (userMenu) {
                    userMenu.addEventListener('click', function(e) {
                        e.stopPropagation();
                        const dropdown = this.querySelector('.dropdown-menu');
                        dropdown.style.opacity = dropdown.style.opacity === '1' ? '0' : '1';
                        dropdown.style.visibility = dropdown.style.visibility === 'visible' ? 'hidden' : 'visible';
                    });
                }

                // Close dropdown when clicking outside
                document.addEventListener('click', function() {
                    const dropdown = document.querySelector('.dropdown-menu');
                    if (dropdown) {
                        dropdown.style.opacity = '0';
                        dropdown.style.visibility = 'hidden';
                    }
                });
            }
        });
document.addEventListener('DOMContentLoaded', function () {
    // Get the modal and buttons
    const modal = document.getElementById('profileModal');
    const profileBtn = document.getElementById('profileBtn');
    const closeBtn = document.querySelector('.profile-close');
    const logoutBtn = document.querySelector('.logout-btn');


    // When the user clicks on (x), close the modal
    closeBtn.addEventListener('click', function () {
        modal.style.display = 'none';
    });

    // When the user clicks anywhere outside the modal, close it
    window.addEventListener('click', function (event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });

    // Logout button functionality
    logoutBtn.addEventListener('click', function () {
        // Perform logout actions here
        window.location.href = '/logout'; // Replace with your logout endpoint
    });

    // Example function to fetch user data (you'll need to implement the backend)
    function fetchUserData() {
        fetch('/api/user/profile') // Replace with your actual endpoint
            .then(response => response.json())
            .then(data => {
                document.getElementById('userName').textContent = data.name;
                document.getElementById('userEmail').textContent = data.email;
                document.getElementById('userMobile').textContent = data.mobile;
                document.getElementById('memberSince').textContent = data.joinDate;
                if (data.profileImage) {
                    document.getElementById('profileImage').src = data.profileImage;
                }
            })
            .catch(error => console.error('Error fetching user data:', error));
    }

    // Change photo button functionality
    document.querySelector('.change-photo-btn').addEventListener('click', function () {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'image/*';
        input.onchange = e => {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function (event) {
                    document.getElementById('profileImage').src = event.target.result;
                    // Here you would typically upload the image to your server
                };
                reader.readAsDataURL(file);
            }
        };
        input.click();
    });
});
