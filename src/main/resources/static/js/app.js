/**
 * ============================================================================
 * Student Management System - Main Application Logic (Vanilla JS)
 * ============================================================================
 * 
 * Features:
 * - Single Page Navigation (Dashboard, Students, Courses, Enrollments)
 * - Complete CRUD for Students and Courses
 * - Course Enrollment & Capacity Management
 * - Dynamic Dashboard metrics, analytics bars, and live occupancy calculations
 * - Real-time table searching, filtering, and tab categorization
 * - Native HTML5 <dialog> modal forms with input validation
 * - Toast notifications, empty states & loading indicators
 * - Client-side Export to CSV for all registries
 * - Dark & Light mode theme persistence
 * - Dual Mode support: Live Spring Boot Backend & Standalone Demo (Mock) Mode
 * ============================================================================
 */

// Global Application State (In-Memory Cache)
const AppState = {
    currentSection: 'dashboard',
    students: [],
    courses: [],
    registrations: [],
    activeDeleteAction: null, // Holds callback for confirmed deletion
    activeTypeFilter: '',     // Filter by Undergraduate / Postgraduate
    theme: localStorage.getItem('sms_theme') || 'light'
};

/* ============================================================================
   1. DOM Element Selectors
   ============================================================================ */
const elements = {
    // Navigation & Layout
    sidebar: document.getElementById('sidebar'),
    sidebarToggleBtn: document.getElementById('sidebar-toggle-btn'),
    navItems: document.querySelectorAll('.nav-item'),
    sections: document.querySelectorAll('.page-section'),
    sectionTitle: document.getElementById('current-section-title'),
    sectionSubtitle: document.getElementById('current-section-subtitle'),
    refreshBtn: document.getElementById('btn-refresh-data'),
    themeToggleBtn: document.getElementById('theme-toggle-btn'),

    // Sidebar Counters
    navCountStudents: document.getElementById('nav-count-students'),
    navCountCourses: document.getElementById('nav-count-courses'),
    navCountEnrollments: document.getElementById('nav-count-enrollments'),

    // Backend / Mock Mode controls
    mockToggle: document.getElementById('mock-mode-toggle'),
    mockLabel: document.getElementById('mock-toggle-label'),
    modeBanner: document.getElementById('mode-banner'),
    statusDot: document.getElementById('status-dot'),
    statusText: document.getElementById('backend-status-text'),

    // Dashboard Hero & Metrics
    heroBtnAddStudent: document.getElementById('hero-btn-add-student'),
    heroBtnEnroll: document.getElementById('hero-btn-enroll'),
    dashStudents: document.getElementById('dash-total-students'),
    dashCourses: document.getElementById('dash-total-courses'),
    dashEnrollments: document.getElementById('dash-total-enrollments'),
    dashStudentSplit: document.getElementById('dash-student-split'),
    dashCourseCapacityTotal: document.getElementById('dash-course-capacity-total'),
    dashOccupancyRate: document.getElementById('dash-occupancy-rate'),
    dashStudentsTrend: document.getElementById('dash-students-trend'),

    // Dashboard Analytics Widgets
    deptProgressBar: document.getElementById('dept-progress-bar'),
    deptLegendGrid: document.getElementById('dept-legend-grid'),
    capacityPercentageBadge: document.getElementById('capacity-percentage-badge'),
    capacityMeterFill: document.getElementById('capacity-meter-fill'),
    capacityEnrolledLabel: document.getElementById('capacity-enrolled-label'),
    capacityAvailableLabel: document.getElementById('capacity-available-label'),

    // Dashboard Recent Students
    recentStudentsTbody: document.getElementById('recent-students-tbody'),
    recentStudentsEmpty: document.getElementById('recent-students-empty'),
    dashBtnViewAllStudents: document.getElementById('dash-btn-view-all-students'),

    // Students Section
    studentsTbody: document.getElementById('students-tbody'),
    studentsEmpty: document.getElementById('students-empty'),
    studentsLoading: document.getElementById('students-loading'),
    studentSearch: document.getElementById('student-search-input'),
    studentSearchClear: document.getElementById('student-search-clear'),
    studentDeptFilter: document.getElementById('student-department-filter'),
    studentTypeFilter: document.getElementById('student-type-filter'),
    filterTabBtns: document.querySelectorAll('.filter-tab-btn'),
    studentFilterCount: document.getElementById('student-filter-count'),
    openAddStudentBtn: document.getElementById('btn-open-add-student'),
    btnExportStudents: document.getElementById('btn-export-students'),

    // Student Modal & Form
    studentModal: document.getElementById('student-modal'),
    studentForm: document.getElementById('student-form'),
    studentModalTitle: document.getElementById('student-modal-title'),
    studentModalClose: document.getElementById('student-modal-close'),
    studentModalCancel: document.getElementById('student-modal-cancel'),
    studentRecordId: document.getElementById('student-record-id'),
    studentFieldId: document.getElementById('student-field-id'),
    studentFieldName: document.getElementById('student-field-name'),
    studentFieldEmail: document.getElementById('student-field-email'),
    studentFieldPhone: document.getElementById('student-field-phone'),
    studentFieldDept: document.getElementById('student-field-department'),
    studentFieldSem: document.getElementById('student-field-semester'),

    // Courses Section
    coursesTbody: document.getElementById('courses-tbody'),
    coursesEmpty: document.getElementById('courses-empty'),
    coursesLoading: document.getElementById('courses-loading'),
    courseSearch: document.getElementById('course-search-input'),
    courseSearchClear: document.getElementById('course-search-clear'),
    coursesSummaryPill: document.getElementById('courses-summary-pill'),
    openAddCourseBtn: document.getElementById('btn-open-add-course'),
    btnExportCourses: document.getElementById('btn-export-courses'),

    // Course Modal & Form
    courseModal: document.getElementById('course-modal'),
    courseForm: document.getElementById('course-form'),
    courseModalTitle: document.getElementById('course-modal-title'),
    courseModalClose: document.getElementById('course-modal-close'),
    courseModalCancel: document.getElementById('course-modal-cancel'),
    courseRecordId: document.getElementById('course-record-id'),
    courseFieldCode: document.getElementById('course-field-code'),
    courseFieldName: document.getElementById('course-field-name'),
    courseFieldCredits: document.getElementById('course-field-credits'),
    courseFieldCapacity: document.getElementById('course-field-capacity'),

    // Enrollments Section
    enrollForm: document.getElementById('enrollment-form'),
    enrollStudentSelect: document.getElementById('enroll-student-select'),
    enrollCourseSelect: document.getElementById('enroll-course-select'),
    enrollmentsTbody: document.getElementById('enrollments-tbody'),
    enrollmentsEmpty: document.getElementById('enrollments-empty'),
    enrollmentsLoading: document.getElementById('enrollments-loading'),
    enrollmentSearch: document.getElementById('enrollment-search-input'),
    enrollmentSearchClear: document.getElementById('enrollment-search-clear'),
    enrollmentsCountPill: document.getElementById('enrollments-count-pill'),
    btnExportEnrollments: document.getElementById('btn-export-enrollments'),

    // Confirmation Modal
    confirmModal: document.getElementById('confirm-modal'),
    confirmModalClose: document.getElementById('confirm-modal-close'),
    confirmModalCancel: document.getElementById('confirm-modal-cancel'),
    confirmModalProceed: document.getElementById('confirm-modal-proceed'),
    confirmModalMessage: document.getElementById('confirm-modal-message'),
    confirmModalDetails: document.getElementById('confirm-modal-details'),

    // Toasts
    toastContainer: document.getElementById('toast-container')
};

