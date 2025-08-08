 // Enhanced Mobile Sidebar Functionality
        const mobileMenuBtn = document.getElementById('mobileMenuBtn');
        const mobileSidebar = document.getElementById('mobileSidebar');
        const closeSidebar = document.getElementById('closeSidebar');
        const overlay = document.getElementById('overlay');
        const fabBtn = document.getElementById('fabBtn');
        const mainHeader = document.getElementById('mainHeader');
        const searchInput = document.getElementById('searchInput');
        const searchBtn = document.getElementById('searchBtn');

        // Set current year in footer
        document.getElementById('currentYear').textContent = new Date().getFullYear();

        // Toggle sidebar
        function toggleSidebar() {
            mobileSidebar.classList.toggle('show');
            overlay.classList.toggle('show');
            document.body.classList.toggle('no-scroll');
        }

        // Event listeners
        mobileMenuBtn.addEventListener('click', toggleSidebar);
        closeSidebar.addEventListener('click', toggleSidebar);
        overlay.addEventListener('click', toggleSidebar);

        // Close sidebar when clicking on a link
        document.querySelectorAll('.mobile-sidebar a').forEach(link => {
            link.addEventListener('click', () => {
                if (window.innerWidth <= 992) {
                    toggleSidebar();
                }
            });
        });

        // FAB button click handler
        fabBtn.addEventListener('click', function () {
            alert('Create new content or action');
            // You can replace this with opening a modal or other action
        });


        // Remove these old search event listeners:
        // searchBtn.addEventListener('click', function() {...});
        // searchInput.addEventListener('keypress', function(e) {...});

        // Search functionality
        document.getElementById('searchBtn').addEventListener('click', function (e) {
            const searchTerm = document.getElementById('searchInput').value.trim();
            if (!searchTerm) {
                e.preventDefault(); // Prevent form submission if search is empty
                window.location.href = '/admin-home'; // Clear any existing search
            }
        });

        document.getElementById('searchInput').addEventListener('keypress', function (e) {
            if (e.key === 'Enter') {
                const searchTerm = this.value.trim();
                if (!searchTerm) {
                    e.preventDefault();
                    window.location.href = '/admin-home'; // Clear any existing search
                }
            }
        });

        // Optional: Clear search when clicking the 'x' icon in the search input
        document.getElementById('searchInput').addEventListener('input', function (e) {
            if (this.value === '') {
                window.location.href = '/admin-home';
            }
        });


        // Header scroll effect
        window.addEventListener('scroll', function () {
            if (window.scrollY > 10) {
                mainHeader.classList.add('scrolled-header');
            } else {
                mainHeader.classList.remove('scrolled-header');
            }
        });

        // Responsive button text
        function updateButtonText() {
            const buttons = document.querySelectorAll('.btn-text, .action-text');
            if (window.innerWidth < 576) {
                buttons.forEach(btn => {
                    btn.style.display = 'none';
                });
            } else {
                buttons.forEach(btn => {
                    btn.style.display = 'inline';
                });
            }
        }

        // Initial check
        updateButtonText();

        // Update on resize
        window.addEventListener('resize', updateButtonText);

        // Modal functionality
        document.querySelectorAll('.close').forEach(button => {
            button.addEventListener('click', function () {
                this.closest('.modal').style.display = 'none';
            });
        });

        window.addEventListener('click', function (event) {
            if (event.target.className === 'modal') {
                event.target.style.display = 'none';
            }
        });

        // Prevent body scroll when sidebar is open
        document.body.classList.remove('no-scroll');

        // Add hover effect to cards dynamically
        document.querySelectorAll('.card').forEach((card, index) => {
            card.addEventListener('mouseenter', function () {
                this.style.transform = 'translateY(-5px)';
                this.style.boxShadow = '0 10px 25px rgba(0, 0, 0, 0.1)';
            });

            card.addEventListener('mouseleave', function () {
                this.style.transform = '';
                this.style.boxShadow = '';
            });

            // Add delay animation
            this.style.animationDelay = `${index * 0.1}s`;
        });

        // Add animation to table rows
        document.querySelectorAll('tbody tr').forEach((row, index) => {
            row.style.opacity = '0';
            row.style.animation = `fadeIn 0.5s ease-out ${index * 0.1}s forwards`;
        });

        document.querySelectorAll('.delete-btn, .activate-btn').forEach(btn => {
            btn.addEventListener('click', function (e) {
                e.preventDefault();
                const url = this.getAttribute('href');
                fetch(url, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
                    }
                }).then(response => {
                    if (response.ok) {
                        window.location.reload();
                    } else {
                        alert('Error changing status');
                    }
                }).catch(error => {
                    console.error('Error:', error);
                    alert('Error changing status');
                });
            });
        });