/* ============================================================================
   2. Theme Switcher (Dark / Light Mode)
   ============================================================================ */
function applyTheme(theme) {
    AppState.theme = theme;
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('sms_theme', theme);
}

function toggleTheme() {
    const nextTheme = AppState.theme === 'dark' ? 'light' : 'dark';
    applyTheme(nextTheme);
    showToast(`Switched to ${nextTheme === 'dark' ? 'Dark' : 'Light'} Mode`, 'info');
}

/* ============================================================================
   3. Visual Helpers: Deterministic Avatars & Colors
   ============================================================================ */
const AVATAR_GRADIENTS = [
    'linear-gradient(135deg, #2563eb, #4f46e5)', // Blue/Indigo
    'linear-gradient(135deg, #059669, #0d9488)', // Emerald/Teal
    'linear-gradient(135deg, #7c3aed, #9333ea)', // Purple/Violet
    'linear-gradient(135deg, #d97706, #ea580c)', // Amber/Orange
    'linear-gradient(135deg, #db2777, #e11d48)', // Pink/Rose
    'linear-gradient(135deg, #0284c7, #2563eb)'  // Sky/Blue
];

const DEPARTMENT_COLORS = {
    'Computer Science': '#3b82f6',
    'Information Technology': '#06b6d4',
    'Electronics & Communication': '#8b5cf6',
    'Mechanical Engineering': '#f59e0b',
    'Civil Engineering': '#10b981'
};

function getInitials(name) {
    if (!name) return 'ST';
    const parts = name.trim().split(/\s+/);
    if (parts.length >= 2) {
        return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.slice(0, 2).toUpperCase();
}

function getAvatarGradient(name) {
    if (!name) return AVATAR_GRADIENTS[0];
    let hash = 0;
    for (let i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    const index = Math.abs(hash) % AVATAR_GRADIENTS.length;
    return AVATAR_GRADIENTS[index];
}

// XSS Prevention: Safe HTML escaping
function escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

/* ============================================================================
   4. Toast Notification Utility
   ============================================================================ */
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    let iconSvg = '';
    if (type === 'success') {
        iconSvg = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#10b981" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>`;
    } else if (type === 'error') {
        iconSvg = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>`;
    } else {
        iconSvg = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>`;
    }

    toast.innerHTML = `
        ${iconSvg}
        <span class="toast-message">${escapeHtml(message)}</span>
        <button class="toast-close" aria-label="Close notification">&times;</button>
    `;

    elements.toastContainer.appendChild(toast);

    toast.querySelector('.toast-close').addEventListener('click', () => {
        toast.remove();
    });

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        setTimeout(() => toast.remove(), 250);
    }, 4000);
}

/* ============================================================================
   5. Client-Side Export to CSV Utility
   ============================================================================ */
function downloadCSV(filename, csvContent) {
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
    showToast(`Downloaded "${filename}" successfully.`);
}

function exportStudentsToCSV() {
    if (AppState.students.length === 0) {
        showToast('No student records to export.', 'warning');
        return;
    }
    const headers = ['Student ID', 'Full Name', 'Email', 'Phone Number', 'Department', 'Semester', 'Student Type'];
    const rows = AppState.students.map(s => [
        `"${s.studentId || s.id}"`,
        `"${s.fullName || ''}"`,
        `"${s.email || ''}"`,
        `"${s.phoneNumber || ''}"`,
        `"${s.department || ''}"`,
        `"${s.semester || ''}"`,
        `"${s.studentType || ''}"`
    ]);
    const csvContent = [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
    downloadCSV('students_directory.csv', csvContent);
}

function exportCoursesToCSV() {
    if (AppState.courses.length === 0) {
        showToast('No courses to export.', 'warning');
        return;
    }
    const headers = ['Course Code', 'Course Name', 'Credits', 'Max Capacity', 'Enrolled Students'];
    const rows = AppState.courses.map(c => {
        const enrolled = AppState.registrations.filter(r => r.courseCode === c.courseCode).length;
        return [
            `"${c.courseCode}"`,
            `"${c.courseName}"`,
            c.credits,
            c.maxCapacity,
            enrolled
        ];
    });
    const csvContent = [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
    downloadCSV('courses_catalog.csv', csvContent);
}

function exportEnrollmentsToCSV() {
    if (AppState.registrations.length === 0) {
        showToast('No enrollments to export.', 'warning');
        return;
    }
    const headers = ['Student ID', 'Student Name', 'Course Code', 'Course Name'];
    const rows = AppState.registrations.map(r => [
        `"${r.student ? r.student.studentId : r.studentId}"`,
        `"${r.student ? r.student.fullName : r.studentName}"`,
        `"${r.course ? r.course.courseCode : r.courseCode}"`,
        `"${r.course ? r.course.courseName : r.courseName}"`
    ]);
    const csvContent = [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
    downloadCSV('course_enrollments.csv', csvContent);
}

/* ============================================================================
   6. Navigation & Section Switching
   ============================================================================ */
const sectionTitles = {
    dashboard: {
        title: 'Dashboard',
        subtitle: 'Overview of college administration metrics & analytics'
    },
    students: {
        title: 'Student Directory',
        subtitle: 'Manage student enrollment records and academic credentials'
    },
    courses: {
        title: 'Course Catalog',
        subtitle: 'Manage curriculum courses, credits, and classroom seat limits'
    },
    enrollments: {
        title: 'Course Enrollment',
        subtitle: 'Assign registered students to academic course sections'
    }
};

function navigateToSection(sectionId) {
    if (!sectionTitles[sectionId]) return;

    AppState.currentSection = sectionId;

    elements.navItems.forEach(item => {
        if (item.getAttribute('data-section') === sectionId) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });

    elements.sections.forEach(section => {
        if (section.id === `section-${sectionId}`) {
            section.classList.add('active');
        } else {
            section.classList.remove('active');
        }
    });

    elements.sectionTitle.textContent = sectionTitles[sectionId].title;
    elements.sectionSubtitle.textContent = sectionTitles[sectionId].subtitle;

    if (window.innerWidth <= 900) {
        elements.sidebar.classList.remove('open');
    }
}

/* ============================================================================
   7. Data Loading & Refresh
   ============================================================================ */
async function loadAllData() {
    try {
        elements.studentsLoading.style.display = 'flex';
        elements.coursesLoading.style.display = 'flex';
        elements.enrollmentsLoading.style.display = 'flex';

        const [students, courses, registrations] = await Promise.all([
            StudentAPI.getAll(),
            CourseAPI.getAll(),
            RegistrationAPI.getAll()
        ]);

        AppState.students = Array.isArray(students) ? students : [];
        AppState.courses = Array.isArray(courses) ? courses : [];
        AppState.registrations = Array.isArray(registrations) ? registrations : [];

        // Update live sidebar counters
        updateSidebarCounters();

        // Render UI sections
        renderDashboard();
        renderStudentsTable();
        renderCoursesTable();
        renderEnrollmentsSection();

        updateBackendStatusUI(true);
    } catch (error) {
        console.error('Error loading application data:', error);
        showToast(error.message || 'Failed to load data from server.', 'error');
        updateBackendStatusUI(false, error.message);
    } finally {
        elements.studentsLoading.style.display = 'none';
        elements.coursesLoading.style.display = 'none';
        elements.enrollmentsLoading.style.display = 'none';
    }
}

function updateSidebarCounters() {
    elements.navCountStudents.textContent = AppState.students.length;
    elements.navCountCourses.textContent = AppState.courses.length;
    elements.navCountEnrollments.textContent = AppState.registrations.length;
}

/* ============================================================================
   8. Dashboard Section Rendering
   ============================================================================ */
function renderDashboard() {
    const totalStudents = AppState.students.length;
    const totalCourses = AppState.courses.length;
    const totalEnrollments = AppState.registrations.length;

    // 1. Metric numbers
    elements.dashStudents.textContent = totalStudents;
    elements.dashCourses.textContent = totalCourses;
    elements.dashEnrollments.textContent = totalEnrollments;

    // 2. Undergrad vs Postgrad split
    const undergradCount = AppState.students.filter(s => s.studentType === 'Undergraduate').length;
    const postgradCount = totalStudents - undergradCount;
    elements.dashStudentSplit.textContent = `${undergradCount} Undergrad • ${postgradCount} Postgrad`;

    // 3. Overall Course capacity
    const totalCapacity = AppState.courses.reduce((sum, c) => sum + (Number(c.maxCapacity) || 0), 0);
    elements.dashCourseCapacityTotal.textContent = `Total Capacity: ${totalCapacity} Seats`;

    // 4. Occupancy Rate
    const occupancyPct = totalCapacity > 0 ? Math.round((totalEnrollments / totalCapacity) * 100) : 0;
    elements.dashOccupancyRate.textContent = `${occupancyPct}% Occupancy`;
    elements.capacityPercentageBadge.textContent = `${occupancyPct}% Filled`;
    elements.capacityMeterFill.style.width = `${Math.min(occupancyPct, 100)}%`;
    elements.capacityEnrolledLabel.textContent = `${totalEnrollments} Enrolled`;
    elements.capacityAvailableLabel.textContent = `${Math.max(totalCapacity - totalEnrollments, 0)} Seats Available`;

    // 5. Department Breakdown Visualization
    renderDepartmentDistribution();

    // 6. Recently Added Students (latest 5 records)
    renderRecentStudents();
}

function renderDepartmentDistribution() {
    const deptCounts = {};
    AppState.students.forEach(s => {
        const d = s.department || 'Other';
        deptCounts[d] = (deptCounts[d] || 0) + 1;
    });

    const total = AppState.students.length;
    elements.deptProgressBar.innerHTML = '';
    elements.deptLegendGrid.innerHTML = '';

    if (total === 0) {
        elements.deptProgressBar.innerHTML = '<div style="width: 100%; height: 100%; background: var(--bg-muted);"></div>';
        elements.deptLegendGrid.innerHTML = '<span style="font-size: 12px; color: var(--text-muted);">No student data available.</span>';
        return;
    }

    Object.keys(deptCounts).forEach(dept => {
        const count = deptCounts[dept];
        const pct = Math.round((count / total) * 100);
        const color = DEPARTMENT_COLORS[dept] || '#64748b';

        // Progress segment
        const seg = document.createElement('div');
        seg.className = 'dept-progress-segment';
        seg.style.width = `${pct}%`;
        seg.style.backgroundColor = color;
        seg.title = `${dept}: ${count} (${pct}%)`;
        elements.deptProgressBar.appendChild(seg);

        // Legend item
        const leg = document.createElement('div');
        leg.className = 'dept-legend-item';
        leg.innerHTML = `
            <span class="dept-legend-dot" style="background-color: ${color};"></span>
            <span class="dept-legend-label">${escapeHtml(dept)}</span>
            <span class="dept-legend-val">${count}</span>
        `;
        elements.deptLegendGrid.appendChild(leg);
    });
}

function renderRecentStudents() {
    const recent = [...AppState.students].slice(0, 5);
    elements.recentStudentsTbody.innerHTML = '';

    if (recent.length === 0) {
        elements.recentStudentsEmpty.style.display = 'block';
    } else {
        elements.recentStudentsEmpty.style.display = 'none';
        recent.forEach(student => {
            const tr = document.createElement('tr');
            const initials = getInitials(student.fullName);
            const avatarGrad = getAvatarGradient(student.fullName);
            const typeBadgeClass = student.studentType === 'Postgraduate' ? 'badge-postgraduate' : 'badge-undergraduate';

            tr.innerHTML = `
                <td>
                    <div class="student-avatar-cell">
                        <div class="avatar-circle" style="background: ${avatarGrad};">${initials}</div>
                        <div class="student-meta-info">
                            <span class="student-name-text">${escapeHtml(student.fullName)}</span>
                            <span class="student-id-subtext">${escapeHtml(student.studentId || student.id)}</span>
                        </div>
                    </div>
                </td>
                <td><span class="badge badge-dept">${escapeHtml(student.department)}</span></td>
                <td>Semester ${escapeHtml(student.semester)}</td>
                <td><span class="badge ${typeBadgeClass}">${escapeHtml(student.studentType)}</span></td>
            `;
            elements.recentStudentsTbody.appendChild(tr);
        });
    }
}

/* ============================================================================
   9. Student Management Section
   ============================================================================ */
function renderStudentsTable() {
    const searchTerm = elements.studentSearch.value.trim().toLowerCase();
    const deptFilter = elements.studentDeptFilter.value;
    const typeFilter = AppState.activeTypeFilter;

    // Toggle clear search button visibility
    elements.studentSearchClear.style.display = searchTerm ? 'block' : 'none';

    const filtered = AppState.students.filter(student => {
        const idMatch = (student.studentId || '').toLowerCase().includes(searchTerm);
        const nameMatch = (student.fullName || '').toLowerCase().includes(searchTerm);
        const emailMatch = (student.email || '').toLowerCase().includes(searchTerm);
        const searchMatches = !searchTerm || idMatch || nameMatch || emailMatch;

        const deptMatches = !deptFilter || student.department === deptFilter;
        const typeMatches = !typeFilter || student.studentType === typeFilter;

        return searchMatches && deptMatches && typeMatches;
    });

    elements.studentFilterCount.textContent = `Showing ${filtered.length} of ${AppState.students.length} students`;
    elements.studentsTbody.innerHTML = '';

    if (filtered.length === 0) {
        elements.studentsEmpty.style.display = 'block';
    } else {
        elements.studentsEmpty.style.display = 'none';
        filtered.forEach(student => {
            const tr = document.createElement('tr');
            const initials = getInitials(student.fullName);
            const avatarGrad = getAvatarGradient(student.fullName);
            const typeBadgeClass = student.studentType === 'Postgraduate' ? 'badge-postgraduate' : 'badge-undergraduate';

            tr.innerHTML = `
                <td>
                    <div class="student-avatar-cell">
                        <div class="avatar-circle" style="background: ${avatarGrad};">${initials}</div>
                        <div class="student-meta-info">
                            <span class="student-name-text">${escapeHtml(student.fullName)}</span>
                            <span class="student-id-subtext">${escapeHtml(student.studentId || student.id)}</span>
                        </div>
                    </div>
                </td>
                <td><span class="badge badge-dept">${escapeHtml(student.department)}</span></td>
                <td>Semester ${escapeHtml(student.semester)}</td>
                <td><span class="badge ${typeBadgeClass}">${escapeHtml(student.studentType)}</span></td>
                <td>
                    <div class="contact-stack">
                        <span class="contact-item">
                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
                            ${escapeHtml(student.email)}
                        </span>
                        <span class="contact-item">
                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/></svg>
                            ${escapeHtml(student.phoneNumber)}
                        </span>
                    </div>
                </td>
                <td>
                    <div class="actions-cell">
                        <button class="btn btn-outline btn-sm btn-edit-student" data-id="${student.id || student.studentId}" title="Edit student">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                            <span>Edit</span>
                        </button>
                        <button class="btn btn-danger-outline btn-sm btn-delete-student" data-id="${student.id || student.studentId}" data-name="${escapeHtml(student.fullName)}" title="Delete student">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                            <span>Delete</span>
                        </button>
                    </div>
                </td>
            `;
            elements.studentsTbody.appendChild(tr);
        });

        elements.studentsTbody.querySelectorAll('.btn-edit-student').forEach(btn => {
            btn.addEventListener('click', () => openEditStudentModal(btn.getAttribute('data-id')));
        });

        elements.studentsTbody.querySelectorAll('.btn-delete-student').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.getAttribute('data-id');
                const name = btn.getAttribute('data-name');
                confirmDeleteStudent(id, name);
            });
        });
    }
}

function openAddStudentModal() {
    resetStudentForm();
    elements.studentModalTitle.textContent = 'Add New Student';
    elements.studentRecordId.value = '';
    elements.studentFieldId.disabled = false;
    elements.studentModal.showModal();
}

function openEditStudentModal(studentId) {
    resetStudentForm();
    const student = AppState.students.find(s => String(s.id) === String(studentId) || s.studentId === String(studentId));
    if (!student) return;

    elements.studentModalTitle.textContent = 'Edit Student Details';
    elements.studentRecordId.value = student.id || student.studentId;
    elements.studentFieldId.value = student.studentId || student.id;
    elements.studentFieldId.disabled = true;
    elements.studentFieldName.value = student.fullName || '';
    elements.studentFieldEmail.value = student.email || '';
    elements.studentFieldPhone.value = student.phoneNumber || '';
    elements.studentFieldDept.value = student.department || '';
    elements.studentFieldSem.value = student.semester || '';

    const typeRadios = elements.studentForm.querySelectorAll('input[name="student-type"]');
    typeRadios.forEach(r => {
        r.checked = (r.value === student.studentType);
    });

    elements.studentModal.showModal();
}

function resetStudentForm() {
    elements.studentForm.reset();
    elements.studentForm.querySelectorAll('.form-input, .form-select').forEach(input => {
        input.classList.remove('is-invalid');
    });
    elements.studentForm.querySelectorAll('.form-error').forEach(err => {
        err.classList.remove('active');
        err.textContent = '';
    });
}

function validateStudentForm() {
    let isValid = true;
    const id = elements.studentFieldId.value.trim();
    const name = elements.studentFieldName.value.trim();
    const email = elements.studentFieldEmail.value.trim();
    const phone = elements.studentFieldPhone.value.trim();
    const dept = elements.studentFieldDept.value;
    const sem = elements.studentFieldSem.value.trim();

    function setError(inputElem, errorElemId, msg) {
        inputElem.classList.add('is-invalid');
        const errDiv = document.getElementById(errorElemId);
        if (errDiv) {
            errDiv.textContent = msg;
            errDiv.classList.add('active');
        }
        isValid = false;
    }

    function clearError(inputElem, errorElemId) {
        inputElem.classList.remove('is-invalid');
        const errDiv = document.getElementById(errorElemId);
        if (errDiv) {
            errDiv.textContent = '';
            errDiv.classList.remove('active');
        }
    }

    if (!id) setError(elements.studentFieldId, 'student-error-id', 'Student ID is required.');
    else clearError(elements.studentFieldId, 'student-error-id');

    if (!name) setError(elements.studentFieldName, 'student-error-name', 'Full Name is required.');
    else clearError(elements.studentFieldName, 'student-error-name');

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email) setError(elements.studentFieldEmail, 'student-error-email', 'Email address is required.');
    else if (!emailRegex.test(email)) setError(elements.studentFieldEmail, 'student-error-email', 'Please enter a valid email address.');
    else clearError(elements.studentFieldEmail, 'student-error-email');

    if (!phone) setError(elements.studentFieldPhone, 'student-error-phone', 'Phone number is required.');
    else if (!/^\d{7,15}$/.test(phone.replace(/[-+() ]/g, ''))) setError(elements.studentFieldPhone, 'student-error-phone', 'Please enter a valid phone number (digits only).');
    else clearError(elements.studentFieldPhone, 'student-error-phone');

    if (!dept) setError(elements.studentFieldDept, 'student-error-department', 'Please select a department.');
    else clearError(elements.studentFieldDept, 'student-error-department');

    const semNum = Number(sem);
    if (!sem || isNaN(semNum) || semNum < 1 || semNum > 8) setError(elements.studentFieldSem, 'student-error-semester', 'Semester must be between 1 and 8.');
    else clearError(elements.studentFieldSem, 'student-error-semester');

    return isValid;
}

async function handleStudentFormSubmit(e) {
    e.preventDefault();
    if (!validateStudentForm()) return;

    const recordId = elements.studentRecordId.value;
    const isEdit = Boolean(recordId);

    const typeRadio = elements.studentForm.querySelector('input[name="student-type"]:checked');
    const studentData = {
        studentId: elements.studentFieldId.value.trim(),
        fullName: elements.studentFieldName.value.trim(),
        email: elements.studentFieldEmail.value.trim(),
        phoneNumber: elements.studentFieldPhone.value.trim(),
        department: elements.studentFieldDept.value,
        semester: Number(elements.studentFieldSem.value),
        studentType: typeRadio ? typeRadio.value : 'Undergraduate'
    };

    try {
        if (isEdit) {
            await StudentAPI.update(recordId, studentData);
            showToast(`Student "${studentData.fullName}" updated successfully.`);
        } else {
            await StudentAPI.create(studentData);
            showToast(`Student "${studentData.fullName}" added successfully.`);
        }

        elements.studentModal.close();
        await loadAllData();
    } catch (err) {
        showToast(err.message || 'Operation failed.', 'error');
    }
}

function confirmDeleteStudent(id, name) {
    openConfirmModal(
        'Delete Student Record',
        `Are you sure you want to delete this student? All course enrollments for this student will also be removed.`,
        `Student: ${name} (ID: ${id})`,
        async () => {
            try {
                await StudentAPI.delete(id);
                showToast(`Student "${name}" deleted successfully.`);
                await loadAllData();
            } catch (err) {
                showToast(err.message || 'Failed to delete student.', 'error');
            }
        }
    );
}

/* ============================================================================
   10. Course Management Section
   ============================================================================ */
function renderCoursesTable() {
    const searchTerm = elements.courseSearch.value.trim().toLowerCase();
    elements.courseSearchClear.style.display = searchTerm ? 'block' : 'none';

    const filtered = AppState.courses.filter(course => {
        const codeMatch = (course.courseCode || '').toLowerCase().includes(searchTerm);
        const nameMatch = (course.courseName || '').toLowerCase().includes(searchTerm);
        return !searchTerm || codeMatch || nameMatch;
    });

    elements.coursesSummaryPill.textContent = `${filtered.length} of ${AppState.courses.length} Courses`;
    elements.coursesTbody.innerHTML = '';

    if (filtered.length === 0) {
        elements.coursesEmpty.style.display = 'block';
    } else {
        elements.coursesEmpty.style.display = 'none';
        filtered.forEach(course => {
            const tr = document.createElement('tr');
            const enrolledCount = AppState.registrations.filter(r => r.courseCode === course.courseCode).length;
            const capacity = Number(course.maxCapacity) || 1;
            const pct = Math.min(Math.round((enrolledCount / capacity) * 100), 100);
            
            let fillClass = '';
            if (pct >= 100) fillClass = 'danger';
            else if (pct >= 75) fillClass = 'warning';

            tr.innerHTML = `
                <td><strong>${escapeHtml(course.courseCode)}</strong></td>
                <td>
                    <div style="font-weight: 700; color: var(--text-primary);">${escapeHtml(course.courseName)}</div>
                </td>
                <td><span class="badge badge-credits">${escapeHtml(course.credits)} Credits</span></td>
                <td>
                    <div style="font-size: 12.5px; font-weight: 600; color: var(--text-primary); display: flex; justify-content: space-between;">
                        <span>${enrolledCount} / ${escapeHtml(course.maxCapacity)} Seats</span>
                        <span style="color: var(--text-muted);">${pct}%</span>
                    </div>
                    <div class="course-seat-track">
                        <div class="course-seat-fill ${fillClass}" style="width: ${pct}%;"></div>
                    </div>
                </td>
                <td>
                    <div class="actions-cell">
                        <button class="btn btn-outline btn-sm btn-edit-course" data-id="${course.id || course.courseCode}" title="Edit course">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                            <span>Edit</span>
                        </button>
                        <button class="btn btn-danger-outline btn-sm btn-delete-course" data-id="${course.id || course.courseCode}" data-name="${escapeHtml(course.courseName)}" title="Delete course">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                            <span>Delete</span>
                        </button>
                    </div>
                </td>
            `;
            elements.coursesTbody.appendChild(tr);
        });

        elements.coursesTbody.querySelectorAll('.btn-edit-course').forEach(btn => {
            btn.addEventListener('click', () => openEditCourseModal(btn.getAttribute('data-id')));
        });

        elements.coursesTbody.querySelectorAll('.btn-delete-course').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.getAttribute('data-id');
                const name = btn.getAttribute('data-name');
                confirmDeleteCourse(id, name);
            });
        });
    }
}

function openAddCourseModal() {
    resetCourseForm();
    elements.courseModalTitle.textContent = 'Add New Course';
    elements.courseRecordId.value = '';
    elements.courseFieldCode.disabled = false;
    elements.courseModal.showModal();
}

function openEditCourseModal(courseId) {
    resetCourseForm();
    const course = AppState.courses.find(c => String(c.id) === String(courseId) || c.courseCode === String(courseId));
    if (!course) return;

    elements.courseModalTitle.textContent = 'Edit Course Details';
    elements.courseRecordId.value = course.id || course.courseCode;
    elements.courseFieldCode.value = course.courseCode;
    elements.courseFieldCode.disabled = true;
    elements.courseFieldName.value = course.courseName;
    elements.courseFieldCredits.value = course.credits;
    elements.courseFieldCapacity.value = course.maxCapacity;

    elements.courseModal.showModal();
}

function resetCourseForm() {
    elements.courseForm.reset();
    elements.courseForm.querySelectorAll('.form-input').forEach(i => i.classList.remove('is-invalid'));
    elements.courseForm.querySelectorAll('.form-error').forEach(e => {
        e.classList.remove('active');
        e.textContent = '';
    });
}

function validateCourseForm() {
    let isValid = true;
    const code = elements.courseFieldCode.value.trim();
    const name = elements.courseFieldName.value.trim();
    const credits = Number(elements.courseFieldCredits.value);
    const capacity = Number(elements.courseFieldCapacity.value);

    function setError(inputElem, errorElemId, msg) {
        inputElem.classList.add('is-invalid');
        const errDiv = document.getElementById(errorElemId);
        if (errDiv) {
            errDiv.textContent = msg;
            errDiv.classList.add('active');
        }
        isValid = false;
    }

    function clearError(inputElem, errorElemId) {
        inputElem.classList.remove('is-invalid');
        const errDiv = document.getElementById(errorElemId);
        if (errDiv) {
            errDiv.textContent = '';
            errDiv.classList.remove('active');
        }
    }

    if (!code) setError(elements.courseFieldCode, 'course-error-code', 'Course Code is required.');
    else clearError(elements.courseFieldCode, 'course-error-code');

    if (!name) setError(elements.courseFieldName, 'course-error-name', 'Course Name is required.');
    else clearError(elements.courseFieldName, 'course-error-name');

    if (!credits || credits < 1 || credits > 6) setError(elements.courseFieldCredits, 'course-error-credits', 'Credits must be between 1 and 6.');
    else clearError(elements.courseFieldCredits, 'course-error-credits');

    if (!capacity || capacity < 1) setError(elements.courseFieldCapacity, 'course-error-capacity', 'Maximum capacity must be at least 1.');
    else clearError(elements.courseFieldCapacity, 'course-error-capacity');

    return isValid;
}

async function handleCourseFormSubmit(e) {
    e.preventDefault();
    if (!validateCourseForm()) return;

    const recordId = elements.courseRecordId.value;
    const isEdit = Boolean(recordId);

    const courseData = {
        courseCode: elements.courseFieldCode.value.trim().toUpperCase(),
        courseName: elements.courseFieldName.value.trim(),
        credits: Number(elements.courseFieldCredits.value),
        maxCapacity: Number(elements.courseFieldCapacity.value)
    };

    try {
        if (isEdit) {
            await CourseAPI.update(recordId, courseData);
            showToast(`Course "${courseData.courseCode}" updated successfully.`);
        } else {
            await CourseAPI.create(courseData);
            showToast(`Course "${courseData.courseCode}" created successfully.`);
        }

        elements.courseModal.close();
        await loadAllData();
    } catch (err) {
        showToast(err.message || 'Operation failed.', 'error');
    }
}

function confirmDeleteCourse(id, name) {
    openConfirmModal(
        'Delete Course',
        `Are you sure you want to remove this course? Any existing student enrollments in this course will also be cancelled.`,
        `Course: ${name} (${id})`,
        async () => {
            try {
                await CourseAPI.delete(id);
                showToast(`Course "${id}" deleted successfully.`);
                await loadAllData();
            } catch (err) {
                showToast(err.message || 'Failed to delete course.', 'error');
            }
        }
    );
}

/* ============================================================================
   11. Course Enrollment Section
   ============================================================================ */
function renderEnrollmentsSection() {
    const selectedStudent = elements.enrollStudentSelect.value;
    elements.enrollStudentSelect.innerHTML = '<option value="">-- Choose Student --</option>';
    AppState.students.forEach(student => {
        const option = document.createElement('option');
        option.value = student.studentId || student.id;
        option.textContent = `${student.studentId || student.id} - ${student.fullName} (${student.department})`;
        elements.enrollStudentSelect.appendChild(option);
    });
    if (selectedStudent) elements.enrollStudentSelect.value = selectedStudent;

    const selectedCourse = elements.enrollCourseSelect.value;
    elements.enrollCourseSelect.innerHTML = '<option value="">-- Choose Course --</option>';
    AppState.courses.forEach(course => {
        const enrolledCount = AppState.registrations.filter(r => r.courseCode === course.courseCode).length;
        const availableSeats = course.maxCapacity - enrolledCount;

        const option = document.createElement('option');
        option.value = course.courseCode;
        option.textContent = `${course.courseCode} - ${course.courseName} (${availableSeats > 0 ? availableSeats + ' seats left' : 'FULL'})`;
        if (availableSeats <= 0) {
            option.disabled = true;
        }
        elements.enrollCourseSelect.appendChild(option);
    });
    if (selectedCourse) elements.enrollCourseSelect.value = selectedCourse;

    renderEnrollmentsTable();
}

function renderEnrollmentsTable() {
    const searchTerm = elements.enrollmentSearch.value.trim().toLowerCase();
    elements.enrollmentSearchClear.style.display = searchTerm ? 'block' : 'none';

    const filtered = AppState.registrations.filter(reg => {
        const studentId = (reg.student ? reg.student.studentId : reg.studentId) || '';
        const studentName = (reg.student ? reg.student.fullName : reg.studentName) || '';
        const courseCode = (reg.course ? reg.course.courseCode : reg.courseCode) || '';
        const courseName = (reg.course ? reg.course.courseName : reg.courseName) || '';

        const text = `${studentId} ${studentName} ${courseCode} ${courseName}`.toLowerCase();
        return !searchTerm || text.includes(searchTerm);
    });

    elements.enrollmentsCountPill.textContent = `${filtered.length} of ${AppState.registrations.length} Enrollments`;
    elements.enrollmentsTbody.innerHTML = '';

    if (filtered.length === 0) {
        elements.enrollmentsEmpty.style.display = 'block';
    } else {
        elements.enrollmentsEmpty.style.display = 'none';
        filtered.forEach(reg => {
            const tr = document.createElement('tr');
            const studentId = (reg.student ? reg.student.studentId : reg.studentId) || 'N/A';
            const studentName = (reg.student ? reg.student.fullName : reg.studentName) || 'N/A';
            const courseCode = (reg.course ? reg.course.courseCode : reg.courseCode) || 'N/A';
            const courseName = (reg.course ? reg.course.courseName : reg.courseName) || 'N/A';

            const initials = getInitials(studentName);
            const avatarGrad = getAvatarGradient(studentName);

            tr.innerHTML = `
                <td>
                    <div class="student-avatar-cell">
                        <div class="avatar-circle" style="background: ${avatarGrad};">${initials}</div>
                        <div class="student-meta-info">
                            <span class="student-name-text">${escapeHtml(studentName)}</span>
                            <span class="student-id-subtext">${escapeHtml(studentId)}</span>
                        </div>
                    </div>
                </td>
                <td><strong>${escapeHtml(courseCode)}</strong></td>
                <td>${escapeHtml(courseName)}</td>
                <td>
                    <button class="btn btn-danger-outline btn-sm btn-delete-enrollment" data-id="${reg.id}" data-info="${escapeHtml(studentName)} - ${escapeHtml(courseCode)}" title="Cancel enrollment">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                        <span>Drop</span>
                    </button>
                </td>
            `;
            elements.enrollmentsTbody.appendChild(tr);
        });

        elements.enrollmentsTbody.querySelectorAll('.btn-delete-enrollment').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.getAttribute('data-id');
                const info = btn.getAttribute('data-info');
                confirmDeleteEnrollment(id, info);
            });
        });
    }
}

async function handleEnrollmentSubmit(e) {
    e.preventDefault();
    const studentId = elements.enrollStudentSelect.value;
    const courseCode = elements.enrollCourseSelect.value;

    if (!studentId || !courseCode) {
        showToast('Please select both a student and a course.', 'warning');
        return;
    }

    try {
        await RegistrationAPI.create({
            studentId,
            courseCode
        });

        showToast(`Successfully enrolled student in ${courseCode}!`);
        elements.enrollStudentSelect.value = '';
        elements.enrollCourseSelect.value = '';
        await loadAllData();
    } catch (err) {
        showToast(err.message || 'Enrollment failed.', 'error');
    }
}

function confirmDeleteEnrollment(id, info) {
    openConfirmModal(
        'Cancel Course Enrollment',
        `Are you sure you want to drop this student's course enrollment?`,
        `Enrollment: ${info}`,
        async () => {
            try {
                await RegistrationAPI.delete(id);
                showToast('Enrollment removed successfully.');
                await loadAllData();
            } catch (err) {
                showToast(err.message || 'Failed to remove enrollment.', 'error');
            }
        }
    );
}

/* ============================================================================
   12. Universal Delete Confirmation Modal
   ============================================================================ */
function openConfirmModal(title, message, details, onConfirmCallback) {
    document.getElementById('confirm-modal-title').textContent = title;
    elements.confirmModalMessage.textContent = message;
    elements.confirmModalDetails.textContent = details || '';
    AppState.activeDeleteAction = onConfirmCallback;
    elements.confirmModal.showModal();
}

function handleConfirmModalProceed() {
    if (typeof AppState.activeDeleteAction === 'function') {
        const action = AppState.activeDeleteAction;
        AppState.activeDeleteAction = null;
        elements.confirmModal.close();
        action();
    } else {
        elements.confirmModal.close();
    }
}

/* ============================================================================
   13. Backend Status & Mock Mode UI Synchronization
   ============================================================================ */
function updateBackendStatusUI(isSuccess, errorMsg = '') {
    const isMock = ApiConfig.isMockMode();

    if (isMock) {
        elements.statusDot.className = 'status-indicator-dot mock';
        elements.statusText.textContent = 'Simulated Data (Demo Mode)';
        elements.mockLabel.textContent = 'Demo (Mock) Mode';
        elements.modeBanner.classList.remove('hidden');
    } else {
        elements.modeBanner.classList.add('hidden');
        if (isSuccess) {
            elements.statusDot.className = 'status-indicator-dot online';
            elements.statusText.textContent = 'Connected (Spring Boot :8080)';
            elements.mockLabel.textContent = 'Live Backend';
        } else {
            elements.statusDot.className = 'status-indicator-dot offline';
            elements.statusText.textContent = 'Backend Offline';
            elements.mockLabel.textContent = 'Live Backend (Offline)';
        }
    }
}

function handleMockModeToggle(e) {
    const useMock = e.target.checked;
    ApiConfig.setMockMode(useMock);
    updateBackendStatusUI(true);

    if (useMock) {
        showToast('Switched to Demo / Mock Mode with local sample data.', 'info');
    } else {
        showToast('Connecting to Live Spring Boot Backend at /api...', 'info');
    }

    loadAllData();
}

/* ============================================================================
   14. Event Listeners Initialization
   ============================================================================ */
function initializeEventListeners() {
    // 1. Navigation clicks
    elements.navItems.forEach(btn => {
        btn.addEventListener('click', () => {
            const section = btn.getAttribute('data-section');
            navigateToSection(section);
        });
    });

    // 2. Mobile sidebar toggle
    elements.sidebarToggleBtn.addEventListener('click', () => {
        elements.sidebar.classList.toggle('open');
    });

    document.addEventListener('click', (e) => {
        if (window.innerWidth <= 900 && 
            !elements.sidebar.contains(e.target) && 
            !elements.sidebarToggleBtn.contains(e.target) &&
            elements.sidebar.classList.contains('open')) {
            elements.sidebar.classList.remove('open');
        }
    });

    // 3. Top Header Theme Toggle & Refresh
    elements.themeToggleBtn.addEventListener('click', toggleTheme);

    elements.refreshBtn.addEventListener('click', () => {
        showToast('Refreshing application data...', 'info');
        loadAllData();
    });

    // 4. Mock Mode Toggle Switch
    elements.mockToggle.addEventListener('change', handleMockModeToggle);

    // 5. Dashboard Hero Actions
    elements.heroBtnAddStudent.addEventListener('click', () => {
        navigateToSection('students');
        openAddStudentModal();
    });

    elements.heroBtnEnroll.addEventListener('click', () => {
        navigateToSection('enrollments');
    });

    elements.dashBtnViewAllStudents.addEventListener('click', () => {
        navigateToSection('students');
    });

    // 6. Student Quick Filter Tabs
    elements.filterTabBtns.forEach(tab => {
        tab.addEventListener('click', () => {
            elements.filterTabBtns.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            AppState.activeTypeFilter = tab.getAttribute('data-type-filter');
            renderStudentsTable();
        });
    });

    // Student real-time filter inputs & clear button
    elements.studentSearch.addEventListener('input', renderStudentsTable);
    elements.studentSearchClear.addEventListener('click', () => {
        elements.studentSearch.value = '';
        renderStudentsTable();
        elements.studentSearch.focus();
    });
    elements.studentDeptFilter.addEventListener('change', renderStudentsTable);

    // Export to CSV buttons
    elements.btnExportStudents.addEventListener('click', exportStudentsToCSV);
    elements.btnExportCourses.addEventListener('click', exportCoursesToCSV);
    elements.btnExportEnrollments.addEventListener('click', exportEnrollmentsToCSV);

    // Student modal events
    elements.openAddStudentBtn.addEventListener('click', openAddStudentModal);
    elements.studentForm.addEventListener('submit', handleStudentFormSubmit);
    elements.studentModalClose.addEventListener('click', () => elements.studentModal.close());
    elements.studentModalCancel.addEventListener('click', () => elements.studentModal.close());

    // 7. Course Management Events
    elements.openAddCourseBtn.addEventListener('click', openAddCourseModal);
    elements.courseForm.addEventListener('submit', handleCourseFormSubmit);
    elements.courseModalClose.addEventListener('click', () => elements.courseModal.close());
    elements.courseModalCancel.addEventListener('click', () => elements.courseModal.close());

    elements.courseSearch.addEventListener('input', renderCoursesTable);
    elements.courseSearchClear.addEventListener('click', () => {
        elements.courseSearch.value = '';
        renderCoursesTable();
        elements.courseSearch.focus();
    });

    // 8. Enrollment Form Events
    elements.enrollForm.addEventListener('submit', handleEnrollmentSubmit);
    elements.enrollmentSearch.addEventListener('input', renderEnrollmentsTable);
    elements.enrollmentSearchClear.addEventListener('click', () => {
        elements.enrollmentSearch.value = '';
        renderEnrollmentsTable();
        elements.enrollmentSearch.focus();
    });

    // 9. Confirm Modal Events
    elements.confirmModalProceed.addEventListener('click', handleConfirmModalProceed);
    elements.confirmModalCancel.addEventListener('click', () => elements.confirmModal.close());
    elements.confirmModalClose.addEventListener('click', () => elements.confirmModal.close());
}

/* ============================================================================
   15. Application Bootstrap
   ============================================================================ */
document.addEventListener('DOMContentLoaded', async () => {
    // 1. Initialize theme
    applyTheme(AppState.theme);

    // 2. Setup event listeners
    initializeEventListeners();

    // 3. Probe if live Spring Boot backend is already running
    const isLiveServerAvailable = await checkBackendHealth();
    if (isLiveServerAvailable) {
        ApiConfig.setMockMode(false);
        elements.mockToggle.checked = false;
        console.log('Live Spring Boot backend detected on localhost:8080');
    } else {
        ApiConfig.setMockMode(true);
        elements.mockToggle.checked = true;
        console.log('Spring Boot backend not detected yet; starting in Demo / Mock mode');
    }

    // 4. Load all data
    await loadAllData();
});
